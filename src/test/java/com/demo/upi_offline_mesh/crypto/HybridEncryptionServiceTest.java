package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.dto.crypto.EncryptedPaymentPacket;
import com.demo.upi_offline_mesh.dto.crypto.PaymentPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HybridEncryptionServiceTest {

    private HybridEncryptionService hybridService;
    private KeyPair backendKeys;
    private KeyPair senderKeys;

    @BeforeEach
    void setUp() throws Exception {

        RsaKeyService rsaService = new RsaKeyService();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registeredModules();

        hybridService = new HybridEncryptionService(
                new AesEncryptionService(),
                rsaService,
                new DigitalSignatureService(),
                mapper
        );

        backendKeys = rsaService.generateKeyPair();
        senderKeys = rsaService.generateKeyPair();
    }

    @Test
    void shouldEncryptAndDecryptPayment() throws Exception {

        PaymentPayload original = new PaymentPayload(
                UUID.randomUUID(),
                1L,
                2L,
                new BigDecimal("500.00"),
                Instant.now(),
                Instant.now().plusSeconds(900)
        );

        EncryptedPaymentPacket packet = hybridService.encrypt(
                original,
                "backend-key-001",
                backendKeys.getPublic(),
                "sender-key-001",
                senderKeys.getPrivate()
        );

        PaymentPayload decrypted = hybridService.decrypt(
                packet,
                backendKeys.getPrivate(),
                senderKeys.getPublic()
        );

        assertEquals(original, decrypted);
    }

    @Test
    void shouldRejectTamperedPacket() throws Exception {

        PaymentPayload original = new PaymentPayload(
                UUID.randomUUID(),
                1L,
                2L,
                new BigDecimal("500.00"),
                Instant.now(),
                Instant.now().plusSeconds(900)
        );

        EncryptedPaymentPacket packet = hybridService.encrypt(
                original,
                "backend-key-001",
                backendKeys.getPublic(),
                "sender-key-001",
                senderKeys.getPrivate()
        );

        EncryptedPaymentPacket tampered = new EncryptedPaymentPacket(
                packet.packetId(),
                packet.keyId(),
                packet.encryptedAesKey(),
                packet.iv(),
                packet.encryptedPayload() + "A",
                packet.signature(),
                packet.signatureKeyId()
        );

        assertThrows(
                GeneralSecurityException.class,
                () -> hybridService.decrypt(
                        tampered,
                        backendKeys.getPrivate(),
                        senderKeys.getPublic()
                )
        );
    }
}
