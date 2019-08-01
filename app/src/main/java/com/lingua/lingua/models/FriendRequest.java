package com.lingua.lingua.models;

import org.parceler.Parcel;

@Parcel
public class FriendRequest {
    String friendRequestID;
    String friendRequestStatus;
    String friendRequestMessage;
    String senderUser;
    String receiverUser;
    String createdTime;
    String respondedTime;

    public FriendRequest() { }

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

    public String getFriendRequestMessage() {
        return friendRequestMessage;
    }

    public void setFriendRequestMessage(String friendRequestMessage) {
        this.friendRequestMessage = friendRequestMessage;
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
}
