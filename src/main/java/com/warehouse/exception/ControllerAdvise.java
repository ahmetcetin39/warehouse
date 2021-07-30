package com.warehouse.exception;

import com.warehouse.exception.custom.FileProcessException;
import com.warehouse.exception.custom.ProductNotAvailableException;
import com.warehouse.exception.custom.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

/**
 * This is the controller advisor where existing and custom exceptions are caught and wrapped in a standard way
 * 7/29/21
 *
 * @author ahmetcetin
 */
@RestControllerAdvice
@Slf4j
public class ControllerAdvise {
    @ExceptionHandler(FileProcessException.class)
    public ResponseEntity<String> handleFileProcessException(FileProcessException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler({ProductNotFoundException.class, ProductNotAvailableException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException e) {
        log.warn("No multipart file is provided!", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to provide a valid multipart file!");
    }
}
