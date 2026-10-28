package com.demo.upi_offline_mesh.crypto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AesEncryptionServiceTest {

    private final AesEncryptionService aesService = new AesEncryptionService();

    @Test
    void shouldEncryptAndDecryptSuccessfully() throws Exception {

        String originalMessage = "Payment of INR 500 from account 1 to account 2";

        SecretKey secretKey = aesService.generateKey();

        var encrypted = aesService.encrypt(originalMessage, secretKey);

        assertNotEquals(originalMessage, encrypted.cipherText());

        String decrypted = aesService.decrypt(encrypted, secretKey);

        assertEquals(originalMessage, decrypted);
    }

    @Test
    void  shouldRejectTamperedCipherText() throws Exception {

        SecretKey secretKey = aesService.generateKey();

        var encrypted = aesService.encrypt("Payment INR 500", secretKey);

        byte[] bytes = Base64.getDecoder().decode(encrypted.cipherText());

        // Modify one byte
        bytes[0] ^= 1;

        var tampered = new AesEncryptionService.EncryptedData(encrypted.iv(),
                Base64.getEncoder().encodeToString(bytes));

        assertThrows(AEADBadTagException.class, () -> aesService.decrypt(tampered, secretKey));

    }

    @Test
    void shouldGenerateDifferentIVs() throws Exception {

        SecretKey secretKey = aesService.generateKey();

        var first = aesService.encrypt("Payment", secretKey);
        var second = aesService.encrypt("Payment", secretKey);

        assertNotEquals(first.iv(), second.iv());
        assertNotEquals(first.cipherText(), second.cipherText());
    }
}
