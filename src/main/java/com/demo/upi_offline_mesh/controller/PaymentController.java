package com.demo.upi_offline_mesh.controller;

import com.demo.upi_offline_mesh.dto.request.InitiatePaymentRequest;
import com.demo.upi_offline_mesh.dto.response.PaymentResponse;
import com.demo.upi_offline_mesh.enums.PaymentStatus;
import com.demo.upi_offline_mesh.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request));
    }

    @GetMapping("/{instructionId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID instructionId) {

        return ResponseEntity.ok(paymentService.getPayment(instructionId));
    }


}
