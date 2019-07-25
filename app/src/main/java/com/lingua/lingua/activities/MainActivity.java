package com.lingua.lingua.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.firebase.client.Firebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lingua.lingua.R;
import com.lingua.lingua.fragments.ChatFragment;
import com.lingua.lingua.fragments.ExploreFragment;
import com.lingua.lingua.fragments.ConnectFragment;
import com.lingua.lingua.fragments.ProfileFragment;
import com.lingua.lingua.models.User;
import org.parceler.Parcels;

/* FINALIZED, DOCUMENTED, and TESTED: MainActivity displays a fragment, switchable with a bottom navigation. */

public class MainActivity extends AppCompatActivity {
    private User currentUser;

    private FrameLayout fragmentFrame;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // associate views with java variables
        fragmentFrame = findViewById(R.id.activity_main_fragment_frame);
        bottomNavigation = findViewById(R.id.activity_main_bottom_navigation);

        // unwrap the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // manage fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment chatFragment = new ChatFragment();
        final Fragment connectFragment = new ConnectFragment();
        final Fragment profileFragment = new ProfileFragment();

        // set initial fragment as "Explore"
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();

        // set initial selected item as "Explore"
        bottomNavigation.setSelectedItemId(R.id.Explore);

        // allow the bottom navigation to control the fragment view
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", (Parcelable) currentUser);

                switch (item.getItemId()) {
                    case R.id.Chat:
                        chatFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, chatFragment).commit();
                        return true;
                    case R.id.Connect:
                        connectFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, connectFragment).commit();
                        return true;
                    case R.id.Profile:
                        profileFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, profileFragment).commit();
                        return true;
                    default:
                        exploreFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_frame, exploreFragment).commit();
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
    protected void onDestroy() {
        super.onDestroy();

        // mark user as dead
        currentUser.setOnline(false);

        // save update
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }
}