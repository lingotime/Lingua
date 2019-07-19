package com.lingua.lingua.models;

import java.util.ArrayList;

public class Language {
    private String name;
    private ArrayList<User> nativeUsers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getNativeUsers() {
        return nativeUsers;
    }

    public void setNativeUsers(ArrayList<User> nativeUsers) {
        this.nativeUsers = nativeUsers;
    }
}