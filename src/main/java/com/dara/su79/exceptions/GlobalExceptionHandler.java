package com.dara.su79.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.swagger.v3.oas.annotations.Hidden;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
        @ExceptionHandler(MyResourceNotFoundException.class)
        public ResponseEntity<ErrorMessage> resourceNotFoundException(MyResourceNotFoundException ex,
                        WebRequest request) {
                ErrorMessage message = new ErrorMessage(
                                HttpStatus.NOT_FOUND.value(),
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex,
                        WebRequest request) {
                ErrorMessage message = new ErrorMessage(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<ErrorMessage>(message,
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
}