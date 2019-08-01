package com.lingua.lingua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.google.gson.Gson;
import com.lingua.lingua.EndlessRecyclerViewScrollListener;
import com.lingua.lingua.R;
import com.lingua.lingua.SearchAdapter;
import com.lingua.lingua.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.Iterator;

/* FINALIZED, DOCUMENTED, and TESTED SearchFragment allows users to search for other users via their names. */

public class SearchFragment extends Fragment {
    private User currentUser;

    Context context;
    ArrayList<User> usersList;
    ArrayList<User> hiddenUsersList;
    SearchAdapter usersAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private SearchView searchBar;
    private RecyclerView resultsTimeline;

    private static final int NUMBER_OF_USERS_PER_LOAD = 6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // associate views with java variables
        searchBar = view.findViewById(R.id.fragment_search_search_bar);
        resultsTimeline = view.findViewById(R.id.fragment_search_results_timeline);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // set the context
        context = getContext();

        // initialize the list of users
        usersList = new ArrayList<User>();
        hiddenUsersList = new ArrayList<User>();

        // set a text change listener for the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queriedName) {
                // change the focus
                searchBar.clearFocus();

                // return success status
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queriedName) {
                // clear the user lists
                usersList.clear();
                hiddenUsersList.clear();
                usersAdapter.notifyDataSetChanged();

                // reset the scroll listener
                scrollListener.resetState();

                // fetch compatible users who match criteria and load them into timeline
                fetchCompatibleUsersAndLoad(currentUser, queriedName);

                // return success status
                return true;
            }
        });

        // set the adapter
        usersAdapter = new SearchAdapter(context, usersList, hiddenUsersList, currentUser);
        resultsTimeline.setAdapter(usersAdapter);

        // set the layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        // prepare the endless scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!hiddenUsersList.isEmpty()) {
                    if (hiddenUsersList.size() > NUMBER_OF_USERS_PER_LOAD) {
                        usersList.addAll(hiddenUsersList.subList(0, NUMBER_OF_USERS_PER_LOAD));
                        hiddenUsersList.removeAll(hiddenUsersList.subList(0, NUMBER_OF_USERS_PER_LOAD));
                    } else {
                        usersList.addAll(hiddenUsersList);
                        hiddenUsersList.clear();
                    }

                    usersAdapter.notifyDataSetChanged();
                }
            }
        };
        resultsTimeline.addOnScrollListener(scrollListener);

        // display timeline
        resultsTimeline.setLayoutManager(layoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();

        // clear the search bar and change the focus
        searchBar.setQuery("", true);
        searchBar.clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();

        // clear the search bar and change the focus
        searchBar.setQuery("", true);
        searchBar.clearFocus();
    }

    private void fetchCompatibleUsersAndLoad(User currentUser, String queriedName) {
        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        // fetch users from database
        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject usersJSONObject = new JSONObject(response);
                    Iterator<String> usersJSONObjectKeys = usersJSONObject.keys();
                    int usersJSONObjectCounter = 0;

                    // iterate through users in the database
                    while (usersJSONObjectKeys.hasNext()) {
                        String userID = usersJSONObjectKeys.next();
                        JSONObject userJSONObject = usersJSONObject.getJSONObject(userID);

                        // convert JSONObject information to user
                        Gson gson = new Gson();
                        User generatedUser = gson.fromJson(userJSONObject.toString(), User.class);

                        // load blank values into null fields
                        if (generatedUser.getKnownLanguages() == null) {
                            generatedUser.setKnownLanguages(new ArrayList<String>());
                        }

                        if (generatedUser.getExploreLanguages() == null) {
                            generatedUser.setExploreLanguages(new ArrayList<String>());
                        }

                        if (generatedUser.getExploreCountries() == null) {
                            generatedUser.setExploreCountries(new ArrayList<String>());
                        }

                        // get relevant information from user for matching
                        String nameOfGeneratedUser = generatedUser.getUserName();

                        // filter user depending on criteria
                        if (generatedUser.isComplete() && !(generatedUser.getUserID().equals(currentUser.getUserID())) && nameOfGeneratedUser.toLowerCase().contains(queriedName.toLowerCase()) && actionNotTaken(generatedUser.getUserID())) {
                            if (usersJSONObjectCounter < NUMBER_OF_USERS_PER_LOAD) {
                                // add to the list of users
                                usersList.add(generatedUser);

                                // increment the user limiter
                                usersJSONObjectCounter++;
                            } else {
                                // add to the hidden list of users
                                hiddenUsersList.add(generatedUser);
                            }
                        }
                    }

                    usersAdapter.notifyDataSetChanged();
                } catch (JSONException exception) {
                    Log.e("SearchFragment", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SearchFragment", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(context);
        databaseRequestQueue.add(databaseRequest);
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

        if (currentUser.getDeclinedUsers() != null) {
            for (String declinedUserID : currentUser.getDeclinedUsers()) {
                if (declinedUserID.equals(generatedUserID)) {
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