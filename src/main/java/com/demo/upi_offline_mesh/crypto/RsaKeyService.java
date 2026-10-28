package com.demo.upi_offline_mesh.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

@Service
public class RsaKeyService {

    private static final String TRANSFORMATION = "RSA/ECB/OAEPPadding";

    private static final OAEPParameterSpec OAEP_SPEC = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
    );

    public KeyPair generateKeyPair() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(3072, new SecureRandom());

        return generator.generateKeyPair();
    }

    public String wrapAesKey(SecretKey aesKey, PublicKey publicKey) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, OAEP_SPEC);

        byte[] encrypted = cipher.doFinal(aesKey.getEncoded());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public SecretKey unwrapAesKey (String encryptedAesKey, PrivateKey privateKey) throws Exception {

        byte[] encrypted = Base64.getDecoder().decode(encryptedAesKey);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(Cipher.DECRYPT_MODE, privateKey, OAEP_SPEC);

        byte[] aesKeyBytes = cipher.doFinal(encrypted);

        if (aesKeyBytes.length != 32) {

            throw new GeneralSecurityException("Invalid AES-256 key length");
        }

        return new SecretKeySpec(aesKeyBytes, "AES");
    }


}
