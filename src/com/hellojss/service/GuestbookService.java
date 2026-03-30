package com.hellojss.service;

import java.sql.SQLException;
import java.util.List;

import com.hellojss.config.DatabaseConfig;
import com.hellojss.migration.SchemaMigrationResult;
import com.hellojss.migration.SchemaMigrator;
import com.hellojss.model.GuestMessage;
import com.hellojss.repository.GuestMessageRepository;

public class GuestbookService {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final SchemaMigrator schemaMigrator;
    private final GuestMessageRepository repository;

    public GuestbookService() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        this.schemaMigrator = new SchemaMigrator(databaseConfig);
        this.repository = new GuestMessageRepository(databaseConfig);
    }

    public SchemaMigrationResult ensureReady() throws SQLException {
        return schemaMigrator.ensureSchema();
    }

    public void addMessage(String senderName, String messageText) throws SQLException {
        String normalizedName = normalize(senderName);
        String normalizedMessage = normalize(messageText);

        if (normalizedName == null) {
            throw new IllegalArgumentException("Please enter your name.");
        }
        if (normalizedMessage == null) {
            throw new IllegalArgumentException("Please enter a message.");
        }
        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be 100 characters or less.");
        }
        if (normalizedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message must be 2000 characters or less.");
        }

        repository.save(normalizedName, normalizedMessage);
    }

    public List<GuestMessage> getMessages() throws SQLException {
        return repository.findAllNewestFirst();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.length() == 0) {
            return null;
        }

        return trimmed;
    }
}