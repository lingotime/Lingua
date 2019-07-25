package com.lingua.lingua.models;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Message {
    String messageID;
    String messageText;
    String senderUser;
    String receiverUser;
    String createdTime;

    public Message() {
        messageID = generateRandomID();
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
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

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
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