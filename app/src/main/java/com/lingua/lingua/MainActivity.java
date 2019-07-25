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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.fragments.ExploreFragment;
import com.lingua.lingua.fragments.NotificationsFragment;
import com.lingua.lingua.fragments.ProfileFragment;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

/*
Main Activity with bottom navigation bar that handles switching between fragments
*/

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User currentUser = Parcels.unwrap(this.getIntent().getParcelableExtra("user"));
        Log.i("MainActivity", currentUser.getId());
        Log.i("MainActivity", currentUser.getFirstName());

        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        prefs.edit().putString("userId", currentUser.getId()).apply();
        prefs.edit().putString("userName", currentUser.getFirstName()).apply();

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

        fragmentManager.beginTransaction().replace(R.id.flContainer, exploreFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.explore:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, exploreFragment).commit();
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
    }
}