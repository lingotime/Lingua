package com.lingua.lingua.models;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class FriendRequest {
    String friendRequestID;
    String friendRequestStatus;
    String senderUser;
    String receiverUser;
    String createdTime;
    String respondedTime;

    public FriendRequest() {
        friendRequestID = generateRandomID();
    }

    public String getFriendRequestID() {
        return friendRequestID;
    }

    public void setFriendRequestID(String friendRequestID) {
        this.friendRequestID = friendRequestID;
    }

    public String getFriendRequestStatus() {
        return friendRequestStatus;
    }

    public void setFriendRequestStatus(String friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    public String getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(String senderUser) {
        this.senderUser = senderUser;
    }

    public String getReceiverUser() {
        return receiverUser;
    }

    public void setReceiverUser(String receiverUser) {
        this.receiverUser = receiverUser;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }

    public String getRespondedTime() {
        return respondedTime;
    }

    public void setRespondedTime(String respondedTime) {
        this.respondedTime = respondedTime;
    }

    private String generateRandomID() {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int numberOfCharacters = 15;

        StringBuilder stringBuilder = new StringBuilder();

        while (numberOfCharacters != 0) {
            int character = (int) (Math.random() * allowedCharacters.length());

            stringBuilder.append(allowedCharacters.charAt(character));

            numberOfCharacters--;
        }

        return stringBuilder.toString();
    }
}
