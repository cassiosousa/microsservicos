package br.com.microservices.orchestrated.inventoryservice.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ExceptionGlobalHandler {

    @ExceptionHandler
    fun handleValidationException(validationException: ValidationException): ResponseEntity<ExceptionDetails> {
        val details = ExceptionDetails(HttpStatus.BAD_REQUEST.value(), validationException.message);
        return ResponseEntity(details,HttpStatus.BAD_REQUEST);
    }
}
