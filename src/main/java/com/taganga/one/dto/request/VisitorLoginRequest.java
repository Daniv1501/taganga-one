package com.taganga.one.dto.request;

import com.taganga.one.domain.DocumentType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VisitorLoginRequest(

        @NotNull(message = "Elige el tipo de documento.")
        DocumentType documentType,

        @NotBlank(message = "Documento no válido.")
        String documentNumber

) {

    @AssertTrue(message = "Documento no válido.")
    public boolean isDocumentNumberValid() {
        return documentType == null
                || documentNumber == null
                || documentNumber.isBlank()
                || documentType.accepts(documentNumber.trim());
    }
}
