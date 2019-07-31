package com.lingua.lingua.notifyAPI;
/**
 * The Notification model defined to send notifications with Twilio Notify
 *
 * https://www.twilio.com/docs/api/notify/rest/notifications
 */
public class Notification {
    public final String title;
    public final String body;
    public final String identity;

    public Notification (String title, String body, String identity) {
        this.title = title;
        this.body = body;
        this.identity = identity;
    }

}
