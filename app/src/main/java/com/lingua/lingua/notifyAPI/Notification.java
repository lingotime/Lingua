package com.lingua.lingua.notifyAPI;
/**
 * The Notification model defined to send notifications with Twilio Notify
 *
 * https://www.twilio.com/docs/api/notify/rest/notifications
 */
public class Notification {
    public final String body;
    public final String identity;
    public final String roomName; // variable added for notifications sent for video chat

    public Notification (String body, String identity, String roomName) {
        this.body = body;
        this.identity = identity;
        this.roomName = roomName;
    }

}
