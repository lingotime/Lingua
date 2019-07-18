package com.lingua.lingua.models;

import java.io.File;
import java.util.ArrayList;

public class Country {
    private String countryId;
    public ArrayList<String> languages;
    public String name;
    public File flag; // file will come from the Firebase
}
