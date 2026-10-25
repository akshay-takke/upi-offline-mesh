package com.demo.upi_offline_mesh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank
        @Size(max = 30)
        String accountNumber,

        @NotBlank
        @Size(max = 100)
        String accountHolderName

) { }
