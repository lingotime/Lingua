package com.lingua.lingua;

import android.util.Log;

import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;


public class VideoTokenGenerator {


    // Required for all types of tokens
    private String twilioAccountSid = "AC7fe7368cc5a508f2c044c3eb1df3a52b";
    private String twilioApiKey = "SK17f5203ceff0da12c3a9cda73e2831c4";
    private String twilioApiSecret = "rDXfaKJbOqlSGwCtF9ZlFZIBxkMjesUo";


    private String identity; // enter user's name here; preferably first
    private String roomName;
    public String JwtToken;
    private VideoGrant grant;
    public AccessToken token;

    public VideoTokenGenerator(String userId, String roomName) {
        // takes care of adding the user
        this.identity = userId;
        this.roomName = roomName;
        // Create Video grant
        this.grant = new VideoGrant().setRoom(roomName);
        // the name of the room will just be the two users appended

        // Create access token
        this.token = new AccessToken.Builder(
                twilioAccountSid,
                twilioApiKey,
                twilioApiSecret
        ).identity(identity).grant(grant).build();
        this.JwtToken = this.token.toJwt();
        Log.i("TokenGenerator", String.format("%s", this.JwtToken));
    }
}
