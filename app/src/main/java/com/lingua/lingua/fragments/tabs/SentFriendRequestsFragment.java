package com.lingua.lingua.fragments.tabs;

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
import com.lingua.lingua.adapters.FriendRequestsAdapter;
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

public class SentFriendRequestsFragment extends Fragment {

    Context context;

    RecyclerView rvSentFriendRequests;
    private FriendRequestsAdapter sentAdapter;
    private List<FriendRequest> friendRequestsSent;
    private SwipeRefreshLayout swipeContainer;

    private TextView noFriendRequestsTv;

    User currentUser;

    // newInstance constructor for creating fragment with arguments
    public static SentFriendRequestsFragment newInstance(User user) {
        SentFriendRequestsFragment fragment = new SentFriendRequestsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set the context
        context = getContext();
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // register to receive broadcasts, in this case from the adapter
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("notificationDeleted"));

        return inflater.inflate(R.layout.tab_fragment_friend_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up recycler view for received notifications
        rvSentFriendRequests = view.findViewById(R.id.tab_fragment_friend_requests_rv);
        friendRequestsSent = new ArrayList<>();
        sentAdapter = new FriendRequestsAdapter(context, friendRequestsSent, currentUser);

        rvSentFriendRequests.setAdapter(sentAdapter);
        LinearLayoutManager receivedLinearLayoutManager = new LinearLayoutManager(context);
        rvSentFriendRequests.setLayoutManager(receivedLinearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvSentFriendRequests.addItemDecoration(itemDecoration);

        noFriendRequestsTv = view.findViewById(R.id.tab_fragment_no_friend_requests_tv);
        noFriendRequestsTv.setText("No pending friend requests sent.");
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
        String urlSent = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + "/sentFriendRequests.json";
        StringRequest request = new StringRequest(Request.Method.GET, urlSent, s -> {
            try {
                JSONObject object = new JSONObject(s);
                noFriendRequestsTv.setVisibility(View.GONE);
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
            Log.e("SentFriendRequestsFragment", "" + volleyError);
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

                friendRequestsSent.add(friendRequest);
                Collections.sort(friendRequestsSent, (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime()));
                Collections.reverse(friendRequestsSent);
                sentAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e("SentFriendRequestsFragment", "" + volleyError);
            swipeContainer.setRefreshing(false);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void refresh() {
        friendRequestsSent.clear();
        sentAdapter.notifyDataSetChanged();
        queryFriendRequests();
    }

    // broadcast receiver that listens to messages from the adapter, so it updates the fragment if friend requests are deleted
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (friendRequestsSent.size() == 0) {
                noFriendRequestsTv.setVisibility(View.VISIBLE);
            }
        }
    };
}
