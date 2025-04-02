package com.annyai.common.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(int status,
                               String message,
                               LocalDateTime timestamp,
                               List<String> details) {

    public ApiErrorResponse(HttpStatus status, String message, List<String> details) {
        this(status.value(), message, LocalDateTime.now(), details);
    }

    public static ApiErrorResponse of(HttpStatus status, String message, List<String> details) {
        return new ApiErrorResponse(status, message, details);
    }

    public static ApiErrorResponse of(HttpStatus status, String message, String... details) {
        return new ApiErrorResponse(status, message, List.of(details));
    }

    public static ApiErrorResponse badRequest(String message, String... details) {
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST, message, List.of(details));
    }

    public static ApiErrorResponse badRequest(String... details) {
        return badRequest(HttpStatus.BAD_REQUEST.getReasonPhrase(), details);
    }

    public static ApiErrorResponse internalError(String message, String... details) {
        return new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, List.of(details));
    }
    public static ApiErrorResponse internalError(String... details) {
        return internalError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), details);
    }

}
