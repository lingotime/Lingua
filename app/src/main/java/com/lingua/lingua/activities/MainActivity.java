package com.lingua.lingua.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

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

/* FINALIZED, DOCUMENTED, and TESTED: MainActivity displays a fragment, switchable with a bottom navigation. */

public class MainActivity extends AppCompatActivity {
    private FrameLayout fragmentFrame;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // associate views with java variables
        fragmentFrame = findViewById(R.id.activity_main_fragment_frame);
        bottomNavigation = findViewById(R.id.activity_main_bottom_navigation);

        // manage fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment chatFragment = new ChatFragment();
        final Fragment connectFragment = new ConnectFragment();
        final Fragment profileFragment = new ProfileFragment();

        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();

        bottomNavigation.setSelectedItemId(R.id.Explore);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Chat:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, chatFragment).commit();
                        return true;
                    case R.id.Connect:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, connectFragment).commit();
                        return true;
                    case R.id.Profile:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, profileFragment).commit();
                        return true;
                    default:
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();
                        return true;
                }
            }
        });
    }
}