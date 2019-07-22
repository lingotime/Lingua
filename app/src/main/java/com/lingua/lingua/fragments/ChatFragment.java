package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lingua.lingua.adapters.ChatAdapter;
import com.lingua.lingua.R;
import com.lingua.lingua.models.Conversation;
import com.lingua.lingua.models.User;

import java.util.ArrayList;
import java.util.List;

/*
Fragment that displays the user's open chats (one with each friend) ordered by most recent, can click
on each chat to message that person in the TextChatActivity
*/

public class ChatFragment extends Fragment {

    RecyclerView rvChats;
    private ChatAdapter adapter;
    private List<Conversation> chats;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = view.findViewById(R.id.fragment_chat_history_timeline);
        chats = new ArrayList<>();

        ArrayList<User> users = new ArrayList<>();
        /*users.add(new User("Cristina"));
        users.add(new User("Marta"));
        chats.add(new Conversation(users, "Hi! how are you doing?"));
        chats.add(new Conversation(users, "Wassuppppp"));*/

        adapter = new ChatAdapter(getContext(), chats);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvChats.addItemDecoration(itemDecoration);
        rvChats.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(linearLayoutManager);

        swipeContainer = view.findViewById(R.id.fragment_chat_swipe_container);
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