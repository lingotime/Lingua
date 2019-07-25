package com.lingua.lingua;

import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;


public class VideoTokenGenerator {


    // Required for all types of tokens
    private String twilioAccountSid = "AC7fe7368cc5a508f2c044c3eb1df3a52b";
    private String twilioApiKey = "SK17f5203ceff0da12c3a9cda73e2831c4";
    private String twilioApiSecret = "rDXfaKJbOqlSGwCtF9ZlFZIBxkMjesUo";

    private String identity = "alice"; // enter user's name here; preferably first

    // Create Video grant
    private VideoGrant grant = new VideoGrant().setRoom("DailyStandup");
    // the name of the room will just be the two users appended

    // Create access token
    public AccessToken token = new AccessToken.Builder(
            twilioAccountSid,
            twilioApiKey,
            twilioApiSecret
    ).identity(identity).grant(grant).build();
}
