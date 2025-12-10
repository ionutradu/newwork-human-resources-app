package com.newwork.human_resources_app.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Incorrect credentials.");
    }

}