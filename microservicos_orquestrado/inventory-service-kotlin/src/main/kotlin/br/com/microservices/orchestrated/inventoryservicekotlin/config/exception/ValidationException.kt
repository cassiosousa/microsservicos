package br.com.microservices.orchestrated.inventoryservice.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationException(message: String): RuntimeException(message)

