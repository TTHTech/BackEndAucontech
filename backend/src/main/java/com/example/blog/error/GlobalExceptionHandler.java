package com.example.blog.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String,Object>> handle(ResponseStatusException ex){
    Map<String,Object> body = Map.of(
        "status", ex.getStatusCode().value(),
        "error", ex.getReason()
    );
    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }
}
