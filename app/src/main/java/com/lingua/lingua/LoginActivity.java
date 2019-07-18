package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/*
Login activity that handles Facebook OAuth
*/

public class LoginActivity extends AppCompatActivity {

    private LoginButton fbLoginButton;
    ProfilePictureView imageView;
    CallbackManager callbackManager;
    Context context;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        imageView = findViewById(R.id.imageView);
        fbLoginButton = findViewById(R.id.fbLoginButton);
        fbLoginButton.setPermissions(Arrays.asList("public_profile, email, user_birthday"));

        // Facebook login
        FacebookSdk.sdkInitialize(context);
        //AppEventsLogger.activateApp(context);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if (isLoggedIn) {
                    handleFacebookAccessToken(accessToken);
                }

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                                try {
                                    Log.i(TAG, "email: " + jsonObject.getString("email"));
                                    Log.i(TAG, "name: " + jsonObject.getString("name"));
                                    if (jsonObject.getString("id") != null) {
                                        Log.i(TAG, "id: " + jsonObject.getString("id")); // This is the unique id that we can use to reference the facebook user
                                        imageView.setPresetSize(ProfilePictureView.LARGE);
                                        imageView.setProfileId(jsonObject.getString("id"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Log.d(TAG, "name: " + user.getDisplayName());
//                            Log.d(TAG, "photoUrl: " + user.getPhotoUrl());
//
//                            //updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                        }
//                    }
//                });
    }

}
