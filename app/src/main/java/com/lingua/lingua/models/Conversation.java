package com.lingua.lingua.models;

import org.parceler.Parcel;
import java.util.ArrayList;

@Parcel
public class Conversation {
    String conversationID;
    String conversationName;
    ArrayList<String> conversationParticipants;
    ArrayList<String> conversationMessages;
    String firstTextChatTime;
    String firstVideoChatTime;
    String lastTextChatTime;
    String lastVideoChatTime;

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

    public String getFirstTextChatTime() {
        return firstTextChatTime;
    }

    public void setFirstTextChatTime(String firstTextChatTime) {
        this.firstTextChatTime = firstTextChatTime;
    }

    public String getFirstVideoChatTime() {
        return firstVideoChatTime;
    }

    public void setFirstVideoChatTime(String firstVideoChatTime) {
        this.firstVideoChatTime = firstVideoChatTime;
    }

    public String getLastTextChatTime() {
        return lastTextChatTime;
    }

    public void setLastTextChatTime(String lastTextChatTime) {
        this.lastTextChatTime = lastTextChatTime;
    }

    public String getLastVideoChatTime() {
        return lastVideoChatTime;
    }

    public void setLastVideoChatTime(String lastVideoChatTime) {
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