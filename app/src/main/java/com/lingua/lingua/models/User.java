package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
}