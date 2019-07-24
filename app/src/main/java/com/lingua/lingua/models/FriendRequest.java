package com.lingua.lingua.models;

/*
Friend request class for pending friend requests, friend request should be deleted once user accepts or rejects the request
*/

public class FriendRequest {

    String message;
    String senderId;
    String senderName;
    String receiverName;
    String receiverId;
    String timestamp;

    public FriendRequest(String message, String senderId, String senderName, String receiverId, String receiverName, String timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
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
