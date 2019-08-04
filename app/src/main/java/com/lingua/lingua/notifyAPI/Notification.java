package com.lingua.lingua.notifyAPI;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * The Notification model defined to send notifications with Twilio Notify
 *
 * https://www.twilio.com/docs/api/notify/rest/notifications
 */
public class Notification {
    @SerializedName("Title")
    public final String title;
    @SerializedName("Identity")
    public final String identity;
    @SerializedName("Body")
    public final String body;
    @SerializedName("Data")
    public final Map<String,String> data;

    public Notification (String title, String body, String identity, Map<String, String> data) {
        this.title = title;
        this.body = body;
        this.data = data;
        this.identity = identity;
    }
}
