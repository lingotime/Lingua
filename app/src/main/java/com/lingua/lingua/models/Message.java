package com.lingua.lingua.models;

/*
Message class with body of message, sender, receiver and timestamp
TODO: make this class Firebase compatible, save and query messages methods
*/

public class Message {

    String message;
    User sender;
    User receiver;
    long createdAt;

    public Message(User sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}