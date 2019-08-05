package com.lingua.lingua.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.NotificationsAdapter;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* Fragment that displays pending friend requests, and possibly in the future also missed calls.
*/

public class NotificationsFragment extends Fragment {
    Context context;

    RecyclerView rvNotifications;
    private NotificationsAdapter adapter;
    private List<FriendRequest> friendRequests;
    private SwipeRefreshLayout swipeContainer;
    private User currentUser;

    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //set the context
        context = getContext();

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.fragment_notifications_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Notifications");

        rvNotifications = view.findViewById(R.id.fragment_notifications_rv);
        friendRequests = new ArrayList<>();
        adapter = new NotificationsAdapter(context, friendRequests, currentUser);
        rvNotifications.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvNotifications.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvNotifications.addItemDecoration(itemDecoration);

        swipeContainer = view.findViewById(R.id.fragment_notifications_swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryFriendRequests();
    }

    private void queryFriendRequests() {
        String urlReceived = "https://lingua-project.firebaseio.com/users/" + userId + "/received-friend-requests.json";
        queryFriendRequests(urlReceived);
        String urlSent = "https://lingua-project.firebaseio.com/users/" + userId + "/sent-friend-requests.json";
        queryFriendRequests(urlSent);
    }

    private void queryFriendRequests(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryFriendRequestInfo(key);
                }
                swipeContainer.setRefreshing(false);
            } catch (JSONException e) {
                if (url.equals("https://lingua-project.firebaseio.com/users/" + userId + "/received-friend-requests.json")) {
                    Toast.makeText(context, "No pending friend requests", Toast.LENGTH_LONG).show();
                }
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
            swipeContainer.setRefreshing(false);
            Log.e("NotificationsFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void queryFriendRequestInfo(String friendRequestId) {
        String url = "https://lingua-project.firebaseio.com/friend-requests/" + friendRequestId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String id = object.getString("id");
                String message = object.getString("message");
                String senderId = object.getString("senderId");
                String senderName = object.getString("senderName");
                String receiverId = object.getString("receiverId");
                String receiverName = object.getString("receiverName");
                String timestamp = object.getString("timestamp");

                ArrayList<String> exploreLanguages = new ArrayList<>();

                if (object.has("exploreLanguages")) {
                    JSONArray objectExploreLanguages = object.getJSONArray("exploreLanguages");
                    for (int index = 0; index < objectExploreLanguages.length(); index++) {
                        exploreLanguages.add((String) objectExploreLanguages.get(index));
                    }
                }

                FriendRequest friendRequest = new FriendRequest();
                friendRequest.setFriendRequestMessage(message);
                friendRequest.setSenderUser(senderId);
                friendRequest.setSenderUserName(senderName);
                friendRequest.setReceiverUser(receiverId);
                friendRequest.setReceiverUserName(receiverName);
                friendRequest.setCreatedTime(timestamp);
                friendRequest.setFriendRequestID(id);
                friendRequest.setExploreLanguages(exploreLanguages);
                friendRequests.add(friendRequest);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e("NotificationsFragment", "" + volleyError);
            swipeContainer.setRefreshing(false);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void refresh() {
        friendRequests.clear();
        adapter.notifyDataSetChanged();
        queryFriendRequests();
    }
}