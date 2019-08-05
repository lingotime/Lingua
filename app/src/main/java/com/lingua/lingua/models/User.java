package com.lingua.lingua.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
/**
* Custom User class with basic info, language information, etc.
 */

@Parcel
public class User {
    String userID;
    String userName;
    String userBirthDate;
    String userBiographyText;
    String userOriginCountry;
    String userProfilePhotoURL;
    ArrayList<String> knownLanguages;
    ArrayList<String> exploreLanguages;
    ArrayList<String> exploreCountries;
    HashMap<String, Integer> hoursSpokenPerLanguage;
    ArrayList<String> friends;
    ArrayList<String> pendingSentFriendRequests;
    ArrayList<String> pendingReceivedFriendRequests;
    ArrayList<String> declinedUsers;
    ArrayList<String> conversations;
    boolean complete;
    boolean online;

    public User() {}

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

    public ArrayList<String> getExploreCountries() {
        return exploreCountries;
    }

    public void setExploreCountries(ArrayList<String> exploreCountries) {
        this.exploreCountries = exploreCountries;
    }

    public HashMap<String, Integer> getHoursSpokenPerLanguage() {
        return hoursSpokenPerLanguage;
    }

    public void setHoursSpokenPerLanguage(HashMap<String, Integer> hoursSpoken) {
        this.hoursSpokenPerLanguage = hoursSpoken;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getDeclinedUsers() {
        return declinedUsers;
    }

    public void setDeclinedUsers(ArrayList<String> declinedUsers) {
        this.declinedUsers = declinedUsers;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}