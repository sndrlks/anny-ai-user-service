package com.annyai.common.exception;

import jakarta.validation.ConstraintViolationException;

import com.annyai.common.dto.ApiErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(Environment env) {
        super(env);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ExceptionUtils.logSafeError(log, "Unhandled exception", ex, isDevOrTest());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse
                        .of(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ExceptionUtils.getSafeMessage(ex, isDevOrTest()))
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ExceptionUtils.logSafeError(log, "Payload validation error", ex, isDevOrTest());

        List<String> errors = ex.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        return ResponseEntity
                .badRequest()
                .body(ApiErrorResponse
                        .of(HttpStatus.BAD_REQUEST, "Payload validation error", errors)
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintException(ConstraintViolationException ex) {
        ExceptionUtils.logSafeError(log, "Validation error", ex, isDevOrTest());

        List<String> details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ApiErrorResponse
                        .of(HttpStatus.BAD_REQUEST, "Validation failed", details)
                );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityException(DataIntegrityViolationException ex) {
        ExceptionUtils.logSafeError(log, "Data integrity violation", ex, isDevOrTest());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse
                        .of(HttpStatus.BAD_REQUEST, "Invalid request data", ex.getMessage())
                );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionException(TransactionSystemException ex) {
        ExceptionUtils.logSafeError(log, "Transaction commit failed", ex, isDevOrTest());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse
                        .of(HttpStatus.BAD_REQUEST, "Transaction failed", ex.getMessage()));
    }
}



