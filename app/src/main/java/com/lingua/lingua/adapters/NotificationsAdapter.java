package com.lingua.lingua.adapters;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lingua.lingua.DateUtil;
import com.lingua.lingua.R;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    private User user;

    // for the explore languages

    private ImageView ivProfile;
    private TextView tvMessage, tvName, tvTimestamp, tvDescription;
    private Button acceptButton, rejectButton, cancelButton;

    String userId;

    private static final int TYPE_RECEIVED_FRIEND_REQUESTS = 1;
    private static final int TYPE_SENT_FRIEND_REQUESTS = 2;

    public NotificationsAdapter(Context context, List<FriendRequest> friendRequests, User user) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.user = user;

        Firebase.setAndroidContext(context);
        reference = new Firebase("https://lingua-project.firebaseio.com");

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
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
        tvMessage.setText(friendRequest.getFriendRequestMessage());
        tvTimestamp.setText(DateUtil.getRelativeTimeAgo(friendRequest.getCreatedTime()));

        Integer viewType = holder.getItemViewType();

        if (viewType == TYPE_RECEIVED_FRIEND_REQUESTS) {

            tvName.setText(friendRequest.getSenderUserName());

            loadProfilePic(friendRequest.getSenderUser());

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptFriendRequest(friendRequest);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriendRequest(friendRequest);
                }
            });

        } else if (viewType == TYPE_SENT_FRIEND_REQUESTS) {

            tvName.setText(friendRequest.getReceiverUserName());

            loadProfilePic(friendRequest.getReceiverUser());

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelFriendRequest(friendRequest);
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
        if (friendRequest.getSenderUser().equals(userId)) {
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
            rejectButton = itemView.findViewById(R.id.friend_request_decline_button);
            cancelButton = itemView.findViewById(R.id.friend_request_cancel_button);
            ivProfile = itemView.findViewById(R.id.friend_request_iv);
        }
    }

    public void deleteFriendRequest(FriendRequest friendRequest) {
        //delete from friend-requests
        reference.child("friend-requests").child(friendRequest.getFriendRequestID()).removeValue();;

        // delete friend request reference in user objects
        reference.child("users").child(friendRequest.getSenderUser()).child("sent-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();
        reference.child("users").child(friendRequest.getReceiverUser()).child("received-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();

        friendRequests.remove(friendRequest);

        // handle Fausto's scheme for friendship -- BEGIN
        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // extract the sender user as a User object from the database request's response
                    JSONObject usersJSONObject = new JSONObject(response);
                    JSONObject senderUserJSONObject = usersJSONObject.getJSONObject(friendRequest.getSenderUser());

                    Gson gson = new Gson();
                    User senderUser = gson.fromJson(senderUserJSONObject.toString(), User.class);

                    if (senderUser.getKnownLanguages() == null) {
                        senderUser.setKnownLanguages(new ArrayList<String>());
                    }

                    if (senderUser.getExploreLanguages() == null) {
                        senderUser.setExploreLanguages(new ArrayList<String>());
                    }

                    if (senderUser.getExploreCountries() == null) {
                        senderUser.setExploreCountries(new ArrayList<String>());
                    }

                    // remove the sender user from the current user's pending received friend requests list
                    if (user.getPendingReceivedFriendRequests() != null) {
                        user.getPendingReceivedFriendRequests().remove(senderUser.getUserID());
                    }

                    // add the sender user as a declined user of the current user
                    if (user.getDeclinedUsers() == null) {
                        user.setDeclinedUsers(new ArrayList<String>(Arrays.asList(senderUser.getUserID())));
                    } else {
                        user.getDeclinedUsers().add(senderUser.getUserID());
                    }

                    // remove the current user from the sender user's pending sent friend requests list
                    if (senderUser.getPendingSentFriendRequests() != null) {
                        senderUser.getPendingSentFriendRequests().remove(user.getUserID());
                    }

                    // add the current user as a declined user of the sender user
                    if (senderUser.getDeclinedUsers() == null) {
                        senderUser.setDeclinedUsers(new ArrayList<String>(Arrays.asList(user.getUserID())));
                    } else {
                        senderUser.getDeclinedUsers().add(user.getUserID());
                    }

                    Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                    // update the database
                    databaseReference.child("users").child(user.getUserID()).child("pendingReceivedFriendRequests").setValue(user.getPendingReceivedFriendRequests());
                    databaseReference.child("users").child(senderUser.getUserID()).child("pendingSentFriendRequests").setValue(senderUser.getPendingSentFriendRequests());
                    databaseReference.child("users").child(user.getUserID()).child("declinedUsers").setValue(user.getDeclinedUsers());
                    databaseReference.child("users").child(senderUser.getUserID()).child("declinedUsers").setValue(senderUser.getDeclinedUsers());
                } catch (JSONException exception) {
                    Log.e("ConnectAdapter", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ConnectAdapter", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);

        // END

        notifyDataSetChanged();
    }

    public void cancelFriendRequest(FriendRequest friendRequest) {
        //delete from friend-requests
        reference.child("friend-requests").child(friendRequest.getFriendRequestID()).removeValue();;

        // delete friend request reference in user objects
        reference.child("users").child(friendRequest.getSenderUser()).child("sent-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();
        reference.child("users").child(friendRequest.getReceiverUser()).child("received-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();

        friendRequests.remove(friendRequest);

        // handle Fausto's scheme for friendship -- BEGIN
        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // extract the receiver user as a User object from the database request's response
                    JSONObject usersJSONObject = new JSONObject(response);
                    JSONObject receiverUserJSONObject = usersJSONObject.getJSONObject(friendRequest.getReceiverUser());

                    Gson gson = new Gson();
                    User receiverUser = gson.fromJson(receiverUserJSONObject.toString(), User.class);

                    if (receiverUser.getKnownLanguages() == null) {
                        receiverUser.setKnownLanguages(new ArrayList<String>());
                    }

                    if (receiverUser.getExploreLanguages() == null) {
                        receiverUser.setExploreLanguages(new ArrayList<String>());
                    }

                    if (receiverUser.getExploreCountries() == null) {
                        receiverUser.setExploreCountries(new ArrayList<String>());
                    }

                    // remove the receiver user from the current user's pending sent friend requests list
                    if (user.getPendingSentFriendRequests() != null) {
                        user.getPendingSentFriendRequests().remove(receiverUser.getUserID());
                    }

                    // remove the current user from the sender user's pending sent friend requests list
                    if (receiverUser.getPendingReceivedFriendRequests() != null) {
                        receiverUser.getPendingReceivedFriendRequests().remove(user.getUserID());
                    }

                    Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                    // update the database
                    databaseReference.child("users").child(user.getUserID()).child("pendingSentFriendRequests").setValue(user.getPendingSentFriendRequests());
                    databaseReference.child("users").child(receiverUser.getUserID()).child("pendingReceivedFriendRequests").setValue(receiverUser.getPendingReceivedFriendRequests());
                } catch (JSONException exception) {
                    Log.e("ConnectAdapter", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ConnectAdapter", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);

        // END

        notifyDataSetChanged();
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        //delete from friend-requests
        reference.child("friend-requests").child(friendRequest.getFriendRequestID()).removeValue();;

        // delete friend request reference in user objects
        reference.child("users").child(friendRequest.getSenderUser()).child("sent-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();
        reference.child("users").child(friendRequest.getReceiverUser()).child("received-friend-requests").child(friendRequest.getFriendRequestID()).removeValue();

        friendRequests.remove(friendRequest);

        notifyDataSetChanged();

        // create chat between users
        String chatId = reference.child("chats").push().getKey();

        Map<String, Object> chat = new HashMap<>();
        chat.put("lastMessage", friendRequest.getFriendRequestMessage());
        chat.put("lastMessageAt", friendRequest.getCreatedTime());
        chat.put("id", chatId);

        // handling the explore languages
        ArrayList<String> exploreLanguages = friendRequest.getExploreLanguages();
        // iterating and adding to avoid duplicates
        ArrayList<String> currentUserExploreLanguages = user.getExploreLanguages();
        for (int index = 0; index < currentUserExploreLanguages.size(); index ++) {
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

        // handle Fausto's scheme for friendship -- BEGIN
        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // extract the sender user as a User object from the database request's response
                    JSONObject usersJSONObject = new JSONObject(response);
                    JSONObject senderUserJSONObject = usersJSONObject.getJSONObject(friendRequest.getSenderUser());

                    Gson gson = new Gson();
                    User senderUser = gson.fromJson(senderUserJSONObject.toString(), User.class);

                    if (senderUser.getKnownLanguages() == null) {
                        senderUser.setKnownLanguages(new ArrayList<String>());
                    }

                    if (senderUser.getExploreLanguages() == null) {
                        senderUser.setExploreLanguages(new ArrayList<String>());
                    }

                    if (senderUser.getExploreCountries() == null) {
                        senderUser.setExploreCountries(new ArrayList<String>());
                    }

                    // remove the sender user from the current user's pending received friend requests list
                    if (user.getPendingReceivedFriendRequests() != null) {
                        user.getPendingReceivedFriendRequests().remove(senderUser.getUserID());
                    }

                    // add the sender user as a friend of the current user
                    if (user.getFriends() == null) {
                        user.setFriends(new ArrayList<String>(Arrays.asList(senderUser.getUserID())));
                    } else {
                        user.getFriends().add(senderUser.getUserID());
                    }

                    // remove the current user from the sender user's pending sent friend requests list
                    if (senderUser.getPendingSentFriendRequests() != null) {
                        senderUser.getPendingSentFriendRequests().remove(user.getUserID());
                    }

                    // add the current user as a friend of the sender user
                    if (senderUser.getFriends() == null) {
                        senderUser.setFriends(new ArrayList<String>(Arrays.asList(user.getUserID())));
                    } else {
                        senderUser.getFriends().add(user.getUserID());
                    }

                    Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                    // update the database
                    databaseReference.child("users").child(user.getUserID()).child("pendingReceivedFriendRequests").setValue(user.getPendingReceivedFriendRequests());
                    databaseReference.child("users").child(senderUser.getUserID()).child("pendingSentFriendRequests").setValue(senderUser.getPendingSentFriendRequests());
                    databaseReference.child("users").child(user.getUserID()).child("friends").setValue(user.getFriends());
                    databaseReference.child("users").child(senderUser.getUserID()).child("friends").setValue(senderUser.getFriends());
                } catch (JSONException exception) {
                    Log.e("ConnectAdapter", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ConnectAdapter", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);

        // END

        // create message in the new chat
        Map<String, String> message = new HashMap<>();
        message.put("message", friendRequest.getFriendRequestMessage());
        message.put("senderId", friendRequest.getSenderUser());
        message.put("timestamp", friendRequest.getCreatedTime());

        reference.child("messages").child(chatId).push().setValue(message);

        Toast.makeText(context, "Friend request accepted", Toast.LENGTH_LONG).show();
    }

    public void loadProfilePic(String userId) {
        String url = "https://lingua-project.firebaseio.com/users/" + userId + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                String profilePhotoURL = object.getString("userProfilePhotoURL");

                // load profile pic
                RequestOptions requestOptionsMedia = new RequestOptions();
                requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
                Glide.with(context)
                        .load(profilePhotoURL)
                        .apply(requestOptionsMedia)
                        .fallback(R.drawable.man)
                        .into(ivProfile);

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