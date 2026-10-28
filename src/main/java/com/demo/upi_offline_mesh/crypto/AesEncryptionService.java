package com.demo.upi_offline_mesh.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesEncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    public SecretKey generateKey() throws Exception {

        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);

        keyGenerator.init(KEY_SIZE, secureRandom);

        return keyGenerator.generateKey();
    }

    public EncryptedData encrypt(String plainText, SecretKey secretKey) throws Exception {

        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return new EncryptedData(
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(encryptedBytes)
        );

    }

    public String decrypt(EncryptedData encryptedData, SecretKey secretKey) throws Exception {

        byte[] iv = Base64.getDecoder().decode(encryptedData.iv);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData.cipherText);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public record EncryptedData(
            String iv,
            String cipherText
    ) {}
}
