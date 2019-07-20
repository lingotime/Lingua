package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lingua.lingua.supports.EndlessRecyclerViewScrollListener;
import com.lingua.lingua.adapters.ExploreAdapter;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import java.util.ArrayList;
import java.util.List;

/*
Fragment that displays other people's profiles that match the user's target language or country for
the user to browse through and send friend requests
*/

public class ExploreFragment extends Fragment {

    RecyclerView rvExplore;
    private ExploreAdapter adapter;
    private List<User> users;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvExplore = view.findViewById(R.id.fragment_explore_rv);
        users = new ArrayList<>();
        users.add(new User("Briana Douglas"));
        users.add(new User("Fausto Zurita"));
        adapter = new ExploreAdapter(getContext(), users);
        rvExplore.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvExplore.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // TODO: load users
            }
        };
        // Adds the scroll listener to RecyclerView
        rvExplore.addOnScrollListener(scrollListener);

        swipeContainer = view.findViewById(R.id.exploreSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // adapter.clear();
                // TODO: load users
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
