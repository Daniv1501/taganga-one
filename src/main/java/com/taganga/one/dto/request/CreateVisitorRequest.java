package com.taganga.one.dto.request;

import com.taganga.one.domain.DocumentType;
import com.taganga.one.domain.VisitReason;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateVisitorRequest(

        @NotBlank(message = "Escribe tu nombre completo.")
        @Size(min = 5, max = 150, message = "Escribe tu nombre completo.")
        @Pattern(regexp = "\\s*\\S+(\\s+\\S+)+\\s*", message = "Escribe tu nombre completo.")
        String fullName,

        @NotNull(message = "Elige el tipo de documento.")
        DocumentType documentType,

        @NotBlank(message = "Documento no válido.")
        String documentNumber,

        @NotBlank(message = "Indica tu procedencia.")
        @Size(min = 2, max = 120, message = "Indica tu procedencia.")
        String origin,

        @NotNull(message = "Entre 1 y 50.")
        @Min(value = 1, message = "Entre 1 y 50.")
        @Max(value = 50, message = "Entre 1 y 50.")
        Integer groupSize,

        @NotBlank(message = "Escribe un correo válido.")
        @Size(max = 254, message = "Escribe un correo válido.")
        @Pattern(regexp = "[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}", message = "Escribe un correo válido.")
        String email,

        @NotNull(message = "Elige una fecha.")
        LocalDate visitDate,

        @NotNull(message = "Elige el motivo de la visita.")
        VisitReason reason,

        @NotNull(message = "Debes aceptar la autorización de tratamiento de datos para continuar.")
        @AssertTrue(message = "Debes aceptar la autorización de tratamiento de datos para continuar.")
        Boolean dataConsentAccepted

) {

    @AssertTrue(message = "Documento no válido.")
    public boolean isDocumentNumberValid() {
        return documentType == null
                || documentNumber == null
                || documentNumber.isBlank()
                || documentType.accepts(documentNumber.trim());
    }
}
