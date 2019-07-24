package com.lingua.lingua.models;

/*
Friend request class for pending friend requests, friend request should be deleted once user accepts or rejects the request
*/

public class FriendRequest {

    String message;
    String senderId;
    String senderName;
    String receiverId;
    String timestamp;

    public FriendRequest(String message, String senderId, String senderName, String timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }
}
