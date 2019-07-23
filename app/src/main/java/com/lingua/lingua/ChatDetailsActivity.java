package com.lingua.lingua;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.models.Message;
import com.lingua.lingua.models.User;

import java.util.ArrayList;
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

    Firebase reference;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        rvMessages = findViewById(R.id.activity_chat_details_rv);
        messages = new ArrayList<>();

        String chatId = getIntent().getStringExtra("chatId");
        String name = getIntent().getStringExtra("name"); //TODO: show as title in toolbar, if chat is not a group show name of friend

        currentUser = ChatFragment.currentUser; //TODO: get current signed in user

        Firebase.setAndroidContext(this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chatId);
        Log.i("ChatDetailsActivity", chatId);

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

            if (!messageText.equals("")) {
                Map<String, String> map = new HashMap<>();
                map.put("message", messageText);
                map.put("sender", currentUser.getId());
                reference.push().setValue(map);
                etMessage.setText("");

                // save this message as the lastMessage of the chat
                Firebase chatReference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chatId);
                chatReference.child("lastMessage").setValue(messageText);
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                if (map.get("sender") != null && map.get("message") != null) {
                    String senderId = map.get("sender").toString();
                    String message = map.get("message").toString();
                    Log.i("ChatDetailsActivity", message + chatId);
                    messages.add(new Message(senderId, message));
                    adapter.notifyDataSetChanged();
                }
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
}
