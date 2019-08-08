package com.lingua.lingua.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.SelectFriendsActivity;
import com.lingua.lingua.TextChatActivity;
import com.lingua.lingua.VideoChatActivity;
import com.lingua.lingua.adapters.ChatAdapter;
import com.lingua.lingua.models.Chat;
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
* Fragment that displays the user's open chats (one with each friend) ordered by most recent, can click
* on each chat to message that person in the TextChatActivity
*/

public class ChatFragment extends Fragment {

    RecyclerView rvChats;
    private Context context;
    private ChatAdapter adapter;
    private List<Chat> chats;
    private SwipeRefreshLayout swipeContainer;
    private TextView noChatsTv;
    private static final String TAG = "ChatFragment";
    private Paint p = new Paint();
    // used to implement the actions for swiping left or right on each chat object

    User currentUser;
    String lastMessageAt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        context = getContext();
        lastMessageAt = "";
        // register to receive broadcasts, in this case from the chat adapter
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("lastMessageChanged"));

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.fragment_chat_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_chat_fragment_group_icon) {
                    Intent intent = new Intent(context, SelectFriendsActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentUser));
                    context.startActivity(intent);
                }

                return ChatFragment.super.onOptionsItemSelected(item);
            }
        });

        noChatsTv = view.findViewById(R.id.fragment_chat_no_chats_tv);

        rvChats = view.findViewById(R.id.fragment_chat_rv);
        chats = new ArrayList<>();

        adapter = new ChatAdapter(context, chats, currentUser);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvChats.addItemDecoration(itemDecoration);
        rvChats.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvChats.setLayoutManager(linearLayoutManager);

        swipeContainer = view.findViewById(R.id.fragment_chat_swipe_container);
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
        enableSwipe();


    }

    private void queryChats() {
        String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + "/chats.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                Iterator keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    queryChatInfo(key);
                }
            } catch (JSONException e) {
                noChatsTv.setVisibility(View.VISIBLE);
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.e("ChatFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void queryChatInfo(String id) {
        String chatUrl = "https://lingua-project.firebaseio.com/chats/" + id + ".json";
        StringRequest chatInfoRequest = new StringRequest(Request.Method.GET, chatUrl, s -> {
            try {
                JSONObject chat = new JSONObject(s);

                String name = "";

                if (chat.has("name")) {
                    name = chat.getString("name");
                }

                String lastMessageAt = chat.getString("lastMessageAt");
                boolean lastMessageSeen = chat.getBoolean("lastMessageSeen");

                String lastMessage = chat.getString("lastMessage");
                if (lastMessage.startsWith(currentUser.getUserName())) {
                    lastMessage = "You" + lastMessage.split(currentUser.getUserName())[1];
                    lastMessageSeen = true;
                } else if (!chat.has("name")) {
                    lastMessage = lastMessage.split(": ")[1];
                }

                // get list of user ids in the chat
                ArrayList<String> userIds = new ArrayList<>();
                JSONObject users = chat.getJSONObject("users");
                Iterator keys = users.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    userIds.add(key);
                }

                // get the explore languages of all users in the chat
                ArrayList<String> exploreLanguages = new ArrayList<>();
                if (chat.has("exploreLanguages")) {
                    JSONArray chatExploreLanguages = chat.getJSONArray("exploreLanguages");
                    if (chatExploreLanguages != null) {
                        for (int index = 0; index < chatExploreLanguages.length(); index ++) {
                            exploreLanguages.add((String) chatExploreLanguages.get(index));
                        }
                    }
                }

                Chat chatOb = new Chat();
                chatOb.setChatID(id);
                chatOb.setChatName(name);
                chatOb.setLastTextChatTime(lastMessageAt);
                chatOb.setChatParticipants(userIds);
                chatOb.setLastTextMessage(lastMessage);
                chatOb.setChatLanguages(exploreLanguages);
                chatOb.setLastMessageSeen(lastMessageSeen);

                if (userIds.size() == 2) {
                    for (String userID : userIds) {
                        if (!userID.equals(currentUser.getUserID())) {
                            getUserDetails(userID, chatOb);
                        }
                    }
                } else {
                    swipeContainer.setRefreshing(false);
                    chats.add(chatOb);
                    Collections.sort(chats, (o1, o2) -> o1.getLastTextChatTime().compareTo(o2.getLastTextChatTime()));
                    Collections.reverse(chats);
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.e("ChatFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(chatInfoRequest);
    }

    public void getUserDetails(String userId, Chat chat) {
        String url = "https://lingua-project.firebaseio.com/users/" + userId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String name = object.getString("userName");
                String profilePhotoURL = object.getString("userProfilePhotoURL");

                // set chat info
                chat.setChatName(name);
                chat.setChatPhotoUrl(profilePhotoURL);

                swipeContainer.setRefreshing(false);
                chats.add(chat);
                Collections.sort(chats, (o1, o2) -> o1.getLastTextChatTime().compareTo(o2.getLastTextChatTime()));
                Collections.reverse(chats);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
            }
        }, volleyError -> {
            Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.e(TAG, "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {

                    if (position != RecyclerView.NO_POSITION) {
                        Chat chat = chats.get(position);

                        // creating the dialog for selecting the language of the call
                        Intent intent = new Intent(context, TextChatActivity.class);
                        // intent to the video chat activity
                        intent.putExtra("chat", Parcels.wrap(chat));
                        intent.putExtra("user", Parcels.wrap(currentUser));
                        context.startActivity(intent);
                    }

                } else {

                    if (position != RecyclerView.NO_POSITION) {
                        Chat chat = chats.get(position);

                        // creating the dialog for selecting the language of the call
                        Intent intent = new Intent(context, VideoChatActivity.class);
                        // intent to the video chat activity
                        intent.setAction("Launch from Chat Fragment");
                        intent.putExtra("chat", Parcels.wrap(chat));
                        intent.putExtra("user", Parcels.wrap(currentUser));
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#6E2FDE"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = tintBitmap(getBitmap(R.drawable.camera), Color.WHITE);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#17A0F8"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = tintBitmap(getBitmap(R.drawable.text_message), Color.WHITE);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvChats);
    }

    // methods to help set up the right images on swiped
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Bitmap tintBitmap(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    // broadcast receiver that listens to messages from the adapter, so it updates the fragment
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String timestamp = intent.getStringExtra("lastMessageAt");
            if (!timestamp.equals(lastMessageAt)) {
                lastMessageAt = timestamp;
                adapter.clear();
                queryChats();
            }
            lastMessageAt = timestamp;
        }
    };
}