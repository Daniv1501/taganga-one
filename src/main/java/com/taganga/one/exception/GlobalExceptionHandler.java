package com.taganga.one.exception;

import com.taganga.one.dto.response.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Traduce excepciones de aplicación a un contrato HTTP consistente.
 * Todos los handlers retornan ResponseEntity<ErrorResponse>.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 — recurso inexistente
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    // 409 — regla de negocio violada
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex) {

        return build(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    // 409 — dos registros simultáneos con el mismo documento chocan con el UNIQUE
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        log.warn("Data integrity violation", ex);

        return build(
            HttpStatus.CONFLICT,
            "No se pudo guardar el registro. Inténtalo de nuevo.",
            Map.of()
        );
    }

    // 400 — Bean Validation sobre @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> details =
                ex.getBindingResult()
                  .getFieldErrors()
                  .stream()
                  .collect(
                      Collectors.toMap(
                          FieldError::getField,
                          FieldError::getDefaultMessage,
                          (first, second) -> first
                      )
                  );

        return build(HttpStatus.BAD_REQUEST, "Revisa los datos enviados.", details);
    }

    // 400 — JSON mal formado o valor de enum inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex) {

        return build(
            HttpStatus.BAD_REQUEST,
            "La solicitud no tiene un formato válido.",
            Map.of("body", "Revisa la sintaxis del JSON y los valores permitidos.")
        );
    }

    // 400 — query param / path variable con tipo inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return build(
            HttpStatus.BAD_REQUEST,
            "Parámetro de la solicitud no válido.",
            Map.of(ex.getName(), "Valor no válido: " + ex.getValue())
        );
    }

    // 404 — ruta inexistente (evita que caiga en el 500 genérico)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(
            NoResourceFoundException ex) {

        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado.", Map.of());
    }

    // 405 — método HTTP no soportado (evita que caiga en el 500 genérico)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido.", Map.of());
    }

    // 500 — error inesperado. No se expone stack trace, SQL ni detalles internos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex) {

        log.error("Unexpected error", ex);

        return build(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error inesperado.",
            Map.of()
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            Map<String, String> details) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(error);
    }
}
