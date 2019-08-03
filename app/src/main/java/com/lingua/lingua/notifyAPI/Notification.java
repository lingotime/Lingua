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
    public final String identity;
    @SerializedName("Body")
    public final String body;
    @SerializedName("Data")
    public final Map<String, String> data;

    public Notification (String body, String identity, Map<String, String> data) {
        this.body = body;
        this.identity = identity;
        this.data = data;
    }
}
