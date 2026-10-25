package com.demo.upi_offline_mesh.dto.response;

import com.demo.upi_offline_mesh.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountHolderName,
        BigDecimal balance,
        AccountStatus status,
        Instant createdAt
) {
}
