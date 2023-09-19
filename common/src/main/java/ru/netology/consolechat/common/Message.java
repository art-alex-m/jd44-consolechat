package ru.netology.consolechat.common;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String content;
    private final String user;
    private final LocalDateTime createdAt;
    private LocalDateTime receivedAt;

    public Message(String content, String user) {
        this.content = content;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.receivedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, content, createdAt, receivedAt);
    }
}
