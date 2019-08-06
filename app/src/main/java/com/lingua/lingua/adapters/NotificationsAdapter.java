package com.lingua.lingua.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.lingua.lingua.DateUtil;
import com.lingua.lingua.R;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* RecyclerView Adapter that adapts Friend Request objects to the viewholders in the recyclerview
*/

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private List<FriendRequest> friendRequests;
    Firebase reference;
    private User currentUser;

    // for the explore languages

    private ImageView ivProfile;
    private TextView tvMessage, tvName, tvTimestamp;
    private Button acceptButton, rejectButton, cancelButton;

    private static final int TYPE_RECEIVED_FRIEND_REQUESTS = 1;
    private static final int TYPE_SENT_FRIEND_REQUESTS = 2;

    public NotificationsAdapter(Context context, List<FriendRequest> friendRequests, User user) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.currentUser = user;

        Firebase.setAndroidContext(context);
        reference = new Firebase("https://lingua-project.firebaseio.com");
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
        holder.setIsRecyclable(false);
        final FriendRequest friendRequest = friendRequests.get(position);
        tvMessage.setText(friendRequest.getFriendRequestMessage());
        tvTimestamp.setText(DateUtil.getRelativeTimeAgo(friendRequest.getCreatedTime()));

        Integer viewType = holder.getItemViewType();

        String name;
        String photoUrl;

        // set different layout depending on whether it's a received or sent friend request
        if (viewType == TYPE_RECEIVED_FRIEND_REQUESTS) {
            name = friendRequest.getSenderUserName();
            photoUrl = friendRequest.getSenderPhotoUrl();

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptFriendRequest(friendRequest);
                    deleteFriendRequest(friendRequest, position);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriendRequest(friendRequest, position);
                }
            });

        } else {
            name = friendRequest.getReceiverUserName();
            photoUrl = friendRequest.getReceiverPhotoUrl();

            tvName.setText(friendRequest.getReceiverUserName());

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriendRequest(friendRequest, position);
                }
            });
        }

        tvName.setText(name);

        RequestOptions requestOptionsMedia = new RequestOptions();
        requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
        Glide.with(context)
                .load(photoUrl)
                .apply(requestOptionsMedia)
                .fallback(R.drawable.man)
                .into(ivProfile);
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    // Determines the appropriate ViewType according to the sender of the friend request.
    @Override
    public int getItemViewType(int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        if (friendRequest.getSenderUser().equals(currentUser.getUserID())) {
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
            acceptButton = itemView.findViewById(R.id.friend_request_accept_button);
            rejectButton = itemView.findViewById(R.id.friend_request_decline_button);
            cancelButton = itemView.findViewById(R.id.friend_request_cancel_button);
            ivProfile = itemView.findViewById(R.id.friend_request_iv);
        }
    }

    public void deleteFriendRequest(FriendRequest friendRequest, int position) {
        //delete from friendRequests
        reference.child("friendRequests").child(friendRequest.getFriendRequestID()).removeValue();;

        // delete friend request reference in user objects
        reference.child("users").child(friendRequest.getSenderUser()).child("sentFriendRequests").child(friendRequest.getFriendRequestID()).removeValue();
        reference.child("users").child(friendRequest.getReceiverUser()).child("receivedFriendRequests").child(friendRequest.getFriendRequestID()).removeValue();

        friendRequests.remove(friendRequest);
        notifyItemRemoved(position);
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        // create chat between users
        String chatId = reference.child("chats").push().getKey();

        Map<String, Object> chat = new HashMap<>();
        chat.put("lastMessage", friendRequest.getSenderUserName() + ": " + friendRequest.getFriendRequestMessage());
        chat.put("lastMessageAt", friendRequest.getCreatedTime());
        chat.put("id", chatId);

        ArrayList<String> exploreLanguages = friendRequest.getExploreLanguages();
        // iterating and adding to avoid duplicates
        ArrayList<String> currentUserExploreLanguages = currentUser.getExploreLanguages();
        for (int index = 0; index < currentUserExploreLanguages.size(); index++) {
            if (!exploreLanguages.contains(currentUserExploreLanguages.get(index))) {
                exploreLanguages.add(currentUserExploreLanguages.get(index));
            }
        }
        chat.put("exploreLanguages", exploreLanguages);

        Map<String, String> users = new HashMap<>();
        users.put(friendRequest.getSenderUser(), "true");
        users.put(friendRequest.getReceiverUser(), "true");
        chat.put("users", users);

        reference.child("chats").child(chatId).setValue(chat);

        // add chat reference to users
        reference.child("users").child(friendRequest.getSenderUser()).child("chats").child(chatId).setValue(true);
        reference.child("users").child(friendRequest.getReceiverUser()).child("chats").child(chatId).setValue(true);

        // update friends
        reference.child("users").child(currentUser.getUserID()).child("friendIDs").child(friendRequest.getSenderUser()).setValue(true);
        reference.child("users").child(friendRequest.getSenderUser()).child("friendIDs").child(currentUser.getUserID()).setValue(true);

        // create message in the new chat
        Map<String, String> message = new HashMap<>();
        message.put("message", friendRequest.getFriendRequestMessage());
        message.put("senderId", friendRequest.getSenderUser());
        message.put("timestamp", friendRequest.getCreatedTime());

        reference.child("messages").child(chatId).push().setValue(message);

        Toast.makeText(context, "Friend request accepted", Toast.LENGTH_SHORT).show();
    }
}