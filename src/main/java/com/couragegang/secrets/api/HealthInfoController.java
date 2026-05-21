package com.couragegang.secrets.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.Map;

@Controller
public final class HealthInfoController {

    @Get("/")
    public Map<String, String> root() {
        return Map.of(
                "service", "secrets-service",
                "health", "/v1/secrets/health",
                "metrics", "/v1/secrets/metrics",
                "internal", "/v1/secrets/internal/credentials");
    }
}
