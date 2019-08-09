package com.lingua.lingua;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lingua.lingua.adapters.TextChatAdapter;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.Message;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
Activity for chatting with a specific friend, the recycler view contains all the messages the user
and that friend have exchanged and user can send messages from here
*/

public class TextChatActivity extends AppCompatActivity {

    RecyclerView rvMessages;
    private TextChatAdapter adapter;
    private List<Message> messages;

    private FloatingActionButton sendButton;
    private EditText etMessage;

    Firebase reference, lastMessageSeenReference;
    Chat chat;
    List<User> participants;
    User currentUser;
    boolean isGroup;

    private ArrayList<String> languagesToBeLearned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_chat);

        chat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        isGroup = chat.getChatParticipantIds().size() > 2;

        rvMessages = findViewById(R.id.activity_text_chat_rv);
        messages = new ArrayList<>();
        participants = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.activity_text_chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(chat.getChatName());

        Firebase.setAndroidContext(this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chat.getChatID());

        adapter = new TextChatAdapter(this, messages, chat);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        sendButton = findViewById(R.id.activity_text_chat_button_send);
        etMessage = findViewById(R.id.activity_text_chat_et);

        // send message on button click
        sendButton.setOnClickListener(view -> {
            String messageText = etMessage.getText().toString();
            sendMessage(messageText);
        });

        // send message on enter
        etMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String messageText = etMessage.getText().toString();
                    sendMessage(messageText);
                    return true;
                }
                return false;
            }
        });

        lastMessageSeenReference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chat.getChatID() + "/lastMessageSeen");

        // set last message of chat as seen if I'm not the sender
        if (!chat.getLastTextMessage().startsWith("You: ")) {
            lastMessageSeenReference.setValue(true);
        }

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // if a message is added, show it in the recycler view
                Map map = dataSnapshot.getValue(Map.class);
                String senderId = map.get("senderId").toString();
                String senderName = "";
                if (map.get("senderName") != null) {
                    senderName = map.get("senderName").toString();
                }
                String message = map.get("message").toString();
                String timestamp = map.get("timestamp").toString();

                Message messageOb = new Message();
                messageOb.setCreatedTime(timestamp);
                messageOb.setMessageText(message);
                messageOb.setSenderName(senderName);
                messageOb.setSenderId(senderId);
                messages.add(messageOb);
                adapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
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
    protected void onResume() {
        super.onResume();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as live
        currentUser.setOnline(true);
        reference.child("online").setValue(currentUser.isOnline());
    }

    @Override
    protected void onStop() {
        super.onStop();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as dead
        currentUser.setOnline(false);
        reference.child("online").setValue(currentUser.isOnline());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isGroup) {
            getMenuInflater().inflate(R.menu.menu_text_chat_activity_group, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_text_chat_activity, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_text_chat_activity_video_icon) {
            Intent intent = new Intent(TextChatActivity.this, VideoChatActivity.class);
            intent.setAction("Launch from Chat Details");
            intent.putExtra("chat", Parcels.wrap(chat));
            intent.putExtra("user", Parcels.wrap(currentUser));
            startActivity(intent);
            return true;

        } else if (id == R.id.menu_text_chat_activity_edit_icon) {
            Intent intent = new Intent(TextChatActivity.this, CreateGroupActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("chat", Parcels.wrap(chat));
            startActivity(intent);
            return true;

        } else if (id == android.R.id.home) {
            // set last message of chat as seen if I'm not the sender
            if (!chat.getLastTextMessage().startsWith("You: ")) {
                lastMessageSeenReference.setValue(true);
            }

            // intent to chat fragment
            Intent intent = new Intent(TextChatActivity.this, MainActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("fragment", "chat");
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String messageText) {
        String timestamp = new Date().toString();
        if (!messageText.equals("")) {
            // save message
            Map<String, String> map = new HashMap<>();
            map.put("message", messageText);
            map.put("senderId", currentUser.getUserID());
            map.put("senderName", currentUser.getUserName());
            map.put("timestamp", timestamp);
            reference.push().setValue(map);
            etMessage.setText("");

            // set this message to be the lastMessage of the chat
            Firebase chatReference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chat.getChatID());
            chatReference.child("lastMessage").setValue(currentUser.getUserName() + ": " + messageText);
            chatReference.child("lastMessageAt").setValue(timestamp);
            chatReference.child("lastMessageSeen").setValue(false);
        }
    }

    // override onBackPressed so the chat fragment refreshes when we go back
    @Override
    public void onBackPressed() {
        // set last message of chat as seen if I'm not the sender
        if (!chat.getLastTextMessage().startsWith("You: ")) {
            lastMessageSeenReference.setValue(true);
        }
        // intent to chat fragment
        Intent intent = new Intent(TextChatActivity.this, MainActivity.class);
        intent.putExtra("user", Parcels.wrap(currentUser));
        intent.putExtra("fragment", "chat");
        startActivity(intent);
    }
}
