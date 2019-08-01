package com.lingua.lingua.notifyAPI;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lingua.lingua.notifyAPI.BindingSharedPreferences.ADDRESS;
import static com.lingua.lingua.notifyAPI.BindingSharedPreferences.ENDPOINT;
import static com.lingua.lingua.notifyAPI.BindingSharedPreferences.IDENTITY;

public class BindingIntentService extends IntentService {

    private static final String TAG = "BindingIntentService";
    private static final String FCM_BINDING_TYPE = "fcm";

    private SharedPreferences sharedPreferences;
    private Intent bindingResultIntent;

    public BindingIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        bindingResultIntent = new Intent(MainActivity.BINDING_REGISTRATION);

        // Set the new binding identity
        String newIdentity = intent.getStringExtra(IDENTITY);

        // Load the old binding values from shared preferences if they exist
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String identity = sharedPreferences.getString(IDENTITY, null);
        String address = sharedPreferences.getString(ADDRESS, null);

        if (newIdentity == null) {
            // If no identity was provided to us then we use the identity stored in shared preferences.
            if (identity != null) {
                newIdentity = identity;
            } else {
                /*
                 * When the application is first installed onTokenRefresh() may be called without the
                 * user providing an identity. In this case we ignore the request to bind.
                 */
                Log.w(TAG, "No identity was provided.");
                return;
            }
        }

        String endpoint = sharedPreferences.getString(ENDPOINT + newIdentity, null);

        /*
         * Obtain the new address based off the Firebase instance token
         */
        String newAddress = FirebaseInstanceId.getInstance().getToken();

        /*
         * Check whether a new binding registration is required by comparing the prior values that
         * were stored in shared preferences after the last successful binding registration.
         */
        boolean sameBinding =
                newIdentity.equals(identity) && newAddress.equals(address);

        if (newIdentity == null) {
            Log.i(TAG, "A new binding registration was not performed because" +
                    " the identity cannot be null.");
            bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, false);
            bindingResultIntent.putExtra(MainActivity.BINDING_RESPONSE, "Binding identity was null");
            // Notify the MainActivity that the registration ended
            LocalBroadcastManager.getInstance(BindingIntentService.this)
                    .sendBroadcast(bindingResultIntent);
        } else if (sameBinding) {
            Log.i(TAG, "A new binding registration was not performed because" +
                    "the binding values are the same as the last registered binding.");
            bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, true);
            bindingResultIntent.putExtra(MainActivity.BINDING_RESPONSE, "Binding already registered");
            // Notify the MainActivity that the registration ended
            LocalBroadcastManager.getInstance(BindingIntentService.this)
                    .sendBroadcast(bindingResultIntent);
        } else {
            /*
             * Clear all the existing bindings from SharedPreferences and attempt to register
             * the new binding values.
             */
            sharedPreferences.edit().remove(IDENTITY).commit();
            sharedPreferences.edit().remove(ENDPOINT + newIdentity).commit();
            sharedPreferences.edit().remove(ADDRESS).commit();
            final Binding binding = new Binding(newIdentity, endpoint, newAddress, FCM_BINDING_TYPE);
            registerBinding(binding);
        }
    }

    /**
     * Register the binding with Twilio Notify via your application server.
     */
    private void registerBinding(final Binding binding) {
        /*
         * Issue the request to your application server and wait for the result in the callback
         */
        Log.i(TAG, "Binding with" +
                " identity: " + binding.identity +
                " endpoint: " + binding.endpoint +
                " address: " + binding.Address);

        /*
         * Make sure the Twilio SDK Starter URL has been updated
         */
        if (TwilioFunctionsAPI.BASE_SERVER_URL.equals(getString(R.string.base_url_string))) {
            String message = "Set the BASE_SERVER_URL in TwilioFunctionsAPI.java";
            Log.e(TAG, message);
            bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, false);
            bindingResultIntent.putExtra(MainActivity.BINDING_RESPONSE, message);
            // Notify the MainActivity that the registration ended
            LocalBroadcastManager.getInstance(BindingIntentService.this)
                    .sendBroadcast(bindingResultIntent);
            return;
        }

        Call<CreateBindingResponse> call = TwilioFunctionsAPI.registerBinding(binding);
        call.enqueue(new Callback<CreateBindingResponse>() {
            @Override
            public void onResponse(Call<CreateBindingResponse> call, Response<CreateBindingResponse> response) {
                if(response.isSuccess()) {
                    // Store binding in SharedPreferences upon success
                    sharedPreferences.edit().putString(IDENTITY, binding.identity).commit();
                    sharedPreferences.edit().putString(ENDPOINT + binding.identity, response.body().endpoint).commit();
                    sharedPreferences.edit().putString(ADDRESS, binding.Address).commit();

                    bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, true);
                } else {
                    String message = "Binding failed " + response.code() + " " + response.message();
                    Log.e(TAG, message);
                    bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, false);
                    bindingResultIntent.putExtra(MainActivity.BINDING_RESPONSE, message);
                }

                // Notify the MainActivity that the registration ended
                LocalBroadcastManager.getInstance(BindingIntentService.this)
                        .sendBroadcast(bindingResultIntent);
            }

            @Override
            public void onFailure(Call<CreateBindingResponse> call, Throwable t) {
                String message = "Binding failed " + t.getMessage();
                Log.e(TAG, message);
                bindingResultIntent.putExtra(MainActivity.BINDING_SUCCEEDED, false);
                bindingResultIntent.putExtra(MainActivity.BINDING_RESPONSE, message);
                // Notify the MainActivity that the registration ended
                LocalBroadcastManager.getInstance(BindingIntentService.this)
                        .sendBroadcast(bindingResultIntent);
            }
        });
    }

}
