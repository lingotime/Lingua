package com.lingua.lingua.notifyAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Class (Model) used to define the invitation sent for video chats
 */

public class Invite {
    public final String roomName; // name of the room to join if the push notification is clicked
    // "from" is a reserved word in Twilio Notify so we use a more verbose name instead
    public final String fromIdentity; // the userId of the sender of the notification

    public Invite(final String fromIdentity, final String roomName) {
        this.fromIdentity = fromIdentity;
        this.roomName = roomName;
    }

    public Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();

        map.put("fromIdentity", fromIdentity);
        map.put("roomName", roomName);

        return map;
    }
}
