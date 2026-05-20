package com.couragegang.secrets.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PayloadCipherTest {

    @Test
    void roundTrip() {
        var cipher = new PayloadCipher("0123456789abcdef0123456789abcdef");
        var plain = "{\"integration_token\":\"x\"}".getBytes();
        var encrypted = cipher.encrypt(plain);
        assertThat(cipher.decrypt(encrypted)).isEqualTo(plain);
    }
}
