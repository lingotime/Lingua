package com.lingua.lingua.models;

/*
    Friend request class for pending friend requests, friend request should be deleted once user accepts or rejects the request
    TODO: make this class Firebase compatible, save, delete and query friend requests by receiver
    */

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
