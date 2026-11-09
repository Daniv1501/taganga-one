package com.taganga.one.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskAssistantRequest(

        @NotBlank(message = "Escribe tu pregunta.")
        @Size(max = 300, message = "La pregunta admite máximo 300 caracteres.")
        String question,

        // Opcional: primer nombre del visitante, solo para personalizar el saludo.
        @Size(max = 150, message = "El nombre admite máximo 150 caracteres.")
        String visitorName

) {
}
