package com.lingua.lingua.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
/*
Custom User class with basic info, language information, etc.
 */

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String biographyText;
    private Country originCountry;
    private String profilePhotoURL;
    private ArrayList<Language> knownLanguages;
    private ArrayList<Language> exploreLanguages;
    private ArrayList<Country> knownCountries;
    private ArrayList<Country> exploreCountries;
    private HashMap<Language, Integer> hoursSpoken;
    private ArrayList<User> confirmedFriends;
    private ArrayList<User> pendingSentRequestFriends;
    private ArrayList<User> pendingReceivedRequestFriends;
    private boolean isOnline;
    private boolean isComplete;

    public User() {
    }

    public User(String firstName) {
        this.firstName = getFirstName();
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

    public Country getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(Country originCountry) {
        this.originCountry = originCountry;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }

    public ArrayList<Language> getKnownLanguages() {
        return knownLanguages;
    }

    public void setKnownLanguages(ArrayList<Language> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }

    public ArrayList<Language> getExploreLanguages() {
        return exploreLanguages;
    }

    public void setExploreLanguages(ArrayList<Language> exploreLanguages) {
        this.exploreLanguages = exploreLanguages;
    }

    public ArrayList<Country> getKnownCountries() {
        return knownCountries;
    }

    public void setKnownCountries(ArrayList<Country> knownCountries) {
        this.knownCountries = knownCountries;
    }

    public ArrayList<Country> getExploreCountries() {
        return exploreCountries;
    }

    public void setExploreCountries(ArrayList<Country> exploreCountries) {
        this.exploreCountries = exploreCountries;
    }

    public HashMap<Language, Integer> getHoursSpoken() {
        return hoursSpoken;
    }

    public void setHoursSpoken(HashMap<Language, Integer> hoursSpoken) {
        this.hoursSpoken = hoursSpoken;
    }

    public ArrayList<User> getConfirmedFriends() {
        return confirmedFriends;
    }

    public void setConfirmedFriends(ArrayList<User> confirmedFriends) {
        this.confirmedFriends = confirmedFriends;
    }

    public ArrayList<User> getPendingSentRequestFriends() {
        return pendingSentRequestFriends;
    }

    public void setPendingSentRequestFriends(ArrayList<User> pendingSentRequestFriends) {
        this.pendingSentRequestFriends = pendingSentRequestFriends;
    }

    public ArrayList<User> getPendingReceivedRequestFriends() {
        return pendingReceivedRequestFriends;
    }

    public void setPendingReceivedRequestFriends(ArrayList<User> pendingReceivedRequestFriends) {
        this.pendingReceivedRequestFriends = pendingReceivedRequestFriends;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}