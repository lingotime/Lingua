package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.Message;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Activity for chatting with a specific friend, the recycler view contains all the messages the user
and that friend have exchanged and user can send messages from here
*/

public class ChatDetailsActivity extends AppCompatActivity {

    RecyclerView rvMessages;
    private ChatDetailsAdapter adapter;
    private List<Message> messages;

    private ImageView sendButtonIcon;
    private Button sendButton;
    private EditText etMessage;

    private String chatId;
    private String name;
    private String otherUserId; // for the other user in the chat

    private String userId;
    private String userName;

    Firebase reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "");

        Chat chat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        rvMessages = findViewById(R.id.activity_chat_details_rv);
        messages = new ArrayList<>();


        chatId = getIntent().getStringExtra("chatId");
        name = getIntent().getStringExtra("name");
        otherUserId = getIntent().getStringExtra("otherUserId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_chat_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(chat.getName());

        Firebase.setAndroidContext(this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chat.getId());

        adapter = new ChatDetailsAdapter(this, messages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        sendButton = findViewById(R.id.activity_chat_details_button_send);
        sendButtonIcon = findViewById(R.id.activity_chat_details_iv_send);
        etMessage = findViewById(R.id.activity_chat_details_et);
        sendButtonIcon.setColorFilter(Color.argb(255, 255, 255, 255));

        sendButton.setOnClickListener(view -> {
            String messageText = etMessage.getText().toString();
            String timestamp = new Date().toString();
            if (!messageText.equals("")) {
                // save message
                Map<String, String> map = new HashMap<>();
                map.put("message", messageText);
                map.put("senderId", userId);
                map.put("timestamp", timestamp);
                reference.push().setValue(map);
                etMessage.setText("");

                // set this message to be the lastMessage of the chat
                Firebase chatReference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chat.getId());
                chatReference.child("lastMessage").setValue( userName + ": " + messageText);
                chatReference.child("lastMessageAt").setValue(timestamp);
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // if a message is added, show it in the recycler view
                Map map = dataSnapshot.getValue(Map.class);
                String senderId = map.get("senderId").toString();
                String message = map.get("message").toString();
                String timestamp = map.get("timestamp").toString();
                messages.add(new Message(senderId, message, timestamp));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.chat_details_videochat_icon) {
            // intent to the video chat activity
            Intent intent = new Intent(this, VideoChatActivity.class);
            intent.putExtra("chatID", chatId);
            intent.putExtra("name", name);
            // get the second user Id from the chat
            intent.putExtra("otherUser", otherUserId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
