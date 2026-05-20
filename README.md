# secrets-service

Шифрованное хранение credentials для MCP installations (`/v1/secrets`).

- **Контракт:** [`../api-contracts/secrets/openapi.yaml`](../api-contracts/secrets/openapi.yaml)
- **Порт:** 8087

## Internal API (mcp-gateway)

```http
POST /v1/secrets/internal/credentials
X-Secrets-Internal-Key: dev-internal-key

{"orgId":"...","workspaceId":"...","connectorKey":"notion","payload":{"integration_token":"..."}}
```

Ответ: `{ "secretRef": "secrets:{uuid}" }`.

```http
GET /v1/secrets/internal/credentials/{secretRef}
DELETE /v1/secrets/internal/credentials/{secretRef}
```

## Запуск

```bash
docker compose up --build
```
