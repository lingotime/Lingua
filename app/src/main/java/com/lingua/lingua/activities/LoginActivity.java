package com.lingua.lingua.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

/* FINALIZED, DOCUMENTED, and TESTED. LoginActivity logs in a user with Facebook OAuth. */

public class LoginActivity extends AppCompatActivity {
    CallbackManager facebookLoginManager;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private TextView applicationNameText;
    private ImageView applicationLogoImage;
    private LoginButton facebookLoginButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize context, facebook login manager, and firebase
        facebookLoginManager = CallbackManager.Factory.create();
        context = getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();

        // get signed in user, if there is one
        firebaseUser = firebaseAuth.getCurrentUser();

        // associate views with java variables
        applicationNameText = findViewById(R.id.activity_login_app_name_text);
        applicationLogoImage = findViewById(R.id.activity_login_app_logo_image);
        facebookLoginButton = findViewById(R.id.activity_login_facebook_button);

        // prepare firebase database
        Firebase.setAndroidContext(this);

        // check if already logged in
        if (firebaseUser != null) {
            // log in
            extractUserAndLoadNextStep(firebaseUser);
        } else {
            facebookLoginButton.setReadPermissions(Arrays.asList("public_profile"));

            // register a callback for logging in
            LoginManager.getInstance().registerCallback(facebookLoginManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken facebookAccessToken = loginResult.getAccessToken();

                    AuthCredential credential = FacebookAuthProvider.getCredential(facebookAccessToken.getToken());

                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser tempFirebaseUser = firebaseAuth.getCurrentUser();
                                extractUserAndLoadNextStep(tempFirebaseUser);
                            } else {
                                Log.e("LoginActivity", "signInWithCredential:failure", task.getException());
                            }
                        }
                    });
                }

                @Override
                public void onCancel() {
                    Log.d("LoginActivity", "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) { Log.e("LoginActivity", "facebook:onError", error); }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // forward activity result to facebook login manager
        facebookLoginManager.onActivityResult(requestCode, resultCode, data);
    }

    private void extractUserAndLoadNextStep(FirebaseUser tempFirebaseUser) {
        // extract data from Firebase user object
        String firebaseUserID = tempFirebaseUser.getUid();
        String firebaseUserName = tempFirebaseUser.getDisplayName();
        String firebaseUserProfilePhotoURL = "https://graph.facebook.com" + tempFirebaseUser.getPhotoUrl().getPath() + "?type=large";

        String databaseURL = "https://lingua-project.firebaseio.com/users.json";

        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("null")) {
                    // there are no users in the database, hence: new user
                    User createdUser = new User();
                    createdUser.setUserID(firebaseUserID);
                    createdUser.setUserName(firebaseUserName);
                    createdUser.setUserProfilePhotoURL(firebaseUserProfilePhotoURL);
                    createdUser.setComplete(false);

                    // save this new user in the database
                    Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
                    databaseReference.child(firebaseUserID).setValue(createdUser);

                    // proceed to set the next activity
                    loadNextStep(createdUser);
                } else {
                    try {
                        JSONObject usersJSONObject = new JSONObject(response);

                        if (usersJSONObject.has(firebaseUserID)) {
                            // user found in database
                            JSONObject userJSONObject = usersJSONObject.getJSONObject(firebaseUserID);

                            // convert JSON object to User
                            Gson gson = new Gson();
                            User generatedUser = gson.fromJson(userJSONObject.toString(), User.class);

                            // load blank values into null fields
                            if (generatedUser.getKnownLanguages() == null) {
                                generatedUser.setKnownLanguages(new ArrayList<String>());
                            }

                            if (generatedUser.getExploreLanguages() == null) {
                                generatedUser.setExploreLanguages(new ArrayList<String>());
                            }

                            if (generatedUser.getKnownCountries() == null) {
                                generatedUser.setKnownCountries(new ArrayList<String>());
                            }

                            if (generatedUser.getExploreCountries() == null) {
                                generatedUser.setExploreCountries(new ArrayList<String>());
                            }

                            // proceed to set next activity
                            loadNextStep(generatedUser);
                        } else {
                            // user not found in database, hence: new user
                            User createdUser = new User();
                            createdUser.setUserID(firebaseUserID);
                            createdUser.setUserName(firebaseUserName);
                            createdUser.setUserProfilePhotoURL(firebaseUserProfilePhotoURL);
                            createdUser.setComplete(false);

                            // save this new user in the database
                            Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
                            databaseReference.child(firebaseUserID).setValue(createdUser);

                            // proceed to set the next activity
                            loadNextStep(createdUser);
                        }
                    } catch (JSONException exception) {
                        Log.e("LoginActivity", "firebase:onException", exception);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginActivity", "firebase:onError", error);
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(LoginActivity.this);
        databaseRequestQueue.add(databaseRequest);
    }

    private void loadNextStep(User currentUser) {
        if (currentUser.isComplete()) {
            // load the main page
            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            startActivity(intent);
            finish();
        } else {
            // load the profile creation page
            final Intent intent = new Intent(LoginActivity.this, ProfileInfoSetupActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            startActivity(intent);
            finish();
        }
    }
}
