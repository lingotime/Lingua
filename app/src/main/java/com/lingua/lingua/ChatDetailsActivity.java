package com.lingua.lingua;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity {

    RecyclerView rvMessages;
    private ChatDetailsAdapter adapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        rvMessages = findViewById(R.id.activity_chat_details_rv);
        messages = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                Message message = new Message();
                User sender = new User();
                sender.setName("Marta");
                message.setMessage("Hey girl! How are you? I've been having a great day I hope you have too");
                message.setSender(sender);
                messages.add(message);
            } else {
                Message message = new Message();
                User sender = new User();
                sender.setName("Cristina");
                message.setMessage("How are you? I'm doing well. Life is good, the family is doing well...");
                message.setSender(sender);
                messages.add(message);
            }
        }

        adapter = new ChatDetailsAdapter(this, messages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);
    }
}
