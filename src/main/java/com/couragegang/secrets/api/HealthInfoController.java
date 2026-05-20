package com.couragegang.secrets.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.Map;

@Controller
public final class HealthInfoController {

    @Get("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "secrets-service");
    }
}
