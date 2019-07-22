package com.lingua.lingua.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lingua.lingua.R;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.fragments.ExploreFragment;
import com.lingua.lingua.fragments.ConnectFragment;
import com.lingua.lingua.fragments.ProfileFragment;

/*
Main Activity with bottom navigation bar that handles switching between fragments
*/

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFragment = new ProfileFragment();
        final Fragment chatFragment = new ChatFragment();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment notificationsFragment = new ConnectFragment();

        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Explore:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();
                        return true;
                    case R.id.Chat:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, chatFragment).commit();
                        return true;
                    case R.id.Connect:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, notificationsFragment).commit();
                        return true;
                    case R.id.Profile:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, profileFragment).commit();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
}