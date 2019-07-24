package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lingua.lingua.MainActivity;
import com.lingua.lingua.NotificationsAdapter;
import com.lingua.lingua.R;
import com.lingua.lingua.models.FriendRequest;

import java.util.ArrayList;
import java.util.List;

/*
Fragment that displays pending friend requests, and possibly in the future also missed calls.
*/

public class NotificationsFragment extends Fragment {

    RecyclerView rvNotifications;
    private NotificationsAdapter adapter;
    private List<FriendRequest> friendRequests;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_notifications_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Notifications");

        rvNotifications = view.findViewById(R.id.fragment_notifications_rv);
        friendRequests = new ArrayList<>();

        adapter = new NotificationsAdapter(getContext(), friendRequests);
        rvNotifications.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(linearLayoutManager);

        swipeContainer = view.findViewById(R.id.exploreSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // adapter.clear();
                // TODO: load friend requests
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
