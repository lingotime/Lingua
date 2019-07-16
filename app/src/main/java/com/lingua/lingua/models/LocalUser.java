package com.lingua.lingua.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class LocalUser {
    // the temporary user model to handle the profile data entry

    private String username;
    private String bio;
    private ArrayList<String> currentLanguages;
    private ArrayList<String> targetLanguages;
    private String originCountry;
    private ArrayList<String> targetCountries;
    private int score = 0;
    private ArrayList<Achievements> achievements; // temporary thing for the
    private HashMap<String, Long> hoursSpoken;
    private ArrayList<LocalUser> friends;
    private boolean online;

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public ArrayList<String> getCurrentLanguages() {
        return currentLanguages;
    }

    public ArrayList<String> getTargetLanguages() {
        return targetLanguages;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public ArrayList<String> getTargetCountries() {
        return targetCountries;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Achievements> getAchievements() {
        return achievements;
    }

    public HashMap<String, Long> getHoursSpoken() {
        return hoursSpoken;
    }

    public ArrayList<LocalUser> getFriends() {
        return friends;
    }

    public boolean isOnline() {
        return online;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCurrentLanguages(ArrayList<String> currentLanguages) {
        this.currentLanguages = currentLanguages;
    }

    public void setTargetLanguages(ArrayList<String> targetLanguages) {
        this.targetLanguages = targetLanguages;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public void setTargetCountries(ArrayList<String> targetCountries) {
        this.targetCountries = targetCountries;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAchievements(ArrayList<Achievements> achievements) {
        this.achievements = achievements;
    }

    public void setHoursSpoken(HashMap<String, Long> hoursSpoken) {
        this.hoursSpoken = hoursSpoken;
    }

    public void setFriends(ArrayList<LocalUser> friends) {
        this.friends = friends;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
