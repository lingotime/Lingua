package com.lingua.lingua.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

/* VideoChatActivity allows a user to videochat with another user. */

public class VideoChatActivity extends AppCompatActivity {
    private User currentUser;

    private TextView descriptionText;
    private FrameLayout videoFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        // associate views with java variables
        descriptionText = findViewById(R.id.activity_video_chat_description_text);
        videoFrame = findViewById(R.id.activity_video_chat_video_frame);
    }
}
