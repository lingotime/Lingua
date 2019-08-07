package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.client.Firebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.fragments.ExploreFragment;
import com.lingua.lingua.fragments.FriendRequestFragment;
import com.lingua.lingua.fragments.ProfileFragment;
import com.lingua.lingua.fragments.SearchFragment;
import com.lingua.lingua.models.User;
import com.lingua.lingua.notifyAPI.BindingIntentService;

import org.parceler.Parcels;

import static com.lingua.lingua.notifyAPI.BindingSharedPreferences.IDENTITY;

/**
 * Main Activity with bottom navigation bar that handles switching between fragments
 */

public class MainActivity extends AppCompatActivity {
    private User currentUser;

    private FrameLayout fragmentFrame;
    private BottomNavigationView bottomNavigationView;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment exploreFragment = new ExploreFragment();
    final Fragment searchFragment = new SearchFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment friendRequestFragment = new FriendRequestFragment();
    final Fragment profileFragment = new ProfileFragment();

    private WakefulBroadcastReceiver bindingBroadcastReceiver;

    private static final String TAG = "MainActivity";

    // Strings for creating a binding for push notifications for the device
    public static final String BINDING_REGISTRATION = "BINDING_REGISTRATION";
    public static final String BINDING_SUCCEEDED = "BINDING_SUCCEEDED";
    public static final String BINDING_RESPONSE = "BINDING_RESPONSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // associate views with java variables
        fragmentFrame = findViewById(R.id.activity_main_container);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);

        // unwrap the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // prepare fragments with user parcel
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", Parcels.wrap(currentUser));
        exploreFragment.setArguments(bundle);
        searchFragment.setArguments(bundle);
        chatFragment.setArguments(bundle);
        friendRequestFragment.setArguments(bundle);
        profileFragment.setArguments(bundle);

        // store some info globally
        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        prefs.edit().putString("userId", currentUser.getUserID()).apply();
        prefs.edit().putString("userName", currentUser.getUserName()).apply();

        // load the right fragment depending on intent extras
        String nextFragment = getIntent().getStringExtra("fragment");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_container, searchFragment).commit();
                        return true;
                    case R.id.chat:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_container, chatFragment).commit();
                        return true;
                    case R.id.notifications:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_container, friendRequestFragment).commit();
                        return true;
                    case R.id.profile:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_container, profileFragment).commit();
                        return true;
                    default:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_container, exploreFragment).commit();
                        return true;
                }
            }
        });

        if (nextFragment != null && nextFragment.equals("explore")) {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, exploreFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.explore);
        } else if (nextFragment != null && nextFragment.equals("profile")) {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, profileFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else if (nextFragment != null && nextFragment.equals("search")) {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, searchFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.search);
        } else if (nextFragment != null && nextFragment.equals("chat")) {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, chatFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.chat);
        } else if ( nextFragment != null && nextFragment.equals("notifications")) {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, friendRequestFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.notifications);
        } else {
            fragmentManager.beginTransaction().replace(R.id.activity_main_container, exploreFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.explore);
        }

        registerBinding();

        bindingBroadcastReceiver = new WakefulBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean succeeded = intent.getBooleanExtra(BINDING_SUCCEEDED, false);
                String message = intent.getStringExtra(BINDING_RESPONSE);
                if (message == null) {
                    message = "";
                }

                if (succeeded) {
                    Log.d(TAG, "Binding registered. " + message);
                } else {
                    Log.e(TAG, "Binding failed. " + message);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(bindingBroadcastReceiver,
                new IntentFilter(BINDING_REGISTRATION));

        // mark user as live
        currentUser.setOnline(true);

        // save update
        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());
        reference.child("online").setValue(currentUser.isOnline());
    }

    /**
     * Start the IntentService to register this app identity (the user ID) with Twilio Notify
     */
    public void registerBinding() {
        Intent intent = new Intent(this, BindingIntentService.class);
        intent.putExtra(IDENTITY, currentUser.getUserID());
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // display the right toolbar for each fragment
        if (chatFragment != null && chatFragment.isVisible()) {
            getMenuInflater().inflate(R.menu.menu_chat_fragment, menu);
        } else if (profileFragment != null && profileFragment.isVisible()) {
            getMenuInflater().inflate(R.menu.menu_profile_fragment, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_default_fragments, menu);
        }

        return true;
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
}