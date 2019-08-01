package com.lingua.lingua.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FriendRequest {

    String id;
    String message;
    String senderId;
    String senderName;
    String receiverName;
    String receiverId;
    String timestamp;
    ArrayList<String> exploreLanguages;

    public FriendRequest(String message, String senderId, String senderName, String receiverId, String receiverName, String timestamp, String id, ArrayList<String> exploreLanguages) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.id = id;
        this.exploreLanguages = exploreLanguages;
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

    public ArrayList<String> getExploreLanguages() {
        return exploreLanguages;
    }
}
