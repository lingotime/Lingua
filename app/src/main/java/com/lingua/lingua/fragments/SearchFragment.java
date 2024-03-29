package com.lingua.lingua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.adapters.SearchAdapter;
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
    TextView noUsersTv;

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
        noUsersTv = view.findViewById(R.id.fragment_search_no_users_tv);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // set the toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_search_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search");

        // set the context
        context = getContext();

        // initialize the list of users
        usersList = new ArrayList<>();
        hiddenUsersList = new ArrayList<>();

        // set a text change listener for the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queriedName) {
                // clear the user lists
                usersList.clear();
                hiddenUsersList.clear();
                usersAdapter.notifyDataSetChanged();

                // reset the scroll listener
                scrollListener.resetState();

                // change the focus
                searchBar.clearFocus();

                // fetch compatible users who match criteria and load them into timeline
                queryInfoAndLoadUsers(queriedName);

                // return success status
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queriedName) {
                // clear the user lists
                if (queriedName.equals("")) {
                    usersList.clear();
                    hiddenUsersList.clear();
                    usersAdapter.notifyDataSetChanged();

                    // reset the scroll listener
                    scrollListener.resetState();

                    // fetch compatible users who match criteria and load them into timeline
                    queryInfoAndLoadUsers(queriedName);
                }

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

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        resultsTimeline.addItemDecoration(itemDecoration);

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

    private void queryInfoAndLoadUsers(String queriedText) {
        String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + ".json";

        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject userObject = new JSONObject(s);
                noUsersTv.setVisibility(View.GONE);
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
                if (userObject.has("friendIDs")) {
                    JSONObject object = userObject.getJSONObject("friendIDs");
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
                fetchCompatibleUsersAndLoad(currentUser, queriedText);

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

    private void fetchCompatibleUsersAndLoad(User currentUser, String queriedText) {
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
                        String userOriginCountry = generatedUser.getUserOriginCountry();
                        ArrayList<String> userKnownLanguages = generatedUser.getKnownLanguages();

                        // filter user depending on criteria
                        if (generatedUser.isComplete() && !(generatedUser.getUserID().equals(currentUser.getUserID())) && matchExists(userOriginCountry, userKnownLanguages, queriedText) && actionNotTaken(generatedUser.getUserID())) {
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

                    if (usersList.isEmpty()) {
                        noUsersTv.setVisibility(View.VISIBLE);
                    }

                    usersAdapter.notifyDataSetChanged();
                } catch (JSONException exception) {
                    noUsersTv.setVisibility(View.VISIBLE);
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

    // ensure there is a language or country match between generated user info and queried text
    private boolean matchExists(String originCountry, ArrayList<String> knownLanguages, String queriedText) {
        if (originCountry.toLowerCase().startsWith(queriedText.toLowerCase())) {
            return true;
        }

        for (String knownLanguage : knownLanguages) {
            if (knownLanguage.toLowerCase().startsWith(queriedText.toLowerCase())) {
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