package com.lingua.lingua.fragments;

import android.content.Context;
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
import com.lingua.lingua.ChatAdapter;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* Fragment that displays the user's open chats (one with each friend) ordered by most recent, can click
* on each chat to message that person in the ChatDetailsActivity
*/

public class ChatFragment extends Fragment {

    RecyclerView rvChats;
    private Context context;
    private ChatAdapter adapter;
    private List<Chat> chats;
    private SwipeRefreshLayout swipeContainer;
    private static final String TAG = "ChatFragment";
    // used to implement the actions for swiping left or right on each chat object

    User currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_chat_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        rvChats = view.findViewById(R.id.fragment_chat_rv);
        chats = new ArrayList<>();

        adapter = new ChatAdapter(getContext(), chats);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvChats.addItemDecoration(itemDecoration);
        rvChats.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(linearLayoutManager);

        swipeContainer = view.findViewById(R.id.exploreSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryChats();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryChats();
    }

    private void queryChats() {
        String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getId() + "/chats.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryChatInfo(key);
                }
                swipeContainer.setRefreshing(false);
            } catch (JSONException e) {
                Toast.makeText(getContext(), "No chats to display", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.e("ChatFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    private void queryChatInfo(String id) {
        String chatUrl = "https://lingua-project.firebaseio.com/chats/" + id + ".json";
        StringRequest chatInfoRequest = new StringRequest(Request.Method.GET, chatUrl, s -> {
            try {
                JSONObject chat = new JSONObject(s);
                Log.i("ChatFragment", chat.toString());
                String lastMessage = chat.getString("lastMessage");
                String lastMessageAt = chat.getString("lastMessageAt");
                String userName1 = chat.getString("user1");
                String userName2 = chat.getString("user2");
                String name;
                // show name of user I'm texting as chat title
                if (userName1.equals(currentUser.getFirstName())) {
                    name = userName2;
                } else {
                    name = userName1;
                }
                // get list of user ids in the chat
                ArrayList<String> userIds = new ArrayList<>();
                JSONObject users = chat.getJSONObject("users");
                Iterator keys = users.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    Log.d(TAG, key);
                    userIds.add(key);
                }
                chats.add(new Chat(id, name, lastMessage, lastMessageAt, userIds));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.e("ChatFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(chatInfoRequest);
    }
}