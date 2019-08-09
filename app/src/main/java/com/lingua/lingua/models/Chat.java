package com.lingua.lingua.models;

import org.parceler.Parcel;
import java.util.ArrayList;

@Parcel
public class Chat {
    String chatID;
    String chatName;
    ArrayList<String> chatParticipantIds;
    ArrayList<String> chatLanguages;
    String firstTextChatTime;
    String firstVideoChatTime;
    String lastTextChatTime;
    String lastVideoChatTime;
    String lastTextMessage;
    String chatPhotoUrl;
    boolean lastMessageSeen;
    boolean isGroup;

    public Chat() {}

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

    public ArrayList<String> getChatParticipantIds() {
        return chatParticipantIds;
    }

    public void setChatParticipantIds(ArrayList<String> chatParticipantIds) {
        this.chatParticipantIds = chatParticipantIds;
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

    public String getLastTextMessage() {
        return lastTextMessage;
    }

    public void setLastTextMessage(String lastTextMessage) {
        this.lastTextMessage = lastTextMessage;
    }

    public String getChatPhotoUrl() {
        return chatPhotoUrl;
    }

    public void setChatPhotoUrl(String chatPhotoUrl) {
        this.chatPhotoUrl = chatPhotoUrl;
    }

    public boolean isLastMessageSeen() {
        return lastMessageSeen;
    }

    public void setLastMessageSeen(boolean lastMessageSeen) { this.lastMessageSeen = lastMessageSeen; }

    public boolean isGroup() { return isGroup; }

    public void setGroup(boolean group) { isGroup = group; }
}