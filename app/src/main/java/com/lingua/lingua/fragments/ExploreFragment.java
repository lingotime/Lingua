package com.lingua.lingua.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

/* FINALIZED, DOCUMENTED, and TESTED ExploreFragment displays timeline of users matching stated profile criteria. */

public class ExploreFragment extends Fragment {
    private User currentUser;

    ArrayList<User> usersList;
    ExploreAdapter usersAdapter;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView historyTimeline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // associate views with java variables
        swipeContainer = view.findViewById(R.id.fragment_explore_swipe_container);
        historyTimeline = view.findViewById(R.id.fragment_explore_history_timeline);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // initialize the list of users
        usersList = new ArrayList<User>();

        // fetch compatible users who match criteria and load them into timeline
        fetchCompatibleUsersAndLoad(currentUser);
    }

    private void fetchCompatibleUsersAndLoad(User currentUser) {
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

                        // get relevant information from user for matching
                        ArrayList<String> languagesSelectedByGeneratedUser = generatedUser.getKnownLanguages();
                        ArrayList<String> countriesSelectedByGeneratedUser = generatedUser.getKnownCountries();

                        // filter user depending on criteria
                        if (generatedUser.getUserID() != currentUser.getUserID() && matchExists(languagesSelectedByUser, countriesSelectedByUser, languagesSelectedByGeneratedUser, countriesSelectedByGeneratedUser)) {
                            // add to the list of users
                            usersList.add(generatedUser);
                        }
                    }

                    // load matched users into timeline
                    usersAdapter = new ExploreAdapter(getContext(), usersList);
                    historyTimeline.setAdapter(usersAdapter);

                    // display timeline
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    historyTimeline.setLayoutManager(layoutManager);
                } catch (JSONException exception) {
                    Log.e("ExploreFragment", "firebase:onException", exception);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ExploreFragment", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(getContext());
        databaseRequestQueue.add(databaseRequest);
    }

    private boolean matchExists(ArrayList<String> exploreLanguages, ArrayList<String> exploreCountries, ArrayList<String> knownLanguages, ArrayList<String> knownCountries) {
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
            for (String knownCountry : knownCountries) {
                if (exploreCountry.equals(knownCountry)) {
                    return true;
                }
            }
        }

        return false;
    }
}