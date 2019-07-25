package com.lingua.lingua;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.lingua.lingua.models.FriendRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
RecyclerView Adapter that adapts Friend Request objects to the viewholders in the recyclerview
*/

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private List<FriendRequest> friendRequests;
    Firebase reference;

    private ImageView ivProfile;
    private TextView tvMessage, tvName, tvTimestamp, tvDescription;
    private Button acceptButton, rejectButton, cancelButton;

    String userId;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECEIVED_FRIEND_REQUESTS = 1;
    private static final int TYPE_SENT_FRIEND_REQUESTS = 2;

    public NotificationsAdapter(Context context, List<FriendRequest> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;
        Firebase.setAndroidContext(context);
        reference = new Firebase("https://lingua-project.firebaseio.com");

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        Log.i("NotificationsAdapter", userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_RECEIVED_FRIEND_REQUESTS) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend_request_received, parent, false);
            return new NotificationsAdapter.ViewHolder(view);
        } else if (viewType == TYPE_SENT_FRIEND_REQUESTS) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend_request_sent, parent, false);
            return new NotificationsAdapter.ViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FriendRequest friendRequest = friendRequests.get(position);
        tvMessage.setText(friendRequest.getMessage());
        tvTimestamp.setText(DateUtil.getRelativeTimeAgo(friendRequest.getTimestamp()));

        Integer viewType = holder.getItemViewType();

        if (viewType == TYPE_RECEIVED_FRIEND_REQUESTS) {

            tvName.setText(friendRequest.getSenderName());

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptFriendRequest(friendRequest);
                    deleteFriendRequest(friendRequest, position);
//                    Intent intent = new Intent(context, ChatDetailsActivity.class);
//                    context.startActivity(intent);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriendRequest(friendRequest, position);
                    Toast.makeText(context, "Friend request rejected", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (viewType == TYPE_SENT_FRIEND_REQUESTS) {

            tvName.setText(friendRequest.getReceiverName());

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriendRequest(friendRequest, position);
                    Toast.makeText(context, "Friend request deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    // Determines the appropriate ViewType according to the sender of the friend request.
    @Override
    public int getItemViewType(int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        if (friendRequest.getSenderId().equals(userId)) {
            // If the current user is the sender
            return TYPE_SENT_FRIEND_REQUESTS;
        } else {
            // If some other user sent the friend request
            return TYPE_RECEIVED_FRIEND_REQUESTS;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.friend_request_message_tv);
            tvTimestamp = itemView.findViewById(R.id.friend_request_timestamp_tv);
            tvName = itemView.findViewById(R.id.friend_request_name);
            tvDescription = itemView.findViewById(R.id.friend_request_description_tv);
            acceptButton = itemView.findViewById(R.id.friend_request_accept_button);
            rejectButton = itemView.findViewById(R.id.friend_request_reject_button);
            cancelButton = itemView.findViewById(R.id.friend_request_cancel_button);
            ivProfile = itemView.findViewById(R.id.friend_request_iv);
        }
    }

    public void deleteFriendRequest(FriendRequest friendRequest, int position) {
        //delete from friend-requests
        reference.child("friend-requests").child(friendRequest.getId()).removeValue();;

        // delete friend request reference in user objects
        reference.child("users").child(friendRequest.getSenderId()).child("sent-friend-requests").child(friendRequest.getId()).removeValue();
        reference.child("users").child(friendRequest.getReceiverId()).child("received-friend-requests").child(friendRequest.getId()).removeValue();

        friendRequests.remove(position);
        notifyItemRemoved(position);
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        // create chat between users
        String chatId = reference.child("chats").push().getKey();

        Map<String, String> chat = new HashMap<>();
        chat.put("lastMessage", friendRequest.getMessage());
        chat.put("lastMessageAt", friendRequest.getTimestamp());
        chat.put("id", chatId);
        chat.put("name", "false");
        chat.put("user1", friendRequest.getSenderName());
        chat.put("user2", friendRequest.getReceiverName());

        reference.child("chats").child(chatId).setValue(chat);

        // add chat reference to users
        reference.child("users").child(friendRequest.getSenderId()).child("chats").child(chatId).setValue(true);
        reference.child("users").child(friendRequest.getReceiverId()).child("chats").child(chatId).setValue(true);

        // create message in the new chat
        Map<String, String> message = new HashMap<>();
        message.put("message", friendRequest.getMessage());
        message.put("senderId", friendRequest.getSenderId());
        message.put("timestamp", friendRequest.getTimestamp());

        reference.child("messages").child(chatId).push().setValue(message);

        Toast.makeText(context, "Friend request accepted", Toast.LENGTH_SHORT).show();
    }
}