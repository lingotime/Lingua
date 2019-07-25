package com.lingua.lingua.models;

import org.parceler.Parcel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/*
Custom User class with basic info, language information, etc.
 */

@Parcel
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String biographyText;
    private String originCountry;
    private String profilePhotoURL;
    private ArrayList<String> knownLanguages;
    private ArrayList<String> exploreLanguages;
    private ArrayList<String> knownCountries;
    private ArrayList<String> exploreCountries;
    private HashMap<String, Integer> hoursSpoken;
    private ArrayList<String> confirmedFriends;
    private ArrayList<String> pendingSentRequestFriends;
    private ArrayList<String> pendingReceivedRequestFriends;
    private boolean online;
    private boolean complete;

    public User() {}

    public User(String firstName) {
        this.firstName = firstName;
    }

    public User(String id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBiographyText() {
        return biographyText;
    }

    public void setBiographyText(String biographyText) {
        this.biographyText = biographyText;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
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

    public HashMap<String, Integer> getHoursSpoken() {
        return hoursSpoken;
    }

    public void setHoursSpoken(HashMap<String, Integer> hoursSpoken) {
        this.hoursSpoken = hoursSpoken;
    }

    public ArrayList<String> getConfirmedFriends() {
        return confirmedFriends;
    }

    public void setConfirmedFriends(ArrayList<String> confirmedFriends) {
        this.confirmedFriends = confirmedFriends;
    }

    public ArrayList<String> getPendingSentRequestFriends() {
        return pendingSentRequestFriends;
    }

    public void setPendingSentRequestFriends(ArrayList<String> pendingSentRequestFriends) {
        this.pendingSentRequestFriends = pendingSentRequestFriends;
    }

    public ArrayList<String> getPendingReceivedRequestFriends() {
        return pendingReceivedRequestFriends;
    }

    public void setPendingReceivedRequestFriends(ArrayList<String> pendingReceivedRequestFriends) {
        this.pendingReceivedRequestFriends = pendingReceivedRequestFriends;
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