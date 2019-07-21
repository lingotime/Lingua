package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.Date;

public class Conversation {
    private String conversationID;
    private String conversationName;
    private ArrayList<String> conversationParticipants;
    private ArrayList<String> conversationMessages;
    private Date firstTextChatTime;
    private Date firstVideoChatTime;
    private Date lastTextChatTime;
    private Date lastVideoChatTime;

    public Conversation() {
        conversationID = generateRandomID();
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public ArrayList<String> getConversationParticipants() {
        return conversationParticipants;
    }

    public void setConversationParticipants(ArrayList<String> conversationParticipants) {
        this.conversationParticipants = conversationParticipants;
    }

    public ArrayList<String> getConversationMessages() {
        return conversationMessages;
    }

    public void setConversationMessages(ArrayList<String> conversationMessages) {
        this.conversationMessages = conversationMessages;
    }

    public Date getFirstTextChatTime() {
        return firstTextChatTime;
    }

    public void setFirstTextChatTime(Date firstTextChatTime) {
        this.firstTextChatTime = firstTextChatTime;
    }

    public Date getFirstVideoChatTime() {
        return firstVideoChatTime;
    }

    public void setFirstVideoChatTime(Date firstVideoChatTime) {
        this.firstVideoChatTime = firstVideoChatTime;
    }

    public Date getLastTextChatTime() {
        return lastTextChatTime;
    }

    public void setLastTextChatTime(Date lastTextChatTime) {
        this.lastTextChatTime = lastTextChatTime;
    }

    public Date getLastVideoChatTime() {
        return lastVideoChatTime;
    }

    public void setLastVideoChatTime(Date lastVideoChatTime) {
        this.lastVideoChatTime = lastVideoChatTime;
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