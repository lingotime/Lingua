package com.lingua.lingua.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.lingua.lingua.R;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/* TODO: Order friend requests chronologically, comment code. */

public class ConnectAdapter extends RecyclerView.Adapter<ConnectAdapter.ViewHolder> {
    private User currentUser;

    Context context;
    List<FriendRequest> friendRequestsList;
    List<FriendRequest> hiddenFriendRequestsList;

    private static final int FRIEND_REQUEST_SENT = 0;
    private static final int FRIEND_REQUEST_RECEIVED = 1;
    private static final int FRIEND_REQUEST_ACCEPTED = 2;

    public ConnectAdapter(Context context, List<FriendRequest> friendRequestsList, List<FriendRequest> hiddenFriendRequestsList, User currentUser) {
        this.context = context;
        this.friendRequestsList = friendRequestsList;
        this.hiddenFriendRequestsList = hiddenFriendRequestsList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ConnectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FRIEND_REQUEST_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request_sent, parent, false);
            return new ConnectAdapter.ViewHolder(view, viewType);
        } else if (viewType == FRIEND_REQUEST_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request_received, parent, false);
            return new ConnectAdapter.ViewHolder(view, viewType);
        } else if (viewType == FRIEND_REQUEST_ACCEPTED) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request_accepted, parent, false);
            return new ConnectAdapter.ViewHolder(view, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectAdapter.ViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequestsList.get(position);
        holder.bind(friendRequest);
    }

    @Override
    public int getItemCount() {
        return friendRequestsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FriendRequest friendRequest = friendRequestsList.get(position);

        if (friendRequest.getSenderUser().equals(currentUser.getUserID()) && friendRequest.getFriendRequestStatus().equals("Pending")) {
            return FRIEND_REQUEST_SENT;
        } else if (friendRequest.getReceiverUser().equals(currentUser.getUserID()) && friendRequest.getFriendRequestStatus().equals("Pending")) {
            return FRIEND_REQUEST_RECEIVED;
        } else if (friendRequest.getFriendRequestStatus().equals("Accepted")) {
            return FRIEND_REQUEST_ACCEPTED;
        }

        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private ImageView profilePhotoImage;
        private TextView nameText;
        private TextView timestampText;
        private TextView typeText;
        private TextView messageText;
        private Button acceptButton;
        private Button rejectButton;
        private Button cancelButton;
        private Button startConversationButton;

        public ViewHolder(View friendRequestItemView, int friendRequestItemViewType) {
            super(friendRequestItemView);

            if (friendRequestItemViewType == FRIEND_REQUEST_SENT) {
                card = friendRequestItemView.findViewById(R.id.item_friend_request_sent_card);
                profilePhotoImage = friendRequestItemView.findViewById(R.id.item_friend_request_sent_profile_image);
                nameText = friendRequestItemView.findViewById(R.id.item_friend_request_sent_name_text);
                timestampText = friendRequestItemView.findViewById(R.id.item_friend_request_sent_timestamp_text);
                typeText = friendRequestItemView.findViewById(R.id.item_friend_request_sent_type_text);
                messageText = friendRequestItemView.findViewById(R.id.item_friend_request_sent_message_text);
                cancelButton = friendRequestItemView.findViewById(R.id.item_friend_request_sent_cancel_button);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            String databaseURL = "https://lingua-project.firebaseio.com/users.json";

                            StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        FriendRequest clickedFriendRequest = friendRequestsList.get(position);
                                        clickedFriendRequest.setFriendRequestStatus("Canceled");
                                        clickedFriendRequest.setRespondedTime((new Date()).toString());

                                        JSONObject usersJSONObject = new JSONObject(response);
                                        JSONObject receiverUserJSONObject = usersJSONObject.getJSONObject(clickedFriendRequest.getReceiverUser());

                                        Gson gson = new Gson();
                                        User receiverUser = gson.fromJson(receiverUserJSONObject.toString(), User.class);

                                        if (receiverUser.getKnownLanguages() == null) {
                                            receiverUser.setKnownLanguages(new ArrayList<String>());
                                        }

                                        if (receiverUser.getExploreLanguages() == null) {
                                            receiverUser.setExploreLanguages(new ArrayList<String>());
                                        }

                                        if (receiverUser.getKnownCountries() == null) {
                                            receiverUser.setKnownCountries(new ArrayList<String>());
                                        }

                                        if (receiverUser.getExploreCountries() == null) {
                                            receiverUser.setExploreCountries(new ArrayList<String>());
                                        }

                                        currentUser.getPendingSentFriendRequests().remove(receiverUser.getUserID());
                                        receiverUser.getPendingReceivedFriendRequests().remove(currentUser.getUserID());

                                        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                                        databaseReference.child("users").child(currentUser.getUserID()).setValue(currentUser);
                                        databaseReference.child("users").child(receiverUser.getUserID()).setValue(receiverUser);
                                        databaseReference.child("friend-requests").child(clickedFriendRequest.getFriendRequestID()).setValue(clickedFriendRequest);
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
                        }
                    }
                });
            } else if (friendRequestItemViewType == FRIEND_REQUEST_RECEIVED) {
                card = friendRequestItemView.findViewById(R.id.item_friend_request_received_card);
                profilePhotoImage = friendRequestItemView.findViewById(R.id.item_friend_request_received_profile_image);
                nameText = friendRequestItemView.findViewById(R.id.item_friend_request_received_name_text);
                timestampText = friendRequestItemView.findViewById(R.id.item_friend_request_received_timestamp_text);
                typeText = friendRequestItemView.findViewById(R.id.item_friend_request_received_type_text);
                messageText = friendRequestItemView.findViewById(R.id.item_friend_request_received_message_text);
                acceptButton = friendRequestItemView.findViewById(R.id.item_friend_request_received_accept_button);
                rejectButton = friendRequestItemView.findViewById(R.id.item_friend_request_received_reject_button);

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            String databaseURL = "https://lingua-project.firebaseio.com/users.json";

                            StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        FriendRequest clickedFriendRequest = friendRequestsList.get(position);
                                        clickedFriendRequest.setFriendRequestStatus("Accepted");
                                        clickedFriendRequest.setRespondedTime((new Date()).toString());

                                        JSONObject usersJSONObject = new JSONObject(response);
                                        JSONObject senderUserJSONObject = usersJSONObject.getJSONObject(clickedFriendRequest.getSenderUser());

                                        Gson gson = new Gson();
                                        User senderUser = gson.fromJson(senderUserJSONObject.toString(), User.class);

                                        if (senderUser.getKnownLanguages() == null) {
                                            senderUser.setKnownLanguages(new ArrayList<String>());
                                        }

                                        if (senderUser.getExploreLanguages() == null) {
                                            senderUser.setExploreLanguages(new ArrayList<String>());
                                        }

                                        if (senderUser.getKnownCountries() == null) {
                                            senderUser.setKnownCountries(new ArrayList<String>());
                                        }

                                        if (senderUser.getExploreCountries() == null) {
                                            senderUser.setExploreCountries(new ArrayList<String>());
                                        }

                                        currentUser.getPendingReceivedFriendRequests().remove(senderUser.getUserID());

                                        if (currentUser.getFriends() == null) {
                                            currentUser.setFriends(new ArrayList<String>(Arrays.asList(senderUser.getUserID())));
                                        } else {
                                            currentUser.getFriends().add(senderUser.getUserID());
                                        }

                                        senderUser.getPendingSentFriendRequests().remove(currentUser.getUserID());

                                        if (senderUser.getFriends() == null) {
                                            senderUser.setFriends(new ArrayList<String>(Arrays.asList(currentUser.getUserID())));
                                        } else {
                                            senderUser.getFriends().add(currentUser.getUserID());
                                        }

                                        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                                        databaseReference.child("users").child(currentUser.getUserID()).setValue(currentUser);
                                        databaseReference.child("users").child(senderUser.getUserID()).setValue(senderUser);
                                        databaseReference.child("friend-requests").child(clickedFriendRequest.getFriendRequestID()).setValue(clickedFriendRequest);
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
                        }
                    }
                });

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            String databaseURL = "https://lingua-project.firebaseio.com/users.json";

                            StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        FriendRequest clickedFriendRequest = friendRequestsList.get(position);
                                        clickedFriendRequest.setFriendRequestStatus("Rejected");
                                        clickedFriendRequest.setRespondedTime((new Date()).toString());

                                        JSONObject usersJSONObject = new JSONObject(response);
                                        JSONObject senderUserJSONObject = usersJSONObject.getJSONObject(clickedFriendRequest.getSenderUser());

                                        Gson gson = new Gson();
                                        User senderUser = gson.fromJson(senderUserJSONObject.toString(), User.class);

                                        if (senderUser.getKnownLanguages() == null) {
                                            senderUser.setKnownLanguages(new ArrayList<String>());
                                        }

                                        if (senderUser.getExploreLanguages() == null) {
                                            senderUser.setExploreLanguages(new ArrayList<String>());
                                        }

                                        if (senderUser.getKnownCountries() == null) {
                                            senderUser.setKnownCountries(new ArrayList<String>());
                                        }

                                        if (senderUser.getExploreCountries() == null) {
                                            senderUser.setExploreCountries(new ArrayList<String>());
                                        }

                                        currentUser.getPendingReceivedFriendRequests().remove(senderUser.getUserID());

                                        if (currentUser.getDeclinedUsers() == null) {
                                            currentUser.setDeclinedUsers(new ArrayList<String>(Arrays.asList(senderUser.getUserID())));
                                        } else if (!currentUser.getDeclinedUsers().contains(senderUser.getUserID())) {
                                            currentUser.getDeclinedUsers().add(senderUser.getUserID());
                                        }

                                        senderUser.getPendingSentFriendRequests().remove(currentUser.getUserID());

                                        if (senderUser.getDeclinedUsers() == null) {
                                            senderUser.setDeclinedUsers(new ArrayList<String>(Arrays.asList(currentUser.getUserID())));
                                        } else if (!senderUser.getDeclinedUsers().contains(currentUser.getUserID())) {
                                            senderUser.getDeclinedUsers().add(currentUser.getUserID());
                                        }

                                        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

                                        databaseReference.child("users").child(currentUser.getUserID()).setValue(currentUser);
                                        databaseReference.child("users").child(senderUser.getUserID()).setValue(senderUser);
                                        databaseReference.child("friend-requests").child(clickedFriendRequest.getFriendRequestID()).setValue(clickedFriendRequest);
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
                        }
                    }
                });
            } else if (friendRequestItemViewType == FRIEND_REQUEST_ACCEPTED) {
                card = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_card);
                profilePhotoImage = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_profile_image);
                nameText = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_name_text);
                timestampText = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_timestamp_text);
                typeText = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_type_text);
                startConversationButton = friendRequestItemView.findViewById(R.id.item_friend_request_accepted_start_button);

                startConversationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: Create a conversation and open it
                    }
                });
            }
        }

        public void bind(FriendRequest friendRequest) {
            String databaseURL = "https://lingua-project.firebaseio.com/users.json";

            StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject usersJSONObject = new JSONObject(response);
                        JSONObject displayUserJSONObject = null;

                        if (friendRequest.getSenderUser().equals(currentUser.getUserID())) {
                            displayUserJSONObject = usersJSONObject.getJSONObject(friendRequest.getReceiverUser());
                        } else {
                            displayUserJSONObject = usersJSONObject.getJSONObject(friendRequest.getSenderUser());
                        }

                        Gson gson = new Gson();
                        User displayUser = gson.fromJson(displayUserJSONObject.toString(), User.class);

                        if (displayUser.getKnownLanguages() == null) {
                            displayUser.setKnownLanguages(new ArrayList<String>());
                        }

                        if (displayUser.getExploreLanguages() == null) {
                            displayUser.setExploreLanguages(new ArrayList<String>());
                        }

                        if (displayUser.getKnownCountries() == null) {
                            displayUser.setKnownCountries(new ArrayList<String>());
                        }

                        if (displayUser.getExploreCountries() == null) {
                            displayUser.setExploreCountries(new ArrayList<String>());
                        }

                        if (friendRequest.getSenderUser().equals(currentUser.getUserID()) && friendRequest.getFriendRequestStatus().equals("Pending")) {
                            // sent
                            Glide.with(context).load(displayUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

                            nameText.setText(displayUser.getUserName());
                            timestampText.setText(getRelativeTime(friendRequest.getCreatedTime()));
                            typeText.setText("received your friend request.");
                            messageText.setText(friendRequest.getFriendRequestMessage());
                        } else if (friendRequest.getReceiverUser().equals(currentUser.getUserID()) && friendRequest.getFriendRequestStatus().equals("Pending")) {
                            // received
                            Glide.with(context).load(displayUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

                            nameText.setText(displayUser.getUserName());
                            timestampText.setText(getRelativeTime(friendRequest.getCreatedTime()));
                            typeText.setText("sent a friend request.");
                            messageText.setText(friendRequest.getFriendRequestMessage());
                        } else if (friendRequest.getFriendRequestStatus().equals("Accepted")) {
                            // accepted
                            Glide.with(context).load(displayUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

                            nameText.setText(displayUser.getUserName());
                            timestampText.setText(getRelativeTime(friendRequest.getRespondedTime()));
                            typeText.setText("is now your friend.");
                        }
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
        }

        private String getRelativeTime(String time) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            dateFormat.setLenient(true);

            String relativeDate = "";

            try {
                long timeInMilliseconds = dateFormat.parse(time).getTime();

                relativeDate = DateUtils.getRelativeTimeSpanString(timeInMilliseconds, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }
    }
}