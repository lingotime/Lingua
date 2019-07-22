package com.lingua.lingua.models;

/*
Chat class with
TODO: make this class Firebase compatible, save and query chats methods
*/

import java.util.ArrayList;

public class Chat {

    String id;
    String lastMessage;
    String name;
    ArrayList<User> users;
    long lastUpdatedAt;

    public Chat(String id, String name, String lastMessage) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getName() { return name; }

    public String getId() { return id; }

    public String getLastMessage() {
        return lastMessage;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}