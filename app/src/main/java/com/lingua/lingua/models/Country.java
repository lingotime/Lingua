package com.lingua.lingua.models;

import java.util.ArrayList;

public class Country {
    private String name;
    private String flagPhotoURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagPhotoURL() {
        return flagPhotoURL;
    }

    public void setFlagPhotoURL(String flagPhotoURL) {
        this.flagPhotoURL = flagPhotoURL;
    }

    public ArrayList<User> getNativeUsers() {
        return nativeUsers;
    }

    public void setNativeUsers(ArrayList<User> nativeUsers) {
        this.nativeUsers = nativeUsers;
    }

    private ArrayList<User> nativeUsers;


}
