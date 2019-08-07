package com.lingua.lingua.fragments.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.NotificationsAdapter;
import com.lingua.lingua.models.FriendRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReceivedFriendRequestsFragment extends Fragment {
    Context context;

    RecyclerView rvReceivedNotifications;
    private NotificationsAdapter receivedAdapter;
    private List<FriendRequest> friendRequestsReceived;
    private SwipeRefreshLayout swipeContainer;

    private TextView noFriendRequestsTv;

    String userId, userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set the context
        context = getContext();

        // register to receive broadcasts, in this case from the adapter
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("notificationDeleted"));

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "");

        return inflater.inflate(R.layout.tab_fragment_friend_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up recycler view for received notifications
        rvReceivedNotifications = view.findViewById(R.id.tab_fragment_friend_requests_rv);
        friendRequestsReceived = new ArrayList<>();
        receivedAdapter = new NotificationsAdapter(context, friendRequestsReceived);

        rvReceivedNotifications.setAdapter(receivedAdapter);
        LinearLayoutManager receivedLinearLayoutManager = new LinearLayoutManager(context);
        rvReceivedNotifications.setLayoutManager(receivedLinearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvReceivedNotifications.addItemDecoration(itemDecoration);

        noFriendRequestsTv = view.findViewById(R.id.tab_fragment_no_friend_requests_tv);
        swipeContainer = view.findViewById(R.id.tab_fragment_friend_request_swipe_container);
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
        StringRequest request = new StringRequest(Request.Method.GET, urlReceived, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryFriendRequestInfo(key);
                }
                swipeContainer.setRefreshing(false);
            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                noFriendRequestsTv.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            noFriendRequestsTv.setVisibility(View.VISIBLE);
            Log.e("ReceivedFriendRequestsFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void queryFriendRequestInfo(String friendRequestId) {
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

                friendRequestsReceived.add(friendRequest);
                Collections.sort(friendRequestsReceived, (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime()));
                Collections.reverse(friendRequestsReceived);
                receivedAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                noFriendRequestsTv.setVisibility(View.VISIBLE);
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e("ReceivedFriendRequestsFragment", "" + volleyError);
            swipeContainer.setRefreshing(false);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void refresh() {
        friendRequestsReceived.clear();
        receivedAdapter.notifyDataSetChanged();
        queryFriendRequests();
    }

    // broadcast receiver that listens to messages from the adapter, so it updates the fragment if friend requests are deleted
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (friendRequestsReceived.size() == 0) {
                noFriendRequestsTv.setVisibility(View.VISIBLE);
            }
        }
    };
}
