package com.lingua.lingua.notifyAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Class (Model) used to define the invitation sent for video chats
 */

public class Invite {
    public final String roomName; // name of the room to join if the push notification is clicked
    // "from" is a reserved word in Twilio Notify so we use a more verbose name instead
    public final String toIdentity; // the userId of the sender of the notification
    public final String videoChatLanguage; // the language in which the call is being carried out and the one which will be tracked

    public Invite(final String fromIdentity, final String roomName, final String videoChatLanguage) {
        this.toIdentity = fromIdentity;
        this.roomName = roomName;
        this.videoChatLanguage  = videoChatLanguage;
    }

    public Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();

        map.put("fromIdentity", toIdentity);
        map.put("roomName", roomName);
        map.put("videoChatLanguage", videoChatLanguage);
        return map;
    }
}
