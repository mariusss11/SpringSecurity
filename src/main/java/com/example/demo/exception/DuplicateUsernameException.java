package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DuplicateUsernameException extends Exception{

    public DuplicateUsernameException(String msg) {
        super(msg);
    }

    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}