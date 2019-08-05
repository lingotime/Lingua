package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import com.lingua.lingua.fragments.NotificationsFragment;
import com.lingua.lingua.fragments.ProfileFragment;
import com.lingua.lingua.fragments.SearchFragment;
import com.lingua.lingua.models.User;
import com.lingua.lingua.notifyAPI.BindingIntentService;
import com.twilio.jwt.accesstoken.AccessToken;

import org.parceler.Parcels;

import static com.lingua.lingua.notifyAPI.BindingSharedPreferences.IDENTITY;

/**
 * Main Activity with bottom navigation bar that handles switching between fragments
 */

public class MainActivity extends AppCompatActivity {

    private User currentUser;
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment searchFragment = new SearchFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment exploreFragment = new ExploreFragment();
    final Fragment notificationsFragment = new NotificationsFragment();

    // Strings for creating a binding for push notifications for the device
    public static final String BINDING_REGISTRATION = "BINDING_REGISTRATION";
    public static final String BINDING_SUCCEEDED = "BINDING_SUCCEEDED";
    public static final String BINDING_RESPONSE = "BINDING_RESPONSE";

    private WakefulBroadcastReceiver bindingBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = Parcels.unwrap(this.getIntent().getParcelableExtra("user"));
        Log.i("MainActivity", currentUser.getUserID());
        Log.i("MainActivity", currentUser.getUserName());

        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        prefs.edit().putString("userId", currentUser.getUserID()).apply();
        prefs.edit().putString("userName", currentUser.getUserName()).apply();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", Parcels.wrap(currentUser));

        profileFragment.setArguments(bundle);
        searchFragment.setArguments(bundle);
        chatFragment.setArguments(bundle);
        exploreFragment.setArguments(bundle);
        notificationsFragment.setArguments(bundle);

        // load the right fragment depending on intent extras
        String fragmentToLoad = getIntent().getStringExtra("fragment");

        if (fragmentToLoad != null && fragmentToLoad.equals("profile")) {
            fragmentManager.beginTransaction().replace(R.id.flContainer, profileFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else if (fragmentToLoad != null && fragmentToLoad.equals("notifications")) {
            fragmentManager.beginTransaction().replace(R.id.flContainer, notificationsFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.notifications);
        } else if (fragmentToLoad != null && fragmentToLoad.equals("chats")){
            fragmentManager.beginTransaction().replace(R.id.flContainer, chatFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.chat);
        } else {
            fragmentManager.beginTransaction().replace(R.id.flContainer, exploreFragment).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.explore:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, exploreFragment).commit();
                        return true;
                    case R.id.search:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, searchFragment).commit();
                        return true;
                    case R.id.chat:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, chatFragment).commit();
                        return true;
                    case R.id.notifications:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, notificationsFragment).commit();
                        return true;
                    case R.id.profile:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, profileFragment).commit();
                        return true;
                    default:
                        return true;
                }
            }
        });


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
        reference.child("online").setValue("true");
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
            // Inflate the menu; this adds items to the toolbar if it is present.
            getMenuInflater().inflate(R.menu.menu_chat_fragment, menu);
        } else if (profileFragment != null && profileFragment.isVisible()) {
            getMenuInflater().inflate(R.menu.menu_profile_fragment, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as live
        reference.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as live
        reference.child("online").setValue("false");
    }
}