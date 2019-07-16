package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton fbLoginButton;
    private SignInButton googleSigninButton;
    ProfilePictureView imageView;
    CallbackManager callbackManager;
    TextView info;
    Context context;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        info = findViewById(R.id.info);
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

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                                try {
                                    Log.i(TAG, "email: " + jsonObject.getString("email"));
                                    Log.i(TAG, "name: " + jsonObject.getString("name"));
                                    if (jsonObject.getString("id") != null) {
                                        Log.i(TAG, "id: " + jsonObject.getString("id"));
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

                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            }

            @Override
            public void onCancel() {
                // App code
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                info.setText("Login attempt failed.");
            }
        });

        // Google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this); // check if null

        googleSigninButton = findViewById(R.id.googleSigninButton);
        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(TAG, "email: " + account.getEmail());
                Log.i(TAG, "name: " + account.getDisplayName());

            } catch (ApiException e) {
                e.printStackTrace();
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
