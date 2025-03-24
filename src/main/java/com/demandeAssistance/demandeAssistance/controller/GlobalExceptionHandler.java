package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.exception.SousPartenaireNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SousPartenaireNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSousPartenaireNotFound(SousPartenaireNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DossierAssistanceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDossierNotFound(DossierAssistanceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }
}
