package com.lingua.lingua.models;

public class FriendRequest {

    String message;
    User sender;
    User receiver;
    long createdAt;

    public FriendRequest(String message, User sender, User receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
