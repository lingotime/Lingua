package com.lingua.lingua.models;

import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.HashMap;

@Parcel
public class User {
    String userID;
    String userName; // Required and prompted
    String userBirthDate; // Required and prompted
    String userGender; // Required and prompted
    String userBiographyText; // Required and prompted
    String userOriginCountry; // Required and prompted
    String userProfilePhotoURL; // Required and prompted
    ArrayList<String> knownLanguages; // Required and prompted
    ArrayList<String> exploreLanguages; // Required and prompted
    ArrayList<String> knownCountries; // Required and prompted
    ArrayList<String> exploreCountries; // Required and prompted
    HashMap<String, Integer> hoursSpokenPerLanguage;
    ArrayList<String> friends;
    ArrayList<String> pendingSentFriendRequests;
    ArrayList<String> pendingReceivedFriendRequests;
    ArrayList<String> conversations;
    boolean complete;
    boolean online;

    public User () { }

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

    public String getUserBirthDate() {
        return userBirthDate;
    }

    public void setUserBirthDate(String userBirthDate) {
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
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}