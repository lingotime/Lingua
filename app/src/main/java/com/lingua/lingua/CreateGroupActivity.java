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

import com.lingua.lingua.adapters.SelectFriendsAdapter;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private User currentUser;

    RecyclerView rvFriends;
    private SelectFriendsAdapter friendsAdapter;
    private List<User> friends;
    private EditText groupNameEt;
    private TextView tvParticipants;
    private static final String TAG = "CreateGroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        friends = Parcels.unwrap(getIntent().getParcelableExtra("selectedUsers"));

        Toolbar toolbar = findViewById(R.id.activity_create_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Group");

        groupNameEt = findViewById(R.id.activity_create_group_name_et);
        tvParticipants = findViewById(R.id.activity_create_group_participants_tv);

        tvParticipants.setText(friends.size() + " participants:");

        rvFriends = findViewById(R.id.activity_create_group_rv);

        friendsAdapter = new SelectFriendsAdapter(this, friends, "CreateGroupActivity");
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvFriends.addItemDecoration(itemDecoration);
        rvFriends.setAdapter(friendsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriends.setLayoutManager(linearLayoutManager);
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
            // TODO: create group
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("fragment", "chat");
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(this, SelectFriendsActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("selectedUsers", Parcels.wrap(friends));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
