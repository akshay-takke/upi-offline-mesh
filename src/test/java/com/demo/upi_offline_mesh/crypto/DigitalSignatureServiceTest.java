package com.demo.upi_offline_mesh.crypto;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DigitalSignatureServiceTest {

    private final DigitalSignatureService service = new DigitalSignatureService();

    private KeyPair generateSigningKeys() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(3072);

        return generator.generateKeyPair();
    }

    @Test
    void shouldVerifyValidSignature() throws Exception {

        KeyPair senderKeys = generateSigningKeys();

        String payment = "PAYMENT:500:ACCOUNT1:ACCOUNT2";

        String signature = service.sign(payment, senderKeys.getPrivate());

        assertTrue(service.verify(payment, signature, senderKeys.getPublic()));
    }

    @Test
    void shouldRejectModifiedPayment() throws Exception {

        KeyPair senderKeys = generateSigningKeys();

        String original = "PAYMENT:500:ACCOUNT1:ACCOUNT2";

        String signature = service.sign(original, senderKeys.getPrivate());

        String modified = "PAYMENT:5000:ACCOUNT1:ACCOUNT2";

        assertFalse(service.verify(modified, signature, senderKeys.getPublic()));
    }

    @Test
    void shouldRejectIncorrectPublicKey() throws Exception {

        KeyPair senderKeys = generateSigningKeys();
        KeyPair attackerKeys = generateSigningKeys();

        String payment = "PAYMENT:500:ACCOUNT1:ACCOUNT2";

        String signature = service.sign(payment, senderKeys.getPrivate());

        assertFalse(service.verify(payment, signature, attackerKeys.getPublic()));
    }

}
