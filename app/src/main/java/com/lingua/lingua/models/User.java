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
    HashMap<String, Long> secondsSpokenPerLanguage;
    ArrayList<String> friends;
    ArrayList<String> pendingSentFriendRequests;
    ArrayList<String> pendingReceivedFriendRequests;
    ArrayList<String> declinedUsers;
    ArrayList<String> conversations;
    HashMap<String, String> learningAchievements;
    HashMap<String, String> teachingAchievements;
    boolean willingToHost;
    boolean lookingForAHost;
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

    public boolean isWillingToHost() {
        return willingToHost;
    }

    public void setUserWillingToHost(boolean willingToHost) {
        this.willingToHost = willingToHost;
    }

    public boolean isLookingForAHost() {
        return this.lookingForAHost;
    }

    public void setUserLookingForAHost(boolean lookingForHost) {
        this.lookingForAHost = lookingForHost;
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

    public HashMap<String, Long> getSecondsSpokenPerLanguage() {
        return secondsSpokenPerLanguage;
    }

    public void setSecondsSpokenPerLanguage(HashMap<String, Long> hoursSpoken) {
        this.secondsSpokenPerLanguage = hoursSpoken;
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

    public HashMap<String, String> getLearningAchievements() {
        return learningAchievements;
    }

    public void setLearningAchievements(HashMap<String, String> learningAchievements) {
        this.learningAchievements = learningAchievements;
    }

    public HashMap<String, String> getTeachingAchievements() {
        return teachingAchievements;
    }

    public void setTeachingAchievements(HashMap<String, String> teachingAchievements) {
        this.teachingAchievements = teachingAchievements;
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