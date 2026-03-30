package com.hellojss.model;

import java.sql.Timestamp;

public class GuestMessage {
    private final long id;
    private final String senderName;
    private final String messageText;
    private final Timestamp createdAt;

    public GuestMessage(long id, String senderName, String messageText, Timestamp createdAt) {
        this.id = id;
        this.senderName = senderName;
        this.messageText = messageText;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}