package com.demo.upi_offline_mesh.crypto;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

@Service
public class DigitalSignatureService {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public String sign(String data, PrivateKey privateKey) throws GeneralSecurityException {

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    public boolean verify(String data, String signatureValue, PublicKey publicKey) throws GeneralSecurityException {

        Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);

        verifier.initVerify(publicKey);
        verifier.update(data.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes;

        try {
            signatureBytes = Base64.getDecoder().decode(signatureValue);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return verifier.verify(signatureBytes);
    }
}