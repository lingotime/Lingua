package com.lingua.lingua.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.adapters.TextChatAdapter;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;
import com.lingua.lingua.models.Message;

import java.util.ArrayList;
import java.util.List;

/*
Activity for chatting with a specific friend, the recycler view contains all the messages the user
and that friend have exchanged and user can send messages from here
*/

public class TextChatActivity extends AppCompatActivity {

    RecyclerView rvMessages;
    private TextChatAdapter adapter;
    private List<Message> messages;

    private ImageView sendButtonIcon;
    private Button sendButton;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_chat);
        rvMessages = findViewById(R.id.activity_text_chat_messages_timeline);
        messages = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                Message message = new Message();
                User sender = new User();
                sender.setFirstName("Marta");
                message.setMessage("Hey girl! How are you? I've been having a great day I hope you have too");
                message.setSender(sender);
                messages.add(message);
            } else {
                Message message = new Message();
                User sender = new User();
                sender.setFirstName("Cristina");
                message.setMessage("How are you? I'm doing well. Life is good, the family is doing well...");
                message.setSender(sender);
                messages.add(message);
            }
        }

        adapter = new TextChatAdapter(this, messages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        sendButton = findViewById(R.id.activity_chat_details_button_send);
        sendButtonIcon = findViewById(R.id.activity_chat_details_iv_send);
        etMessage = findViewById(R.id.activity_text_chat_message_field);
        sendButtonIcon.setColorFilter(Color.argb(255, 255, 255, 255));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: send message
            }
        });
    }
}
