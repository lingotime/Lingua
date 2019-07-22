package com.lingua.lingua.models;

/*
Message class with body of message, sender, receiver and timestamp
TODO: make this class Firebase compatible, save and query messages methods
*/

public class Message {

    String message;
    String senderId;
    long createdAt;

    public Message(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}