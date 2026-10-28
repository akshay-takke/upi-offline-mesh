package com.demo.upi_offline_mesh.crypto;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RsaKeyServiceTest {

    private final RsaKeyService rsaKeyService = new RsaKeyService();

    private final AesEncryptionService aesService = new AesEncryptionService();


    @Test
    void shouldWrapAndUnwrapAesKey() throws Exception {

        KeyPair keyPair = rsaKeyService.generateKeyPair();
        SecretKey originalKey = aesService.generateKey();

        String wrappedKey = rsaKeyService.wrapAesKey(originalKey, keyPair.getPublic());

        SecretKey unwrappedKey = rsaKeyService.unwrapAesKey(wrappedKey, keyPair.getPrivate());

        assertArrayEquals(originalKey.getEncoded(), unwrappedKey.getEncoded());
    }

    @Test
    void shouldRejectIncorrectPrivateKey() throws Exception {

        KeyPair correctPair = rsaKeyService.generateKeyPair();
        KeyPair incorrectPair = rsaKeyService.generateKeyPair();

        SecretKey aesKey = aesService.generateKey();

        String wrappedKey = rsaKeyService.wrapAesKey(aesKey, correctPair.getPublic());

        assertThrows(GeneralSecurityException.class, () -> rsaKeyService.unwrapAesKey(wrappedKey, incorrectPair.getPrivate()));
    }
}
