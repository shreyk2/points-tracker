package com.fetch.points_tracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid transaction exceptions.
     *
     * @param ex The InvalidTransactionException to handle.
     * @return A response entity containing an error message and status.
     */
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransaction(InvalidTransactionException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Transaction");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles insufficient points exceptions.
     *
     * @param ex The InsufficientPointsException to handle.
     * @return A response entity containing an error message and status.
     */
    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientPoints(InsufficientPointsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "User has insufficient points");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles general exceptions.
     *
     * @param ex The Exception to handle.
     * @return A response entity containing an error message and status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Action");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
