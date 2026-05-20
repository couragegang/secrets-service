package com.couragegang.secrets.repo;

import jakarta.inject.Singleton;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

@Singleton
public final class CredentialsRepository {

    private final DataSource dataSource;

    public CredentialsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UUID insert(UUID orgId, UUID workspaceId, String connectorKey, byte[] ciphertext)
            throws SQLException {
        try (var c = dataSource.getConnection();
                var ps = c.prepareStatement(
                        """
                        INSERT INTO installation_credentials (org_id, workspace_id, connector_key, ciphertext)
                        VALUES (?, ?, ?, ?)
                        RETURNING id
                        """)) {
            ps.setObject(1, orgId);
            ps.setObject(2, workspaceId);
            ps.setString(3, connectorKey);
            ps.setBytes(4, ciphertext);
            try (var rs = ps.executeQuery()) {
                rs.next();
                return rs.getObject(1, UUID.class);
            }
        }
    }

    public Optional<StoredRow> findById(UUID id) throws SQLException {
        try (var c = dataSource.getConnection();
                var ps = c.prepareStatement(
                        "SELECT org_id, workspace_id, connector_key, ciphertext FROM installation_credentials WHERE id = ?")) {
            ps.setObject(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            new StoredRow(
                                    rs.getObject(1, UUID.class),
                                    rs.getObject(2, UUID.class),
                                    rs.getString(3),
                                    rs.getBytes(4)));
                }
            }
        }
        return Optional.empty();
    }

    public boolean delete(UUID id) throws SQLException {
        try (var c = dataSource.getConnection();
                var ps = c.prepareStatement("DELETE FROM installation_credentials WHERE id = ?")) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public record StoredRow(UUID orgId, UUID workspaceId, String connectorKey, byte[] ciphertext) {}
}
