package com.taganga.one.service.impl;

import com.taganga.one.domain.Visitor;
import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;
import com.taganga.one.exception.BusinessRuleException;
import com.taganga.one.exception.ResourceNotFoundException;
import com.taganga.one.mapper.VisitorMapper;
import com.taganga.one.repository.VisitorRepository;
import com.taganga.one.service.VisitorService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class VisitorServiceImpl implements VisitorService {

    static final String LEGAL_BASIS = "Ley 1581 de 2012 / Decreto 1377 de 2013";

    private final VisitorRepository repository;

    private final VisitorMapper mapper;

    private final Clock clock;

    public VisitorServiceImpl(
            VisitorRepository repository,
            VisitorMapper mapper,
            Clock clock) {

        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public VisitorResponse register(CreateVisitorRequest request) {

        // Regla 1: la visita no puede ser en una fecha pasada.
        if (request.visitDate().isBefore(LocalDate.now(clock))) {
            throw new BusinessRuleException(
                "La fecha de visita no puede ser anterior a hoy."
            );
        }

        String documentNumber = request.documentNumber().trim();
        String fullName = request.fullName().trim();
        String email = request.email().trim();
        String origin = request.origin().trim();
        String visitorCode = "TG-" + repository.nextVisitorCodeNumber();

        // Regla 2: un documento solo tiene un registro; si ya existía, se reemplaza.
        Visitor visitor = repository
                .findByDocumentTypeAndDocumentNumber(request.documentType(), documentNumber)
                .map(existing -> {
                    existing.replaceRegistration(
                            visitorCode, fullName, email, origin,
                            request.groupSize(), request.visitDate(), request.reason());
                    return existing;
                })
                .orElseGet(() -> new Visitor(
                        visitorCode, fullName, request.documentType(), documentNumber,
                        email, origin, request.groupSize(),
                        request.visitDate(), request.reason()));

        // Regla 3: toda autorización de datos queda registrada como prueba (Ley 1581).
        visitor.recordConsent(LEGAL_BASIS, LocalDateTime.now(clock));

        return mapper.toResponse(repository.save(visitor));
    }

    @Override
    public VisitorResponse login(VisitorLoginRequest request) {

        return repository
                .findByDocumentTypeAndDocumentNumber(
                        request.documentType(), request.documentNumber().trim())
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No encontramos ese documento. Completa tu registro."));
    }
}
