package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.dto.crypto.EncryptedPaymentPacket;
import com.demo.upi_offline_mesh.dto.crypto.PaymentPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HybridEncryptionService {

    private final AesEncryptionService aesService;
    private final RsaKeyService rsaService;
    private final DigitalSignatureService signatureService;
    private final ObjectMapper objectMapper;

    public EncryptedPaymentPacket encrypt(
            PaymentPayload payload,
            String encryptionKeyId,
            PublicKey backendPublicKey,
            String signatureKeyId,
            PrivateKey senderPrivateKey
    ) throws Exception {

        String json = objectMapper.writeValueAsString(payload);

        SecretKey aesKey = aesService.generateKey();

        var encrypted = aesService.encrypt(json, aesKey);

        String wrappedKey = rsaService.wrapAesKey(aesKey, backendPublicKey);

        UUID packetId = UUID.randomUUID();

        String signedData = buildSignedData(
                packetId,
                encryptionKeyId,
                wrappedKey,
                encrypted.iv(),
                encrypted.cipherText(),
                signatureKeyId
        );

        String signature = signatureService.sign(signedData, senderPrivateKey);

        return new EncryptedPaymentPacket(
                packetId,
                encryptionKeyId,
                wrappedKey,
                encrypted.iv(),
                encrypted.cipherText(),
                signature,
                signatureKeyId
        );

    }

    public PaymentPayload decrypt (
            EncryptedPaymentPacket packet,
            PrivateKey backendPrivateKey,
            PublicKey senderPublicKey
    ) throws Exception {

        String signedData = buildSignedData(
                packet.packetId(),
                packet.keyId(),
                packet.encryptedAesKey(),
                packet.iv(),
                packet.encryptedPayload(),
                packet.signatureKeyId()
        );

        boolean valid = signatureService.verify(
                signedData,
                packet.signature(),
                senderPublicKey
        );

        if (!valid) {
            throw new GeneralSecurityException(
                    "Invalid payment packet signature"
            );
        }

        SecretKey aesKey = rsaService.unwrapAesKey(packet.encryptedAesKey(), backendPrivateKey);

        var encryptedData = new AesEncryptionService.EncryptedData(
                packet.iv(),
                packet.encryptedPayload()
        );

        String json = aesService.decrypt(encryptedData, aesKey);

        return objectMapper.readValue(json, PaymentPayload.class);
    }

    private String buildSignedData(
            UUID packetId,
            String keyId,
            String wrappedKey,
            String iv,
            String encryptedPayload,
            String signatureKeyId
    ) {

        return String.join(
                "\n",
                packetId.toString(),
                keyId,
                wrappedKey,
                iv,
                encryptedPayload,
                signatureKeyId
        );
    }
}
