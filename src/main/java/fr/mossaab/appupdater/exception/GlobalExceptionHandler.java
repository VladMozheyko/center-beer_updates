package fr.mossaab.appupdater.exception;

import fr.mossaab.appupdater.dto.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                sb.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ")
        );
        return ResponseEntity.badRequest().body(new ErrorInfo("VALIDATION_ERROR", sb.toString()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorInfo> handleBindException(BindException ex) {
        return ResponseEntity.badRequest().body(new ErrorInfo("BIND_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ErrorInfo("CONSTRAINT_VIOLATION", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleAnyException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorInfo("INTERNAL_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorInfo> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(new ErrorInfo("CONSTRAINT_VIOLATION", ex.getMessage()));
    }

}
