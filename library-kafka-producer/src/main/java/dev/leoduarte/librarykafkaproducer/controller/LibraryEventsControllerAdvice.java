package dev.leoduarte.librarykafkaproducer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class LibraryEventsControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleRequestBody(MethodArgumentNotValidException ex) {

        List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
        String errorMessage = errorList.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
        log.info("errorMessage : {} ", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
