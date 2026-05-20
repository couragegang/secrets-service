package com.couragegang.secrets.api.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public final class SecretsModels {

    private SecretsModels() {}

    @Serdeable
    public record StoreCredentialsRequest(
            @NotNull UUID orgId,
            @NotNull UUID workspaceId,
            @NotNull String connectorKey,
            @NotNull Map<String, String> payload) {}

    @Serdeable
    public record StoreCredentialsResponse(String secretRef) {}

    @Serdeable
    public record CredentialsPayloadResponse(Map<String, String> payload) {}

    @Serdeable
    public record ErrorBody(String code, String message) {
        public static ErrorBody of(String code, String message) {
            return new ErrorBody(code, message);
        }
    }
}
