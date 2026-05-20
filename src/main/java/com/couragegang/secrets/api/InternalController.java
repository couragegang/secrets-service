package com.couragegang.secrets.api;

import com.couragegang.secrets.api.dto.SecretsModels.CredentialsPayloadResponse;
import com.couragegang.secrets.api.dto.SecretsModels.StoreCredentialsRequest;
import com.couragegang.secrets.api.dto.SecretsModels.StoreCredentialsResponse;
import com.couragegang.secrets.service.CredentialsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;

@Controller("/internal")
public class InternalController {

    private final CredentialsService credentials;

    public InternalController(CredentialsService credentials) {
        this.credentials = credentials;
    }

    @Post("/credentials")
    public HttpResponse<StoreCredentialsResponse> store(@Body @Valid StoreCredentialsRequest body) {
        return HttpResponse.ok(credentials.store(body));
    }

    @Get("/credentials/{+secretRef}")
    public HttpResponse<CredentialsPayloadResponse> get(@PathVariable String secretRef) {
        return HttpResponse.ok(credentials.getPayload(secretRef));
    }

    @Delete("/credentials/{+secretRef}")
    public HttpResponse<Void> revoke(@PathVariable String secretRef) {
        credentials.revoke(secretRef);
        return HttpResponse.noContent();
    }
}
