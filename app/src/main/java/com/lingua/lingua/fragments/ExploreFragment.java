package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lingua.lingua.EndlessRecyclerViewScrollListener;
import com.lingua.lingua.ExploreAdapter;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Iterator;
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

    User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        Log.i("ExploreFragment", currentUser.getId());
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvExplore = view.findViewById(R.id.fragment_explore_rv);
        users = new ArrayList<>();
        queryUsers();
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
                adapter.clear();
                queryUsers();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void queryUsers() {
        String url = "https://lingua-project.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    Object key = keys.next();
                    JSONObject userObject = object.getJSONObject((String) key);
                    String id = userObject.getString("id");
                    String name = userObject.getString("firstName");
                    User user = new User(id, name);
                    users.add(user);
                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                Toast.makeText(getContext(), "No users to display", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, volleyError -> {
            swipeContainer.setRefreshing(false);
            Log.e("ExploreFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }
}
