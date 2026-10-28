package com.demo.upi_offline_mesh.dto.crypto;

import java.util.UUID;

public record EncryptedPaymentPacket(
        UUID packetId,
        String keyId,
        String encryptedAesKey,
        String iv,
        String encryptedPayload,
        String signature,
        String signatureKeyId
) {
}
