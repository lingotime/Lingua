package com.lingua.lingua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.FriendRequestTabsAdapter;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

/**
 * Fragment that displays pending friend requests, and possibly in the future also missed calls.
 */

public class FriendRequestFragment extends Fragment {

    Context context;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set the context
        context = getContext();
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.fragment_friend_request_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Pending Friend Requests");

        TabLayout tabLayout = view.findViewById(R.id.fragment_friend_request_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Received"));
        tabLayout.addTab(tabLayout.newTab().setText("Sent"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager viewPager = view.findViewById(R.id.fragment_friend_request_view_pager);
        FriendRequestTabsAdapter adapter = new FriendRequestTabsAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}