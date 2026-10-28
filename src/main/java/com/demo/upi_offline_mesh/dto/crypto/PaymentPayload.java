package com.demo.upi_offline_mesh.dto.crypto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentPayload(
        UUID instructionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        Instant createdAt,
        Instant expiresAt
) {
}
