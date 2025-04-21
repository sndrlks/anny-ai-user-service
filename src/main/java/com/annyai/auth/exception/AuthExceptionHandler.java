package com.annyai.auth.exception;

import com.annyai.common.dto.ApiErrorResponse;
import com.annyai.common.exception.BaseExceptionHandler;
import com.annyai.common.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler extends BaseExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);

    protected AuthExceptionHandler(Environment env) {
        super(env);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> unauthorizedException(UnauthorizedException ex) {
        ExceptionUtils.logSafeError(log, "Invalid refresh token", ex, isDevOrTest());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.unauthorized(ExceptionUtils.getSafeMessage(ex, isDevOrTest())));
    }
}
