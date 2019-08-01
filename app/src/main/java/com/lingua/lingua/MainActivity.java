package com.lingua.lingua;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.firebase.client.Firebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.fragments.ExploreFragment;
import com.lingua.lingua.fragments.NotificationsFragment;
import com.lingua.lingua.fragments.ProfileFragment;
import com.lingua.lingua.fragments.SearchFragment;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

/**
* Main Activity with bottom navigation bar that handles switching between fragments
*/

public class MainActivity extends AppCompatActivity {
    private User currentUser;
    BottomNavigationView bottomNavigationView;

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

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        final Fragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        final Fragment exploreFragment = new ExploreFragment();
        exploreFragment.setArguments(bundle);
        final Fragment notificationsFragment = new NotificationsFragment();
        notificationsFragment.setArguments(bundle);
        final Fragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.flContainer, exploreFragment).commit();

        bottomNavigationView.setSelectedItemId(R.id.explore);

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

        // mark user as live
        currentUser.setOnline(true);

        // save update
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // mark user as live
        currentUser.setOnline(true);

        // save update
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // mark user as dead
        currentUser.setOnline(false);

        // save update
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }
}