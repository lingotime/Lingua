package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.models.FriendRequest;

import java.util.List;

/*
RecyclerView Adapter that adapts Friend Request objects to the viewholders in the recyclerview
*/

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private List<FriendRequest> friendRequests;

    private ImageView ivProfile;
    private TextView tvMessage, tvName, tvTimestamp, tvDescription;
    private Button acceptButton, rejectButton, cancelButton;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECEIVED_FRIEND_REQUESTS = 1;
    private static final int TYPE_SENT_FRIEND_REQUESTS = 2;

    public NotificationsAdapter(Context context, List<FriendRequest> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;
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
        Log.i("NotificationsAdapter", friendRequest.getMessage());
        Log.i("NotificationsAdapter", friendRequest.getSenderName()); //TODO: fix layout so it shows
        tvMessage.setText(friendRequest.getMessage());

        Integer viewType = holder.getItemViewType();

        if (viewType == TYPE_RECEIVED_FRIEND_REQUESTS) {
            tvName.setText(friendRequest.getSenderName());

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: add friend, create chat with friend's message
                    Intent intent = new Intent(context, ChatDetailsActivity.class);
                    context.startActivity(intent);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: delete friend request from database, refresh
                }
            });

        } else if (viewType == TYPE_SENT_FRIEND_REQUESTS) {
            tvName.setText(friendRequest.getReceiverName());
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: delete friend request from database
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
        if (friendRequest.getSenderId().equals(MainActivity.currentUser.getId())) {
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
}