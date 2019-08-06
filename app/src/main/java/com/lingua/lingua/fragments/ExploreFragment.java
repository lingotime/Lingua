package com.lingua.lingua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.ExploreAdapter;
import com.lingua.lingua.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Fragment that displays other people's profiles that match the user's target language or country for
 * the user to browse through and send friend requests
 */

public class ExploreFragment extends Fragment {
    private User currentUser;

    Context context;
    ArrayList<User> usersList;
    ExploreAdapter usersAdapter;

    private RecyclerView historyTimeline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // associate view with java variable
        historyTimeline = view.findViewById(R.id.fragment_explore_history_timeline);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // set the context
        context = getContext();

        // initialize the list of users
        usersList = new ArrayList<>();

        // set the adapter
        usersAdapter = new ExploreAdapter(context, usersList, currentUser);
        historyTimeline.setAdapter(usersAdapter);

        // set the layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        // enable the snap helper for card snapping
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(historyTimeline);

        // display timeline
        historyTimeline.setLayoutManager(layoutManager);

        queryInfoAndLoadUsers();
    }

    private void queryInfoAndLoadUsers() {
        String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + ".json";

        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject userObject = new JSONObject(s);

                // get received friend requests
                if (userObject.has("receivedFriendRequests")) {
                    JSONObject receivedFriendRequests = userObject.getJSONObject("receivedFriendRequests");
                    Iterator receivedFriendRequestKeys = receivedFriendRequests.keys();
                    ArrayList<String> receivedFriendRequestUserIDs = new ArrayList<>();
                    while (receivedFriendRequestKeys.hasNext()) {
                        String key = receivedFriendRequestKeys.next().toString();
                        String userID = key.split("@")[0];
                        receivedFriendRequestUserIDs.add(userID);
                    }
                    currentUser.setPendingReceivedFriendRequests(receivedFriendRequestUserIDs);
                }

                // get sent friend requests
                if (userObject.has("sentFriendRequests")) {
                    JSONObject object = userObject.getJSONObject("sentFriendRequests");
                    Iterator sentFriendRequestKeys = object.keys();
                    ArrayList<String> sentFriendRequestUserIDs = new ArrayList<>();
                    while (sentFriendRequestKeys.hasNext()) {
                        String key = sentFriendRequestKeys.next().toString();
                        String userID = key.split("@")[1];
                        sentFriendRequestUserIDs.add(userID);
                    }
                    currentUser.setPendingSentFriendRequests(sentFriendRequestUserIDs);
                }

                // get friends
                if (userObject.has("friends")) {
                    JSONObject object = userObject.getJSONObject("friends");
                    Iterator keys = object.keys();
                    ArrayList<String> friends = new ArrayList<>();
                    while (keys.hasNext()) {
                        String key = keys.next().toString();
                        friends.add(key);
                    }
                    currentUser.setFriends(friends);
                }

                usersList.clear();
                usersAdapter.notifyDataSetChanged();
                fetchCompatibleUsers(currentUser);

            } catch (JSONException e) {
                Log.e("ExploreFragment", e.toString());
            }
        }, volleyError -> {
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
            Log.e("ExploreFragment", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    private void fetchCompatibleUsers(User currentUser) {
        // get criteria for users to be loaded into timeline
        ArrayList<String> languagesSelectedByUser = currentUser.getExploreLanguages();
        ArrayList<String> countriesSelectedByUser = currentUser.getExploreCountries();

        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        // fetch users from database
        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject usersJSONObject = new JSONObject(response);
                    Iterator<String> usersJSONObjectKeys = usersJSONObject.keys();

                    // iterate through users in the database
                    while (usersJSONObjectKeys.hasNext()) {
                        String userID = usersJSONObjectKeys.next();
                        JSONObject userJSONObject = usersJSONObject.getJSONObject(userID);

                        // convert JSONObject information to user
                        Gson gson = new Gson();
                        User generatedUser = gson.fromJson(userJSONObject.toString(), User.class);

                        // load blank values into null fields
                        if (generatedUser.getKnownLanguages() == null) {
                            generatedUser.setKnownLanguages(new ArrayList<>());
                        }

                        if (generatedUser.getExploreLanguages() == null) {
                            generatedUser.setExploreLanguages(new ArrayList<>());
                        }

                        if (generatedUser.getExploreCountries() == null) {
                            generatedUser.setExploreCountries(new ArrayList<>());
                        }

                        // get relevant information from user for matching
                        ArrayList<String> languagesSelectedByGeneratedUser = generatedUser.getKnownLanguages();
                        String originCountrySelectedByGeneratedUser = generatedUser.getUserOriginCountry();

                        // filter user depending on criteria
                        if (generatedUser.isComplete() && !(generatedUser.getUserID().equals(currentUser.getUserID())) && matchExists(languagesSelectedByUser, countriesSelectedByUser, languagesSelectedByGeneratedUser, originCountrySelectedByGeneratedUser) && actionNotTaken(generatedUser.getUserID())) {
                            // add to the list of users
                            usersList.add(generatedUser);
                        }
                    }

                    if (usersList.isEmpty()) {
                        Toast.makeText(context, "No users to display", Toast.LENGTH_SHORT).show();
                    }

                    usersAdapter.notifyDataSetChanged();
                } catch (JSONException exception) {
                    Toast.makeText(context, "No users to display", Toast.LENGTH_SHORT).show();
                    Log.e("ExploreFragment", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ExploreFragment", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);
    }

    // ensure there is a language or country match between user and displayed user
    private boolean matchExists(ArrayList<String> exploreLanguages, ArrayList<String> exploreCountries, ArrayList<String> knownLanguages, String originCountry) {
        if (exploreLanguages.size() == 0 && exploreCountries.size() == 0) {
            return true;
        }

        for (String exploreLanguage : exploreLanguages) {
            for (String knownLanguage : knownLanguages) {
                if (exploreLanguage.equals(knownLanguage)) {
                    return true;
                }
            }
        }

        for (String exploreCountry : exploreCountries) {
            if (exploreCountry.equals(originCountry)) {
                return true;
            }
        }

        return false;
    }

    // ensure there is no previous relationship between user and displayed user
    private boolean actionNotTaken(String generatedUserID) {
        if (currentUser.getFriends() != null) {
            for (String friendUserID : currentUser.getFriends()) {
                if (friendUserID.equals(generatedUserID)) {
                    return false;
                }
            }
        }

        if (currentUser.getPendingSentFriendRequests() != null) {
            for (String pendingUserID : currentUser.getPendingSentFriendRequests()) {
                if (pendingUserID.equals(generatedUserID)) {
                    return false;
                }
            }
        }

        if (currentUser.getPendingReceivedFriendRequests() != null) {
            for (String pendingUserID : currentUser.getPendingReceivedFriendRequests()) {
                if (pendingUserID.equals(generatedUserID)) {
                    return false;
                }
            }
        }

        return true;
    }
}