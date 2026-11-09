package com.taganga.one.dto.response;

import com.taganga.one.domain.VisitReason;

import java.time.LocalDate;

/**
 * Solo lo que la app necesita mostrar: el correo y el número de documento
 * nunca salen del servidor.
 */
public record VisitorResponse(
        String visitorCode,
        String fullName,
        int groupSize,
        LocalDate visitDate,
        VisitReason reason
) {
}
