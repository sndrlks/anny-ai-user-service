package com.annyai.user.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class UserException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> details;

    protected UserException(String message, HttpStatus status, String... details) {
        super(message);
        this.status = status;
        this.details = List.of(details);
    }

    protected UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.details = List.of();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getDetails() {
        return details;
    }
}

