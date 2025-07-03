package com.main.multithreaded_scanner_file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RestResponseStatusExceptionResolver {

    @ExceptionHandler(ScanCancelledException.class)
    public ResponseEntity<String> handleScanCancelledException(ScanCancelledException ex) {
        return new ResponseEntity<>("Scan cancelled: " + ex.getMessage(), HttpStatus.CONFLICT);
    }
}
