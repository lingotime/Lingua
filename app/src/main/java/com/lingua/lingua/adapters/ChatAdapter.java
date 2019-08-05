package com.lingua.lingua.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lingua.lingua.TextChatActivity;
import com.lingua.lingua.DateUtil;
import com.lingua.lingua.R;
import com.lingua.lingua.VideoChatActivity;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

/**
* RecyclerView Adapter that adapts Chat objects to the viewholders in the recyclerview
*/

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    public List<Chat> chats;

    private CardView chatCard;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvText;
    private TextView tvTimestamp;

    String userId, userName;
    User currentUser;
    String nameToDisplay;


    public ChatAdapter(Context context, List<Chat> chats, User user) {
        this.context = context;
        this.chats = chats;

        currentUser = user;
        userId = currentUser.getUserID();
        userName = currentUser.getUserName();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
        Chat chat = chats.get(position);
        tvText.setText(chat.getLastTextMessage());
        tvTimestamp.setText(DateUtil.getRelativeTimeAgo(chat.getLastTextChatTime()));

        // fill in the user profile pic and name of the friend
        for (String id : chat.getChatParticipants()) {
            if (!id.equals(userId)) {
                getUserDetails(id);
            }
        }

        // set an event listener to update the last message and timestamp of the chat if there is a change
        Firebase.setAndroidContext(context);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chat.getChatID());
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("ChatAdapter", s);
                String key = (String) dataSnapshot.getKey();
                if (key.equals("lastMessage")) {
                    // display last message with the other user's name or with "You: " if current user is the sender
                    String lastMessageRaw = (String) dataSnapshot.getValue();
                    if (lastMessageRaw != null && lastMessageRaw.startsWith(currentUser.getUserName())) {
                        String lastMessage = "You" + lastMessageRaw.split(currentUser.getUserName())[1];
                        tvText.setText(lastMessage);
                    } else {
                        tvText.setText(lastMessageRaw);
                    }
                } else if (key.equals("lastMessageAt")) {
                    String lastMessageTimestamp = (String) dataSnapshot.getValue();
                    tvTimestamp.setText(DateUtil.getRelativeTimeAgo(lastMessageTimestamp));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            chatCard = itemView.findViewById(R.id.item_chat_card);
            ivProfile = itemView.findViewById(R.id.item_chat_iv);
            tvName = itemView.findViewById(R.id.item_chat_tv_name);
            tvText = itemView.findViewById(R.id.item_chat_tv_text);
            tvTimestamp = itemView.findViewById(R.id.item_chat_tv_timestamp);

            chatCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, TextChatActivity.class);
                        Chat chat = chats.get(position);
                        intent.putExtra("chat", Parcels.wrap(chat));
                        intent.putExtra("user", Parcels.wrap(currentUser));
                        intent.putExtra("nameToDisplay", nameToDisplay);
                        context.startActivity(intent);
                    }
                }
            });
        }

    }

    // Clean all elements of the recycler
    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    public void getUserDetails(String userId) {
        String url = "https://lingua-project.firebaseio.com/users/" + userId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String name = object.getString("userName");
                String profilePhotoURL = object.getString("userProfilePhotoURL");

                // load profile pic
                RequestOptions requestOptionsMedia = new RequestOptions();
                requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
                Glide.with(context)
                        .load(profilePhotoURL)
                        .apply(requestOptionsMedia)
                        .fallback(R.drawable.man)
                        .into(ivProfile);

                // set name
                nameToDisplay = name;
                tvName.setText(name);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e("ChatAdapter", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }
}
