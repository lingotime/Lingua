package com.lingua.lingua;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Class to add configurations to the FirebaseMessagingService - needed for the push notifications functionality
 */
public class FCMConfig extends FirebaseMessagingService {
    private final static String TAG = "FCMConfig";

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);
        // TODO: push the new token for the current user to the database

        super.onNewToken(s);
    }
}
