package com.lingua.lingua.models;

/*
Chat class
*/

import java.util.ArrayList;


public class Chat {

    String id;
    String lastMessage;
    String name;
    ArrayList<String> users;
    String lastUpdatedAt;

    public Chat(String id, String name, String lastMessage, String lastUpdatedAt) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastUpdatedAt = lastUpdatedAt;
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