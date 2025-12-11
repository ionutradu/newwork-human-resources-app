package com.newwork.human_resources_app.shared.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        var errors = new HashMap<String, String>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        (error) -> {
                            var fieldName = ((FieldError) error).getField();

                            var errorMessage = error.getDefaultMessage();

                            errors.put(fieldName, errorMessage);
                        });

        return errors;
    }
}
