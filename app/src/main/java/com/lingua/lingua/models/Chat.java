package com.lingua.lingua.models;

import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Parcel
public class Chat {

    String id;
    String lastMessage;
    String name;
    ArrayList<String> users;
    String lastUpdatedAt;
    ArrayList<String> exploreLanguages;

    public Chat() {}

    public Chat(String id, String name, String lastMessage, String lastUpdatedAt, ArrayList<String> users, ArrayList<String> exploreLanguages) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastUpdatedAt = lastUpdatedAt;
        this.users = users;
        this.exploreLanguages = exploreLanguages;

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

    public ArrayList<String> getExploreLanguages() {
        return exploreLanguages;
    }

    public void setExploreLanguages(ArrayList<String> exploreLanguages) {
        this.exploreLanguages = exploreLanguages;
    }
}