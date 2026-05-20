CREATE TABLE installation_credentials (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id          UUID NOT NULL,
    workspace_id    UUID NOT NULL,
    connector_key   TEXT NOT NULL,
    ciphertext      BYTEA NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX installation_credentials_org_idx ON installation_credentials (org_id);
