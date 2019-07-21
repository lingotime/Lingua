package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class User {
    private String userID;
    private String userName; // Required and prompted
    private Date userBirthDate; // Required and prompted
    private String userGender; // Required and prompted
    private String userBiographyText; // Required and prompted
    private String userOriginCountry; // Required and prompted
    private String userProfilePhotoURL; // Required and prompted
    private ArrayList<String> knownLanguages; // Required and prompted
    private ArrayList<String> exploreLanguages; // Required and prompted
    private ArrayList<String> knownCountries; // Required and prompted
    private ArrayList<String> exploreCountries; // Required and prompted
    private HashMap<String, Integer> hoursSpokenPerLanguage;
    private ArrayList<String> friends;
    private ArrayList<String> pendingSentFriendRequests;
    private ArrayList<String> pendingReceivedFriendRequests;
    private ArrayList<String> conversations;
    private boolean isComplete;
    private boolean isOnline;

    public User() {
        userID = generateRandomID();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getUserBirthDate() {
        return userBirthDate;
    }

    public void setUserBirthDate(Date userBirthDate) {
        this.userBirthDate = userBirthDate;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserBiographyText() {
        return userBiographyText;
    }

    public void setUserBiographyText(String userBiographyText) {
        this.userBiographyText = userBiographyText;
    }

    public String getUserOriginCountry() {
        return userOriginCountry;
    }

    public void setUserOriginCountry(String userOriginCountry) {
        this.userOriginCountry = userOriginCountry;
    }

    public String getUserProfilePhotoURL() {
        return userProfilePhotoURL;
    }

    public void setUserProfilePhotoURL(String userProfilePhotoURL) {
        this.userProfilePhotoURL = userProfilePhotoURL;
    }

    public ArrayList<String> getKnownLanguages() {
        return knownLanguages;
    }

    public void setKnownLanguages(ArrayList<String> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }

    public ArrayList<String> getExploreLanguages() {
        return exploreLanguages;
    }

    public void setExploreLanguages(ArrayList<String> exploreLanguages) {
        this.exploreLanguages = exploreLanguages;
    }

    public ArrayList<String> getKnownCountries() {
        return knownCountries;
    }

    public void setKnownCountries(ArrayList<String> knownCountries) {
        this.knownCountries = knownCountries;
    }

    public ArrayList<String> getExploreCountries() {
        return exploreCountries;
    }

    public void setExploreCountries(ArrayList<String> exploreCountries) {
        this.exploreCountries = exploreCountries;
    }

    public HashMap<String, Integer> getHoursSpokenPerLanguage() {
        return hoursSpokenPerLanguage;
    }

    public void setHoursSpokenPerLanguage(HashMap<String, Integer> hoursSpokenPerLanguage) {
        this.hoursSpokenPerLanguage = hoursSpokenPerLanguage;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getPendingSentFriendRequests() {
        return pendingSentFriendRequests;
    }

    public void setPendingSentFriendRequests(ArrayList<String> pendingSentFriendRequests) {
        this.pendingSentFriendRequests = pendingSentFriendRequests;
    }

    public ArrayList<String> getPendingReceivedFriendRequests() {
        return pendingReceivedFriendRequests;
    }

    public void setPendingReceivedFriendRequests(ArrayList<String> pendingReceivedFriendRequests) {
        this.pendingReceivedFriendRequests = pendingReceivedFriendRequests;
    }

    public ArrayList<String> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<String> conversations) {
        this.conversations = conversations;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    private String generateRandomID() {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int numberOfCharacters = 20;

        StringBuilder stringBuilder = new StringBuilder();

        while (numberOfCharacters != 0) {
            int character = (int) (Math.random() * allowedCharacters.length());

            stringBuilder.append(allowedCharacters.charAt(character));

            numberOfCharacters--;
        }

        return stringBuilder.toString();
    }
}