package com.lingua.lingua;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.lingua.lingua.adapters.SelectFriendsAdapter;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    private User currentUser;

    RecyclerView rvFriends;
    private SelectFriendsAdapter friendsAdapter;
    private List<User> participants;
    private EditText groupNameEt;
    private String groupName;
    private TextView tvParticipants;
    private static final String TAG = "CreateGroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        participants = Parcels.unwrap(getIntent().getParcelableExtra("selectedUsers"));

        Toolbar toolbar = findViewById(R.id.activity_create_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Group");

        groupNameEt = findViewById(R.id.activity_create_group_name_et);
        tvParticipants = findViewById(R.id.activity_create_group_participants_tv);

        tvParticipants.setText(participants.size() + " participants:");

        rvFriends = findViewById(R.id.activity_create_group_rv);

        friendsAdapter = new SelectFriendsAdapter(this, participants, "CreateGroupActivity");
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvFriends.addItemDecoration(itemDecoration);
        rvFriends.setAdapter(friendsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriends.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_create_group_icon) {
            groupName = groupNameEt.getText().toString();
            createGroup();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("fragment", "chat");
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(this, SelectFriendsActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("selectedUsers", Parcels.wrap(participants));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createGroup() {

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com");

        // create chat between users
        String chatId = reference.child("chats").push().getKey();

        Map<String, Object> chat = new HashMap<>();
        String lastMessage = currentUser.getUserName() + " created group " + groupName;
        String lastMessageAt = new Date().toString();
        chat.put("lastMessage", lastMessage);
        chat.put("lastMessageAt", lastMessageAt);
        chat.put("lastMessageSeen", false);
        chat.put("id", chatId);
        chat.put("name", groupName);

        participants.add(currentUser);

        ArrayList<String> exploreLanguages = new ArrayList<>();
        Map<String, String> users = new HashMap<>();

        users.put(currentUser.getUserID(), "true");
        for (User participant : participants) {
            users.put(participant.getUserID(), "true");

            // add chat reference to users
            reference.child("users").child(participant.getUserID()).child("chats").child(chatId).setValue(true);

            if (participant.getExploreLanguages() != null) {
                for (String language : participant.getExploreLanguages()) {
                    if (!exploreLanguages.contains(language)) {
                        exploreLanguages.add(language);
                    }
                }
            }
        }
        chat.put("users", users);
        chat.put("exploreLanguages", exploreLanguages);

        reference.child("chats").child(chatId).setValue(chat);

        // create message in the new chat
        Map<String, String> message = new HashMap<>();
        message.put("message", "Created group " + groupName);
        message.put("senderId", currentUser.getUserID());
        message.put("timestamp", lastMessageAt);

        reference.child("messages").child(chatId).push().setValue(message);
    }
}
