package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.Date;

public class User {

    String name;
    Date dob;
    String gender;
    String profilePictureUrl;
    ArrayList<String> targetLanguages;
    ArrayList<String> targetCountries;
    ArrayList<User> friends;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
