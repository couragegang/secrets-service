package com.couragegang.secrets.service;

import com.couragegang.secrets.api.dto.SecretsModels.CredentialsPayloadResponse;
import com.couragegang.secrets.api.dto.SecretsModels.StoreCredentialsRequest;
import com.couragegang.secrets.api.dto.SecretsModels.StoreCredentialsResponse;
import com.couragegang.secrets.crypto.PayloadCipher;
import com.couragegang.secrets.repo.CredentialsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class CredentialsService {

    private final CredentialsRepository repo;
    private final PayloadCipher cipher;
    private final ObjectMapper json;

    public CredentialsService(CredentialsRepository repo, PayloadCipher cipher, ObjectMapper json) {
        this.repo = repo;
        this.cipher = cipher;
        this.json = json;
    }

    public StoreCredentialsResponse store(StoreCredentialsRequest req) {
        try {
            var bytes = json.writeValueAsString(req.payload()).getBytes(StandardCharsets.UTF_8);
            var id = repo.insert(req.orgId(), req.workspaceId(), req.connectorKey(), cipher.encrypt(bytes));
            return new StoreCredentialsResponse(PayloadCipher.encodeRef(id));
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public CredentialsPayloadResponse getPayload(String secretRef) {
        try {
            var id = PayloadCipher.parseRef(secretRef);
            var row = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("not found"));
            var plain = cipher.decrypt(row.ciphertext());
            var payload = json.readValue(plain, new TypeReference<Map<String, String>>() {});
            return new CredentialsPayloadResponse(payload);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void revoke(String secretRef) {
        try {
            var id = PayloadCipher.parseRef(secretRef);
            repo.delete(id);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
