package com.taganga.one.exception;

/** Correo o contraseña incorrectos. Nunca dice cuál de los dos falló. */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
