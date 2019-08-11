package com.lingua.lingua;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.lingua.lingua.adapters.SelectFriendsAdapter;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SelectFriendsActivity extends AppCompatActivity {

    RecyclerView rvFriends;
    private SelectFriendsAdapter friendsAdapter;
    private List<User> friends, selectedUsers;
    private TextView noFriendsTv;
    private static final String TAG = "SelectFriendsActivity";
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        Toolbar toolbar = findViewById(R.id.activity_select_friends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add participants");

        rvFriends = findViewById(R.id.activity_friends_rv);
        friends = new ArrayList<>();
        if (getIntent().hasExtra("selectedUsers")) {
            selectedUsers = Parcels.unwrap(getIntent().getParcelableExtra("selectedUsers"));
        } else {
            selectedUsers = new ArrayList<>();
        }

        friendsAdapter = new SelectFriendsAdapter(this, friends, "SelectFriendsActivity");
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvFriends.addItemDecoration(itemDecoration);
        rvFriends.setAdapter(friendsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriends.setLayoutManager(linearLayoutManager);

        noFriendsTv = findViewById(R.id.activity_select_friends_no_users_tv);

        queryFriends();
    }

    private void queryFriends() {
        String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + "/friendIDs.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryFriendInfo(key);
                }
            } catch (JSONException e) {
                noFriendsTv.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
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
                for (User user : selectedUsers) {
                    if (user.getUserID().equals(id)) {
                        friend.setSelected(true);
                    }
                }
                friends.add(friend);
                Collections.sort(friends, (o1, o2) -> o1.getUserName().compareTo(o2.getUserName()));
                friendsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e(TAG, "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_select_friends_next_icon) {
            selectedUsers.clear();
            for (User friend : friends) {
                if (friend.isSelected()) {
                    selectedUsers.add(friend);
                }
            }
            if (selectedUsers.size() > 1) {
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("selectedUsers", Parcels.wrap(selectedUsers));
                intent.putExtra("isNewGroup", true);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Add at least two participants to the group.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home) {
            back();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", Parcels.wrap(currentUser));
        intent.putExtra("fragment", "chat");
        startActivity(intent);
    }
}