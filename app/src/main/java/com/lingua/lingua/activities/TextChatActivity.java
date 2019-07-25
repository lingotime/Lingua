package com.lingua.lingua.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.TextChatAdapter;
import com.lingua.lingua.models.Message;
import com.lingua.lingua.models.User;

import java.util.ArrayList;

/* TextChatActivity allows a user to text another user. */

public class TextChatActivity extends AppCompatActivity {
    private User currentUser;

    ArrayList<Message> messages;
    TextChatAdapter messagesAdapter;
    private TextView descriptionText;
    private RecyclerView messagesTimeline;
    private EditText messageField;
    private ImageButton sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_chat);

        // associate views with java variables
        descriptionText = findViewById(R.id.activity_text_chat_description_text);
        messagesTimeline = findViewById(R.id.activity_text_chat_messages_timeline);
        messageField = findViewById(R.id.activity_text_chat_message_field);
        sendButton = findViewById(R.id.activity_text_chat_send_button);

        // load messages into timeline
        // messages = loadMessages();
        messages = new ArrayList<Message>();
        messagesAdapter = new TextChatAdapter(this, messages);
        messagesTimeline.setAdapter(messagesAdapter);

        // display timeline
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesTimeline.setLayoutManager(layoutManager);
    }
}