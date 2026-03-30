package com.hellojss.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hellojss.config.DatabaseConfig;

public class SchemaMigrator {
    private static final String BASELINE_VERSION = "baseline-guestbook-v1";

    private final DatabaseConfig databaseConfig;

    public SchemaMigrator(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public SchemaMigrationResult ensureSchema() throws SQLException {
        Connection connection = databaseConfig.openConnection();

        try {
            connection.setAutoCommit(false);

            createVersionTable(connection);
            createGuestMessageTable(connection);
            createGuestMessageIndex(connection);
            boolean migratedNow = markBaselineVersion(connection);

            connection.commit();
            return new SchemaMigrationResult(migratedNow);
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(connection);
        }
    }

    private void createVersionTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS app_schema_version ("
                    + "version_key VARCHAR(100) PRIMARY KEY,"
                    + "applied_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ")");
        } finally {
            statement.close();
        }
    }

    private void createGuestMessageTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS guest_messages ("
                    + "id BIGSERIAL PRIMARY KEY,"
                    + "sender_name VARCHAR(100) NOT NULL,"
                    + "message_text TEXT NOT NULL,"
                    + "created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ")");
        } finally {
            statement.close();
        }
    }

    private void createGuestMessageIndex(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute("CREATE INDEX IF NOT EXISTS idx_guest_messages_created_at ON guest_messages (created_at DESC, id DESC)");
        } finally {
            statement.close();
        }
    }

    private boolean markBaselineVersion(Connection connection) throws SQLException {
        if (isBaselineAlreadyApplied(connection)) {
            return false;
        }

        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO app_schema_version (version_key) VALUES (?) ON CONFLICT (version_key) DO NOTHING");
        try {
            statement.setString(1, BASELINE_VERSION);
            return statement.executeUpdate() > 0;
        } finally {
            statement.close();
        }
    }

    private boolean isBaselineAlreadyApplied(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
            "SELECT 1 FROM app_schema_version WHERE version_key = ?");
        try {
            statement.setString(1, BASELINE_VERSION);
            ResultSet resultSet = statement.executeQuery();
            try {
                return resultSet.next();
            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}