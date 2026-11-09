package com.taganga.one.controller;

import com.taganga.one.domain.VisitReason;
import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;
import com.taganga.one.exception.BusinessRuleException;
import com.taganga.one.exception.GlobalExceptionHandler;
import com.taganga.one.exception.ResourceNotFoundException;
import com.taganga.one.service.VisitorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitorController.class)
@Import(GlobalExceptionHandler.class)
class VisitorControllerTest {

    private static final String VALID_BODY = """
            {
              "fullName": "María Pérez Gómez",
              "documentType": "CC",
              "documentNumber": "1234567890",
              "origin": "Bogotá",
              "groupSize": 2,
              "email": "maria@example.com",
              "visitDate": "2026-12-01",
              "reason": "TOURISM",
              "dataConsentAccepted": true
            }
            """;

    private static final VisitorResponse RESPONSE = new VisitorResponse(
            "TG-1000", "María Pérez Gómez", 2, LocalDate.of(2026, 12, 1), VisitReason.TOURISM);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitorService service;

    private org.springframework.test.web.servlet.ResultActions postVisitor(String body)
            throws Exception {
        return mockMvc.perform(post("/api/visitors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void shouldRegisterVisitorWithoutExposingPersonalData() throws Exception {
        when(service.register(any(CreateVisitorRequest.class))).thenReturn(RESPONSE);

        postVisitor(VALID_BODY)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitorCode").value("TG-1000"))
                .andExpect(jsonPath("$.fullName").value("María Pérez Gómez"))
                .andExpect(jsonPath("$.groupSize").value(2))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.documentNumber").doesNotExist());
    }

    @Test
    void shouldReturn400WithDetailsWhenRequestIsEmpty() throws Exception {
        postVisitor("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Revisa los datos enviados."))
                .andExpect(jsonPath("$.details.fullName").exists())
                .andExpect(jsonPath("$.details.documentType").exists())
                .andExpect(jsonPath("$.details.documentNumber").exists())
                .andExpect(jsonPath("$.details.origin").exists())
                .andExpect(jsonPath("$.details.groupSize").exists())
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.visitDate").exists())
                .andExpect(jsonPath("$.details.reason").exists())
                .andExpect(jsonPath("$.details.dataConsentAccepted").exists());

        verify(service, never()).register(any());
    }

    @Test
    void shouldRequireDataConsent() throws Exception {
        postVisitor(VALID_BODY.replace("\"dataConsentAccepted\": true", "\"dataConsentAccepted\": false"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.dataConsentAccepted").value(
                        "Debes aceptar la autorización de tratamiento de datos para continuar."));

        verify(service, never()).register(any());
    }

    @Test
    void shouldRequireFullNameWithAtLeastTwoWords() throws Exception {
        postVisitor(VALID_BODY.replace("María Pérez Gómez", "Mariana"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.fullName").value("Escribe tu nombre completo."));
    }

    @Test
    void shouldRejectGroupOutOfRange() throws Exception {
        postVisitor(VALID_BODY.replace("\"groupSize\": 2", "\"groupSize\": 51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.groupSize").value("Entre 1 y 50."));
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        postVisitor(VALID_BODY.replace("maria@example.com", "maria@example"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").value("Escribe un correo válido."));
    }

    @Test
    void shouldRejectLettersInNumericDocument() throws Exception {
        postVisitor(VALID_BODY.replace("1234567890", "AB12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.documentNumberValid").value("Documento no válido."));
    }

    @Test
    void shouldAcceptLettersInPassport() throws Exception {
        when(service.register(any(CreateVisitorRequest.class))).thenReturn(RESPONSE);

        postVisitor(VALID_BODY.replace("1234567890", "AB12345").replace("\"CC\"", "\"PA\""))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenEnumValueIsInvalid() throws Exception {
        postVisitor(VALID_BODY.replace("TOURISM", "MAGIC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La solicitud no tiene un formato válido."));

        verify(service, never()).register(any());
    }

    @Test
    void shouldReturn409WhenBusinessRuleIsViolated() throws Exception {
        when(service.register(any(CreateVisitorRequest.class)))
                .thenThrow(new BusinessRuleException("La fecha de visita no puede ser anterior a hoy."));

        postVisitor(VALID_BODY)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("La fecha de visita no puede ser anterior a hoy."))
                .andExpect(jsonPath("$.details").isMap());
    }

    @Test
    void shouldLogin() throws Exception {
        when(service.login(any(VisitorLoginRequest.class))).thenReturn(RESPONSE);

        mockMvc.perform(post("/api/visitors/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "documentType": "CC", "documentNumber": "1234567890" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitorCode").value("TG-1000"));
    }

    @Test
    void shouldReturn404WhenLoginDocumentDoesNotExist() throws Exception {
        when(service.login(any(VisitorLoginRequest.class)))
                .thenThrow(new ResourceNotFoundException("No encontramos ese documento. Completa tu registro."));

        mockMvc.perform(post("/api/visitors/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "documentType": "CC", "documentNumber": "99999" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No encontramos ese documento. Completa tu registro."));
    }

    @Test
    void shouldReturn405InsteadOf500ForUnsupportedMethod() throws Exception {
        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
