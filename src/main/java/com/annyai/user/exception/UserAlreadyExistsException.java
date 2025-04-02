package com.annyai.user.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
