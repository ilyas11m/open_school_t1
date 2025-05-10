package com.project.app.open_school_t1.exception.handler;

import com.project.app.open_school_t1.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> taskNotFoundHandler(TaskNotFoundException exception) {
        Map<String, String> response = Map.of("message", exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
