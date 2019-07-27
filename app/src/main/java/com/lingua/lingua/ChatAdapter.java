package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.lingua.lingua.models.Chat;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

/*
RecyclerView Adapter that adapts Chat objects to the viewholders in the recyclerview
*/

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    public List<Chat> chats;

    private SwipeLayout chatSwipeLayout;
    private ImageView textChatButton;
    private ImageView videoChatButton;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvText;
    private TextView tvTimestamp;

    String userId, userName;

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "");
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
        tvText.setText(chat.getLastMessage());
        tvTimestamp.setText(DateUtil.getRelativeTimeAgo(chat.getLastUpdatedAt()));

        // fill in the user profile pic and name of the friend
        for (String id : chat.getUsers()) {
            if (!id.equals(userId)) {
                getUserDetails(id);
            }
        }

        // to handle the SwipeLayout
        chatSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            chatSwipeLayout = itemView.findViewById(R.id.item_chat_swipe_layout);
            textChatButton = itemView.findViewById(R.id.item_chat_text_chat);
            videoChatButton = itemView.findViewById(R.id.item_chat_video_chat);
            ivProfile = itemView.findViewById(R.id.item_user_iv_profile);
            tvName = itemView.findViewById(R.id.item_chat_tv_name);
            tvText = itemView.findViewById(R.id.item_chat_tv_text);
            tvTimestamp = itemView.findViewById(R.id.item_chat_tv_timestamp);
            itemView.setOnClickListener(this);

            textChatButton.setOnClickListener(new View.OnClickListener() {
                // launch the chat details activity
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ChatDetailsActivity.class);
                        Chat chat = chats.get(position);
                        intent.putExtra("chat", Parcels.wrap(chat));
                        context.startActivity(intent);
                    }
                }
            });

            videoChatButton.setOnClickListener(new View.OnClickListener() {
                // start a video chat and generate a room
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Chat chat = chats.get(position);
                        Intent intent = new Intent(context, VideoChatActivity.class);
                        intent.putExtra("chatID", chat.getId());
                        intent.putExtra("name", chat.getName());
                        // get the second user Id from the
                        for (int i = 0; i < chat.getUsers().size(); i++) {
                            String otherUserId = chat.getUsers().get(i);
                            if (otherUserId != userId) {
                                intent.putExtra("otherUser", otherUserId);
                            }
                        }
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                Chat chat = chats.get(position);
                intent.putExtra("chat", Parcels.wrap(chat));
                context.startActivity(intent);
            }
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
                String name = object.getString("firstName");
                String profilePhotoURL = object.getString("profilePhotoURL");

                // load profile pic
                RequestOptions requestOptionsMedia = new RequestOptions();
                requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
                Glide.with(context)
                        .load(profilePhotoURL)
                        .apply(requestOptionsMedia)
                        .into(ivProfile);

                // set name
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
