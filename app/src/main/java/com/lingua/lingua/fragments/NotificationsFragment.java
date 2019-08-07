package com.lingua.lingua.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
* Fragment that displays pending friend requests, and possibly in the future also missed calls.
*/

public class NotificationsFragment extends Fragment {
    Context context;

    RecyclerView rvReceivedNotifications, rvSentNotifications;
    private NotificationsAdapter receivedAdapter, sentAdapter;
    private List<FriendRequest> friendRequestsReceived, friendRequestsSent;
    private SwipeRefreshLayout swipeContainer;
    private User currentUser;

    private TextView receivedHeader, sentHeader, noNotificationsTv;

    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set the context
        context = getContext();

        // register to receive broadcasts, in this case from the adapter
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("notificationDeleted"));

        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        userId = currentUser.getUserID();

        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.fragment_notifications_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Notifications");

        // set up recycler view for received notifications
        rvReceivedNotifications = view.findViewById(R.id.fragment_notifications_received_rv);
        friendRequestsReceived = new ArrayList<>();
        receivedAdapter = new NotificationsAdapter(context, friendRequestsReceived);

        rvReceivedNotifications.setAdapter(receivedAdapter);
        LinearLayoutManager receivedLinearLayoutManager = new LinearLayoutManager(context);
        rvReceivedNotifications.setLayoutManager(receivedLinearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvReceivedNotifications.addItemDecoration(itemDecoration);

        // set up recycler view for sent notifications
        rvSentNotifications = view.findViewById(R.id.fragment_notifications_sent_rv);
        friendRequestsSent = new ArrayList<>();
        sentAdapter = new NotificationsAdapter(context, friendRequestsSent);

        rvSentNotifications.setAdapter(sentAdapter);
        LinearLayoutManager sentLinearLayoutManager = new LinearLayoutManager(context);
        rvSentNotifications.setLayoutManager(sentLinearLayoutManager);

        rvSentNotifications.addItemDecoration(itemDecoration);

        receivedHeader = view.findViewById(R.id.fragment_notifications_header_received);
        sentHeader = view.findViewById(R.id.fragment_notifications_header_sent);
        noNotificationsTv = view.findViewById(R.id.fragment_notifications_no_notifications_tv);
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
        String urlReceived = "https://lingua-project.firebaseio.com/users/" + userId + "/receivedFriendRequests.json";
        queryFriendRequests(urlReceived, "received");
    }

    private void queryFriendRequests(String url, String type) {
        String urlSent = "https://lingua-project.firebaseio.com/users/" + userId + "/sentFriendRequests.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryFriendRequestInfo(key, type);
                }
                if (type.equals("received")) {
                    receivedHeader.setVisibility(View.VISIBLE);
                    queryFriendRequests(urlSent, "sent");
                } else {
                    sentHeader.setVisibility(View.VISIBLE);
                    swipeContainer.setRefreshing(false);
                }
            } catch (JSONException e) {
                if (type.equals("received")) {
                    queryFriendRequests(urlSent, "sent");
                } else {
                    swipeContainer.setRefreshing(false);
                    if (receivedHeader.getVisibility() == View.GONE) {
                        noNotificationsTv.setVisibility(View.VISIBLE);
                    }
                }
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            noNotificationsTv.setVisibility(View.VISIBLE);
            Log.e("NotificationsFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void queryFriendRequestInfo(String friendRequestId, String type) {
        String url = "https://lingua-project.firebaseio.com/friendRequests/" + friendRequestId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String id = object.getString("id");
                String message = object.getString("message");
                String senderId = object.getString("senderId");
                String senderName = object.getString("senderName");
                String senderPhotoUrl = object.getString("senderPhotoUrl");
                String receiverId = object.getString("receiverId");
                String receiverName = object.getString("receiverName");
                String receiverPhotoUrl = object.getString("receiverPhotoUrl");
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
                friendRequest.setSenderPhotoUrl(senderPhotoUrl);
                friendRequest.setReceiverUser(receiverId);
                friendRequest.setReceiverUserName(receiverName);
                friendRequest.setReceiverPhotoUrl(receiverPhotoUrl);
                friendRequest.setCreatedTime(timestamp);
                friendRequest.setFriendRequestID(id);
                friendRequest.setExploreLanguages(exploreLanguages);

                if (type.equals("received")) {
                    friendRequestsReceived.add(friendRequest);
                    Collections.sort(friendRequestsReceived, (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime()));
                    Collections.reverse(friendRequestsReceived);
                    receivedAdapter.notifyDataSetChanged();
                } else {
                    friendRequestsSent.add(friendRequest);
                    Collections.sort(friendRequestsSent, (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime()));
                    Collections.reverse(friendRequestsSent);
                    sentAdapter.notifyDataSetChanged();
                }

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
        friendRequestsReceived.clear();
        friendRequestsSent.clear();
        receivedAdapter.notifyDataSetChanged();
        sentAdapter.notifyDataSetChanged();
        queryFriendRequests();
    }

    // broadcast receiver that listens to messages from the adapter, so it updates the fragment if friend requests are deleted
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (friendRequestsReceived.size() == 0) {
                receivedHeader.setVisibility(View.GONE);
            }
            if (friendRequestsSent.size() == 0) {
                sentHeader.setVisibility(View.GONE);
            }
            if (friendRequestsReceived.size() == 0 && friendRequestsSent.size() == 0) {
                noNotificationsTv.setVisibility(View.VISIBLE);
            }
        }
    };
}