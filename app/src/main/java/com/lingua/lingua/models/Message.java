package com.lingua.lingua.models;

/*
Message class with body of message, sender, receiver and timestamp
*/

public class Message {

    String message;
    String senderId;
    String timestamp;

    public Message(String senderId, String message, String timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() { return senderId; }

    public String getTimestamp() { return timestamp; }
}