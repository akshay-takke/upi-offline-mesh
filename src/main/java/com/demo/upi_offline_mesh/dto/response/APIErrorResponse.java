package com.demo.upi_offline_mesh.dto.response;

import java.time.Instant;

public record APIErrorResponse(

        int status,
        String error,
        String message,
        Instant timeStamp
) {
}
