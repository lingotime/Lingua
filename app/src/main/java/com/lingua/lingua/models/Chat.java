package com.lingua.lingua.models;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Chat {

    String id;
    String lastMessage;
    String name;
    ArrayList<String> users;
    String lastUpdatedAt;

    public Chat() {}

    public Chat(String id, String name, String lastMessage, String lastUpdatedAt, ArrayList<String> users) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastUpdatedAt = lastUpdatedAt;
        this.users = users;
    }

    public String getName() { return name; }

    public String getId() { return id; }

    public String getLastMessage() { return lastMessage; }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}