package com.annyai.user.exception;

import com.annyai.common.dto.ApiErrorResponse;
import com.annyai.common.exception.BaseExceptionHandler;
import com.annyai.common.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler extends BaseExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(UserExceptionHandler.class);

    public UserExceptionHandler(Environment env) {
        super(env);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiErrorResponse> handleUserExceptions(UserException ex) {
        ExceptionUtils.logSafeError(log, ex.getMessage(), ex, isDevOrTest());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiErrorResponse
                        .of(ex.getStatus(), ex.getMessage(), ex.getDetails()));
    }
}