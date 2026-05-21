package com.couragegang.secrets.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class PayloadCipherExtraTest {

    @Test
    void padsShortKeyMaterial() {
        var cipher = new PayloadCipher("short-key");
        var plain = "x".getBytes();
        assertThat(cipher.decrypt(cipher.encrypt(plain))).isEqualTo(plain);
    }

    @Test
    void parseRefValid() {
        var id = UUID.randomUUID();
        assertThat(PayloadCipher.parseRef("secrets:" + id)).isEqualTo(id);
    }

    @Test
    void parseRefInvalid() {
        assertThatThrownBy(() -> PayloadCipher.parseRef("bad")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PayloadCipher.parseRef(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void encodeRefFormat() {
        var id = UUID.randomUUID();
        assertThat(PayloadCipher.encodeRef(id)).isEqualTo("secrets:" + id);
    }
}
