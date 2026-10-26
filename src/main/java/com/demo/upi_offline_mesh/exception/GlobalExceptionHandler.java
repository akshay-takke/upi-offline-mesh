package com.demo.upi_offline_mesh.exception;

import com.demo.upi_offline_mesh.dto.response.APIErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIErrorResponse> handleResponseStatusException(
            ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        APIErrorResponse response = new APIErrorResponse(
                status,
                ex.getStatusCode().toString(),
                ex.getReason(),
                Instant.now()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }
}
