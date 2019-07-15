package com.lingua.lingua;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFragment = new ProfileFragment();
        final Fragment chatFragment = new ChatFragment();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment notificationsFragment = new NotificationsFragment();

        fragmentManager.beginTransaction().replace(R.id.flContainer, profileFragment).commit();

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