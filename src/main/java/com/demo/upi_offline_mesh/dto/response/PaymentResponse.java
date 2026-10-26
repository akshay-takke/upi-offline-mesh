package com.demo.upi_offline_mesh.dto.response;

import com.demo.upi_offline_mesh.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID instructionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt,
        Instant expiresAt
) {
}
