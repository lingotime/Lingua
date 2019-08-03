package com.lingua.lingua.models;

import org.parceler.Parcel;
import java.util.ArrayList;

@Parcel
public class Chat {
    String chatID;
    String chatName;
    ArrayList<String> chatParticipants;
    ArrayList<String> chatMessages;
    ArrayList<String> chatLanguages;
    String firstTextChatTime;
    String firstVideoChatTime;
    String lastTextChatTime;
    String lastVideoChatTime;

    public Chat() { }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ArrayList<String> getChatParticipants() {
        return chatParticipants;
    }

    public void setChatParticipants(ArrayList<String> chatParticipants) {
        this.chatParticipants = chatParticipants;
    }

    public ArrayList<String> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<String> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public ArrayList<String> getChatLanguages() {
        return chatLanguages;
    }

    public void setChatLanguages(ArrayList<String> chatLanguages) {
        this.chatLanguages = chatLanguages;
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
}