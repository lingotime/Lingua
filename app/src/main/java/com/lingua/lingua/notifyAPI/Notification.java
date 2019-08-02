package com.lingua.lingua.notifyAPI;

import java.util.Map;

/**
 * The Notification model defined to send notifications with Twilio Notify
 *
 * https://www.twilio.com/docs/api/notify/rest/notifications
 */
public class Notification {
    public final String body;
    public final String identity;
    public final Map<String, String> videoChatData; // variable added for notifications sent for video chat

    public Notification (String body, String identity, Map<String, String> videoInvite) {
        this.body = body;
        this.identity = identity;
        this.videoChatData = videoInvite;
    }
}
