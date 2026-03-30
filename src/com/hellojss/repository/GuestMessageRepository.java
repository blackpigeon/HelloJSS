package com.hellojss.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hellojss.config.DatabaseConfig;
import com.hellojss.model.GuestMessage;

public class GuestMessageRepository {
    private final DatabaseConfig databaseConfig;

    public GuestMessageRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void save(String senderName, String messageText) throws SQLException {
        Connection connection = databaseConfig.openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO guest_messages (sender_name, message_text) VALUES (?, ?)");
            try {
                statement.setString(1, senderName);
                statement.setString(2, messageText);
                statement.executeUpdate();
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    public List<GuestMessage> findAllNewestFirst() throws SQLException {
        List<GuestMessage> messages = new ArrayList<GuestMessage>();
        Connection connection = databaseConfig.openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT id, sender_name, message_text, created_at "
                    + "FROM guest_messages ORDER BY created_at DESC, id DESC");
            try {
                ResultSet resultSet = statement.executeQuery();
                try {
                    while (resultSet.next()) {
                        messages.add(new GuestMessage(
                            resultSet.getLong("id"),
                            resultSet.getString("sender_name"),
                            resultSet.getString("message_text"),
                            resultSet.getTimestamp("created_at")));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
        return messages;
    }
}