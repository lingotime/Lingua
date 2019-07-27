package com.lingua.lingua.models;

public class FriendRequest {

    String id;
    String message;
    String senderId;
    String senderName;
    String receiverName;
    String receiverId;
    String timestamp;

    public FriendRequest(String message, String senderId, String senderName, String receiverId, String receiverName, String timestamp, String id) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
