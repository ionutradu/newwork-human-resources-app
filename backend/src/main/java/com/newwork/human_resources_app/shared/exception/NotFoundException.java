package com.newwork.human_resources_app.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Entity not found.");
    }

    public NotFoundException(String identifier) {
        super("Entity not found by identifier %s.".formatted(identifier));
    }
}
