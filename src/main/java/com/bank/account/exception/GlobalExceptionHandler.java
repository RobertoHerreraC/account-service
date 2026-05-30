package com.bank.account.exception;

import com.bank.account.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.OffsetDateTime;
import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException exception,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                "ACCOUNT_NOT_FOUND",
                exception.getMessage(),
                exchange
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(
            CustomerNotFoundException exception,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                "CUSTOMER_NOT_FOUND",
                exception.getMessage(),
                exchange
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException exception,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                "BUSINESS_RULE_VIOLATION",
                exception.getMessage(),
                exchange
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            ServerWebExchange exchange) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        ErrorResponse error = buildError(
                "INVALID_REQUEST",
                message,
                exchange
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception exception,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                exchange
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private ErrorResponse buildError(
            String code,
            String message,
            ServerWebExchange exchange) {

        ErrorResponse error = new ErrorResponse();

        error.setCode(code);
        error.setMessage(message);
        error.setPath(exchange.getRequest().getPath().value());
        error.setTimestamp(Date.from(OffsetDateTime.now().toInstant()));

        return error;
    }
}