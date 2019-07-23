package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
Fragment that displays the user's open chats (one with each friend) ordered by most recent, can click
on each chat to message that person in the ChatDetailsActivity
*/

public class ChatFragment extends Fragment {

    RecyclerView rvChats;
    private ChatAdapter adapter;
    private List<Chat> chats;
    private SwipeRefreshLayout swipeContainer;
    private TextView tvNoChats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = view.findViewById(R.id.fragment_chat_rv);
        tvNoChats = view.findViewById(R.id.fragment_chat_no_chats_tv);

        chats = new ArrayList<>();
        queryChats();

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
    }

    private void queryChats() {
        String url = "https://lingua-project.firebaseio.com/users/" + MainActivity.currentUser.getId() + "/chats.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONArray array = new JSONArray(s);
                Log.i("ChatFragment", array.toString());
                for (int i = 0; i < array.length(); i++) {
                    queryChatInfo(array.getString(i));
                }
                if (array.length() == 0) {
                    tvNoChats.setVisibility(View.VISIBLE);
                    rvChats.setVisibility(View.GONE);
                } else {
                    tvNoChats.setVisibility(View.GONE);
                    rvChats.setVisibility(View.VISIBLE);
                }
                swipeContainer.setRefreshing(false);

            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            swipeContainer.setRefreshing(false);
            tvNoChats.setText("Oops! There was a connection error.");
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
                String name = chat.getString("name");
                String lastMessage = chat.getString("lastMessage");
                String lastMessageAt = chat.getString("lastMessageAt");
                chats.add(new Chat(id, name, lastMessage, lastMessageAt));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            tvNoChats.setText("Oops! There was a connection error.");
            Log.e("ChatFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(chatInfoRequest);
    }
}