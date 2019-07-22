package com.lingua.lingua;

import android.graphics.Color;
import android.os.Bundle;
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

    Firebase reference1, reference2;
    private User currentUser;
    private User friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        rvMessages = findViewById(R.id.activity_chat_details_rv);
        messages = new ArrayList<>();

        String friendId = getIntent().getStringExtra("userId");
        String friendName = getIntent().getStringExtra("username"); //TODO: show as title in toolbar

        currentUser = new User("Marta"); //TODO: get current signed in user

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://lingua-project.firebaseio.com/messages/" + currentUser.getId() + "/" + friendId);
        reference2 = new Firebase("https://lingua-project.firebaseio.com/messages/" + friendId + "/" + currentUser.getId());

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
                reference1.push().setValue(map);
                reference2.push().setValue(map);
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String senderId = map.get("sender").toString();

                messages.add(new Message(senderId, message)); //TODO: get user from user ID
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
}
