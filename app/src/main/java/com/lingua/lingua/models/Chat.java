package com.lingua.lingua.models;

/*
Chat class
*/

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Chat {

    String id;
    String lastMessage;
    String name;
    ArrayList<String> users;
    String lastUpdatedAt;

    public Chat(String id, String name, String lastMessage, String lastUpdatedAt) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getName() { return name; }

    public String getId() { return id; }

    public String getLastMessage() {
        return lastMessage;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    // Converts from date format "Tue Jul 09 17:22:36 PDT 2019" to "5 min. ago"
    public static String getRelativeTimeAgo(String rawDate) {
        String parseFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(parseFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}