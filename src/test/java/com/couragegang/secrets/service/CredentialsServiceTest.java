package com.couragegang.secrets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.couragegang.secrets.api.dto.SecretsModels.StoreCredentialsRequest;
import com.couragegang.secrets.crypto.PayloadCipher;
import com.couragegang.secrets.repo.CredentialsRepository;
import com.couragegang.secrets.repo.CredentialsRepository.StoredRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

    @Mock
    CredentialsRepository repo;

    PayloadCipher cipher = new PayloadCipher("0123456789abcdef0123456789abcdef");
    ObjectMapper json = new ObjectMapper();
    CredentialsService svc;

    UUID orgId = UUID.randomUUID();
    UUID wsId = UUID.randomUUID();
    UUID credId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        svc = new CredentialsService(repo, cipher, json);
    }

    @Test
    void storeReturnsSecretRef() throws Exception {
        when(repo.insert(any(), any(), any(), any())).thenReturn(credId);

        var res = svc.store(new StoreCredentialsRequest(orgId, wsId, "notion", Map.of("token", "x")));

        assertThat(res.secretRef()).isEqualTo("secrets:" + credId);
    }

    @Test
    void getPayloadRoundTrip() throws Exception {
        var encrypted = cipher.encrypt("{\"token\":\"abc\"}".getBytes());
        when(repo.findById(credId)).thenReturn(Optional.of(new StoredRow(orgId, wsId, "notion", encrypted)));

        var res = svc.getPayload("secrets:" + credId);

        assertThat(res.payload()).containsEntry("token", "abc");
    }

    @Test
    void getPayloadNotFound() {
        assertThatThrownBy(() -> svc.getPayload("secrets:" + UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void revokeDeletes() throws Exception {
        svc.revoke("secrets:" + credId);
        verify(repo).delete(credId);
    }
}
