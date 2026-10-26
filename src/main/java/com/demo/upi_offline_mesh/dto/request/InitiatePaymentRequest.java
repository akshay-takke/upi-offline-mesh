package com.demo.upi_offline_mesh.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InitiatePaymentRequest(

        @NotNull
        @Positive
        Long senderAccountId,

        @NotNull
        @Positive
        Long receiverAccountId,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount
) {
}
