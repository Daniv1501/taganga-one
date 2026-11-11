package com.taganga.one.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VisitorLoginRequest(

        @NotBlank(message = "Escribe tu correo.")
        @Size(max = 254, message = "Escribe un correo válido.")
        String email,

        @NotBlank(message = "Escribe tu contraseña.")
        @Size(max = 72, message = "Contraseña no válida.")
        String password

) {
}
