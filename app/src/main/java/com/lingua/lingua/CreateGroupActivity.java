package com.lingua.lingua;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.lingua.lingua.adapters.SelectFriendsAdapter;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
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
    Firebase reference;
    Chat chat;
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameEt = findViewById(R.id.activity_create_group_name_et);
        tvParticipants = findViewById(R.id.activity_create_group_participants_tv);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (getIntent().hasExtra("selectedUsers")) {
            participants = Parcels.unwrap(getIntent().getParcelableExtra("selectedUsers"));
        }

        isEdit = false;
        if (getIntent().hasExtra("chat")) {
            chat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));
            isEdit = true;
            groupNameEt.setText(chat.getChatName());
            participants = new ArrayList<>();
            for (String userId : chat.getChatParticipants()) {
                queryFriendInfo(userId);
            }
            //TODO: prefill image
        }

        Firebase.setAndroidContext(this);
        reference = new Firebase("https://lingua-project.firebaseio.com");

        Toolbar toolbar = findViewById(R.id.activity_create_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (isEdit) {
            getSupportActionBar().setTitle("Edit Group");
        } else {
            getSupportActionBar().setTitle("New Group");
        }

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
            if (!groupName.equals("")) {
                if (isEdit) {
                    saveEdits();
                } else {
                    createGroup();
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("fragment", "chat");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Must enter a group name", Toast.LENGTH_SHORT);
            }
            return true;
        } else if (id == android.R.id.home) {
            if (isEdit) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("fragment", "chat");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SelectFriendsActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("selectedUsers", Parcels.wrap(participants));
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createGroup() {

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

    private void saveEdits() {
        reference.child("chats").child(chat.getChatID()).child("name").setValue(groupName);
        //TODO: save image
    }

    private void queryFriendInfo(String friendId) {
        String url = "https://lingua-project.firebaseio.com/users/" + friendId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String id = object.getString("userID");
                String name = object.getString("userName");
                String bio = object.getString("userBiographyText");
                String profilePhotoURL = object.getString("userProfilePhotoURL");

                User friend = new User();
                friend.setUserID(id);
                friend.setUserName(name);
                friend.setUserProfilePhotoURL(profilePhotoURL);
                friend.setUserBiographyText(bio);
                friend.setSelected(false);
                participants.add(friend);
                Collections.sort(participants, (o1, o2) -> o1.getUserName().compareTo(o2.getUserName()));
                friendsAdapter.notifyDataSetChanged();
                tvParticipants.setText(participants.size() + " participants:");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e(TAG, "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

}
