package com.lingua.lingua;

import android.os.Bundle;
import android.util.Log;
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
    private List<User> friends;
    private TextView noFriendsTv;
    private static final String TAG = "FriendsActivity";
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        Toolbar toolbar = findViewById(R.id.activity_friends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add participants:");

        rvFriends = findViewById(R.id.activity_friends_rv);
        friends = new ArrayList<>();

        friendsAdapter = new SelectFriendsAdapter(this, friends);
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
}