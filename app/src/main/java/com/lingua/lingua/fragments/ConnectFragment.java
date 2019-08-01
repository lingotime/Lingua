package com.lingua.lingua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.ConnectAdapter;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;
import com.lingua.lingua.supports.EndlessRecyclerViewScrollListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* FINALIZED, DOCUMENTED, and TESTED ConnectFragment displays pending sent and received friend request notifications. */

public class ConnectFragment extends Fragment {
    private User currentUser;

    Context context;
    ArrayList<FriendRequest> friendRequestsList;
    ArrayList<FriendRequest> hiddenFriendRequestsList;
    ConnectAdapter friendRequestsAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private TextView descriptionText;
    private RecyclerView historyTimeline;

    private static final int NUMBER_OF_FRIEND_REQUESTS_PER_LOAD = 6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // associate views with java variables
        descriptionText = view.findViewById(R.id.fragment_connect_description_text);
        historyTimeline = view.findViewById(R.id.fragment_connect_history_timeline);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // set the context
        context = getContext();

        // initialize the list of friend requests
        friendRequestsList = new ArrayList<FriendRequest>();
        hiddenFriendRequestsList = new ArrayList<FriendRequest>();

        // set the adapter
        friendRequestsAdapter = new ConnectAdapter(context, friendRequestsList, hiddenFriendRequestsList, currentUser);
        historyTimeline.setAdapter(friendRequestsAdapter);

        // set the layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        // prepare the endless scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!hiddenFriendRequestsList.isEmpty()) {
                    if (hiddenFriendRequestsList.size() > NUMBER_OF_FRIEND_REQUESTS_PER_LOAD) {
                        friendRequestsList.addAll(hiddenFriendRequestsList.subList(0, NUMBER_OF_FRIEND_REQUESTS_PER_LOAD));
                        hiddenFriendRequestsList.removeAll(hiddenFriendRequestsList.subList(0, NUMBER_OF_FRIEND_REQUESTS_PER_LOAD));
                    } else {
                        friendRequestsList.addAll(hiddenFriendRequestsList);
                        hiddenFriendRequestsList.clear();
                    }

                    friendRequestsAdapter.notifyDataSetChanged();
                }
            }
        };
        historyTimeline.addOnScrollListener(scrollListener);

        // display timeline
        historyTimeline.setLayoutManager(layoutManager);

        // create a database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUserID());

        // prepare the database listener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get the updated user
                User updatedUser = dataSnapshot.getValue(User.class);

                // load blank values into null fields
                if (updatedUser.getKnownLanguages() == null) {
                    updatedUser.setKnownLanguages(new ArrayList<String>());
                }

                if (updatedUser.getExploreLanguages() == null) {
                    updatedUser.setExploreLanguages(new ArrayList<String>());
                }

                if (updatedUser.getKnownCountries() == null) {
                    updatedUser.setKnownCountries(new ArrayList<String>());
                }

                if (updatedUser.getExploreCountries() == null) {
                    updatedUser.setExploreCountries(new ArrayList<String>());
                }

                // update the current user
                currentUser = updatedUser;

                // clear the friend request lists
                friendRequestsList.clear();
                hiddenFriendRequestsList.clear();
                friendRequestsAdapter.notifyDataSetChanged();

                // reset the scroll listener
                scrollListener.resetState();

                // fetch friend requests and load them into timeline
                fetchCompatibleFriendRequestsAndLoad(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConnectFragment", "firebase:onError");
            }
        });
    }

    private void fetchCompatibleFriendRequestsAndLoad(User currentUser) {
        // get criteria for friend requests to be loaded into timeline
        String currentUserID = currentUser.getUserID();

        String databaseURL = "https://lingua-project.firebaseio.com/friend-requests.json";

        // fetch friend requests from database
        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject friendRequestsJSONObject = new JSONObject(response);
                    Iterator<String> friendRequestsJSONObjectKeys = friendRequestsJSONObject.keys();
                    int friendRequestsJSONObjectCounter = 0;

                    // iterate through friend requests in the database
                    while (friendRequestsJSONObjectKeys.hasNext()) {
                        String friendRequestID = friendRequestsJSONObjectKeys.next();
                        JSONObject friendRequestJSONObject = friendRequestsJSONObject.getJSONObject(friendRequestID);

                        // convert JSONObject information to friend request
                        Gson gson = new Gson();
                        FriendRequest generatedFriendRequest = gson.fromJson(friendRequestJSONObject.toString(), FriendRequest.class);

                        // get relevant information from friend request for matching
                        String status = generatedFriendRequest.getFriendRequestStatus();
                        String senderUserID = generatedFriendRequest.getSenderUser();
                        String receiverUserID = generatedFriendRequest.getReceiverUser();

                        // filter friend request depending on criteria
                        if ((senderUserID.equals(currentUserID) || receiverUserID.equals(currentUserID)) && (status.equals("Pending") || status.equals("Accepted"))) {
                            if (friendRequestsJSONObjectCounter < NUMBER_OF_FRIEND_REQUESTS_PER_LOAD) {
                                // check to ensure friend request is not loaded in the list of friend requests
                                boolean isLoaded = false;

                                for (FriendRequest loadedFriendRequest : friendRequestsList) {
                                    if (loadedFriendRequest.getFriendRequestID().equals(generatedFriendRequest.getFriendRequestID())) {
                                        isLoaded = true;
                                    }
                                }

                                for (FriendRequest loadedFriendRequest : hiddenFriendRequestsList) {
                                    if (loadedFriendRequest.getFriendRequestID().equals(generatedFriendRequest.getFriendRequestID())) {
                                        isLoaded = true;
                                    }
                                }

                                // if friend request is not loaded, add to the list of friend requests
                                if (!isLoaded) {
                                    friendRequestsList.add(generatedFriendRequest);
                                }

                                // increment the friend request limiter
                                friendRequestsJSONObjectCounter++;
                            } else {
                                // check to ensure friend request is not loaded in the list of friend requests
                                boolean isLoaded = false;

                                for (FriendRequest loadedFriendRequest : friendRequestsList) {
                                    if (loadedFriendRequest.getFriendRequestID().equals(generatedFriendRequest.getFriendRequestID())) {
                                        isLoaded = true;
                                    }
                                }

                                for (FriendRequest loadedFriendRequest : hiddenFriendRequestsList) {
                                    if (loadedFriendRequest.getFriendRequestID().equals(generatedFriendRequest.getFriendRequestID())) {
                                        isLoaded = true;
                                    }
                                }

                                // if friend request is not loaded, add to the hidden list of friend requests
                                if (!isLoaded) {
                                    hiddenFriendRequestsList.add(generatedFriendRequest);
                                }
                            }
                        }
                    }

                    friendRequestsAdapter.notifyDataSetChanged();
                } catch (JSONException exception) {
                    Log.e("ConnectFragment", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ConnectFragment", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);
    }
}