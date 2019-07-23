package com.lingua.lingua;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.opentok.MediaMode;
import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.SessionProperties;
import com.opentok.exception.OpenTokException;

public class VideoCallActivity extends AppCompatActivity {

    int apiKey = 46389502; // YOUR API
    String apiSecret = String.valueOf(R.string.tokbox_secret);
    OpenTok opentok = new OpenTok(apiKey, apiSecret);

    // remember to close the OpenTok object with opentok.close()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        // A session that uses the OpenTok Media Router:
        try {
            Session session = opentok.createSession(new SessionProperties.Builder()
                    .mediaMode(MediaMode.ROUTED)
                    .build());
            // Store this sessionId in the database for later use:
            String sessionId = session.getSessionId();
            // TODO: Check if this identifier can be used to access the length of the session
            // Generate a token by calling the method on the Session (returned from createSession)
            String token = session.generateToken();
        } catch (OpenTokException e) {
            e.printStackTrace();
        }

    }
}
