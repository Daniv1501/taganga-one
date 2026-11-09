package com.taganga.one.service;

import com.taganga.one.domain.DocumentType;
import com.taganga.one.domain.VisitReason;
import com.taganga.one.domain.Visitor;
import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;
import com.taganga.one.exception.BusinessRuleException;
import com.taganga.one.exception.ResourceNotFoundException;
import com.taganga.one.mapper.VisitorMapper;
import com.taganga.one.repository.VisitorRepository;
import com.taganga.one.service.impl.VisitorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorServiceImplTest {

    // 2026-11-06 23:30 en Bogotá; en UTC ya es 7 de noviembre.
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-11-07T04:30:00Z"), ZoneId.of("America/Bogota"));

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 6);

    @Mock
    private VisitorRepository repository;

    @Mock
    private VisitorMapper mapper;

    private VisitorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VisitorServiceImpl(repository, mapper, CLOCK);
    }

    private CreateVisitorRequest request(LocalDate visitDate) {
        return new CreateVisitorRequest(
                "  María Pérez Gómez ", DocumentType.CC, " 1234567890 ", " Bogotá ", 2,
                " maria@example.com ", visitDate, VisitReason.TOURISM, true);
    }

    @Test
    void shouldRegisterNewVisitorWithSequentialCodeAndConsent() {
        VisitorResponse response = new VisitorResponse(
                "TG-1000", "María Pérez Gómez", 2, TODAY, VisitReason.TOURISM);

        when(repository.nextVisitorCodeNumber()).thenReturn(1000L);
        when(repository.findByDocumentTypeAndDocumentNumber(DocumentType.CC, "1234567890"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Visitor.class))).thenAnswer(call -> call.getArgument(0));
        when(mapper.toResponse(any(Visitor.class))).thenReturn(response);

        assertThat(service.register(request(TODAY))).isEqualTo(response);

        ArgumentCaptor<Visitor> saved = ArgumentCaptor.forClass(Visitor.class);
        verify(repository).save(saved.capture());
        Visitor visitor = saved.getValue();
        assertThat(visitor.getVisitorCode()).isEqualTo("TG-1000");
        assertThat(visitor.getFullName()).isEqualTo("María Pérez Gómez");
        assertThat(visitor.getDocumentNumber()).isEqualTo("1234567890");
        assertThat(visitor.getEmail()).isEqualTo("maria@example.com");
        assertThat(visitor.getOrigin()).isEqualTo("Bogotá");
        assertThat(visitor.getConsents()).hasSize(1);
        assertThat(visitor.getConsents().get(0).getLegalBasis())
                .isEqualTo("Ley 1581 de 2012 / Decreto 1377 de 2013");
        assertThat(visitor.getConsents().get(0).getAcceptedAt())
                .isEqualTo(LocalDateTime.of(2026, 11, 6, 23, 30));
    }

    @Test
    void shouldReplaceExistingRegistrationAndKeepPreviousConsent() {
        Visitor existing = new Visitor(
                "TG-1000", "Nombre Anterior", DocumentType.CC, "1234567890",
                "viejo@example.com", "Cali", 1, TODAY, VisitReason.OTHER);
        existing.recordConsent("Ley 1581 de 2012 / Decreto 1377 de 2013",
                LocalDateTime.of(2026, 1, 1, 8, 0));

        when(repository.nextVisitorCodeNumber()).thenReturn(1001L);
        when(repository.findByDocumentTypeAndDocumentNumber(DocumentType.CC, "1234567890"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.register(request(TODAY.plusDays(3)));

        assertThat(existing.getVisitorCode()).isEqualTo("TG-1001");
        assertThat(existing.getFullName()).isEqualTo("María Pérez Gómez");
        assertThat(existing.getEmail()).isEqualTo("maria@example.com");
        assertThat(existing.getVisitDate()).isEqualTo(TODAY.plusDays(3));
        assertThat(existing.getReason()).isEqualTo(VisitReason.TOURISM);
        assertThat(existing.getConsents()).hasSize(2);
    }

    @Test
    void shouldRejectVisitDateBeforeTodayInBogota() {
        assertThatThrownBy(() -> service.register(request(TODAY.minusDays(1))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("La fecha de visita no puede ser anterior a hoy.");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldLoginWithTrimmedDocument() {
        Visitor visitor = new Visitor(
                "TG-1000", "María Pérez", DocumentType.PA, "AB12345",
                "maria@example.com", "Lima", 1, TODAY, VisitReason.TOURISM);
        VisitorResponse response = new VisitorResponse(
                "TG-1000", "María Pérez", 1, TODAY, VisitReason.TOURISM);

        when(repository.findByDocumentTypeAndDocumentNumber(DocumentType.PA, "AB12345"))
                .thenReturn(Optional.of(visitor));
        when(mapper.toResponse(visitor)).thenReturn(response);

        assertThat(service.login(new VisitorLoginRequest(DocumentType.PA, " AB12345 ")))
                .isEqualTo(response);
    }

    @Test
    void shouldThrowWhenLoginDocumentDoesNotExist() {
        when(repository.findByDocumentTypeAndDocumentNumber(DocumentType.CC, "99999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new VisitorLoginRequest(DocumentType.CC, "99999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No encontramos ese documento. Completa tu registro.");
    }
}
