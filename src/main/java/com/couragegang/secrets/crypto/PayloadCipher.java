package com.couragegang.secrets.crypto;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Singleton
public final class PayloadCipher {

    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;

    private final byte[] key;

    public PayloadCipher(@Value("${secrets-service.encryption-key}") String keyMaterial) {
        var raw = keyMaterial.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            var padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, 32));
            this.key = padded;
        } else {
            this.key = raw.length == 32 ? raw : java.util.Arrays.copyOf(raw, 32);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        try {
            var iv = new byte[IV_BYTES];
            new SecureRandom().nextBytes(iv);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            var encrypted = cipher.doFinal(plaintext);
            var buf = ByteBuffer.allocate(iv.length + encrypted.length);
            buf.put(iv);
            buf.put(encrypted);
            return buf.array();
        } catch (Exception e) {
            throw new IllegalStateException("encrypt failed", e);
        }
    }

    public byte[] decrypt(byte[] ciphertext) {
        try {
            var buf = ByteBuffer.wrap(ciphertext);
            var iv = new byte[IV_BYTES];
            buf.get(iv);
            var payload = new byte[buf.remaining()];
            buf.get(payload);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            return cipher.doFinal(payload);
        } catch (Exception e) {
            throw new IllegalStateException("decrypt failed", e);
        }
    }

    public static String encodeRef(UUID id) {
        return "secrets:" + id;
    }

    public static UUID parseRef(String secretRef) {
        if (secretRef == null || !secretRef.startsWith("secrets:")) {
            throw new IllegalArgumentException("invalid secret ref");
        }
        return UUID.fromString(secretRef.substring("secrets:".length()));
    }
}
