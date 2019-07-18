package com.lingua.lingua.models;

/*
Chat class with
TODO: make this class Firebase compatible, save and query chats methods
*/

import java.util.ArrayList;

public class Chat {

    String lastMessage;
    ArrayList<User> users;
    long lastUpdatedAt;

    public Chat(ArrayList<User> users, String lastMessage) {
        this.users = users;
        this.lastMessage = lastMessage;
    }

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