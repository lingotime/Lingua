package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private List<FriendRequest> friendRequests;

    private ImageView ivProfile;
    private TextView tvMessage;
    private TextView tvName;
    private TextView tvTimestamp;
    private Button acceptButton;
    private Button rejectButton;

    public NotificationsAdapter(Context context, List<FriendRequest> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FriendRequest friendRequest = friendRequests.get(position);
        final User user = friendRequest.getSender();
        tvMessage.setText(friendRequest.getMessage());
        tvName.setText(user.getName());

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add friend, create chat with friend's message
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("user", Parcels.wrap(user));
                context.startActivity(intent);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: delete friend request from database, refresh
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.friend_request_iv);
            tvMessage = itemView.findViewById(R.id.friend_request_message_tv);
            tvName = itemView.findViewById(R.id.friend_request_name);
            tvTimestamp = itemView.findViewById(R.id.friend_request_timestamp_tv);
            acceptButton = itemView.findViewById(R.id.friend_request_accept_button);
            rejectButton = itemView.findViewById(R.id.friend_request_reject_button);
        }
    }
}