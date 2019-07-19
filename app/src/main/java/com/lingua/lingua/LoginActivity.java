package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    CallbackManager facebookLoginManager;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private LoginButton facebookLoginButton;
    private ProfilePictureView facebookProfileImage;

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
        facebookProfileImage = findViewById(R.id.activity_login_profile_image);
        facebookLoginButton = findViewById(R.id.activity_login_facebook_button);

        // check if already logged in
        if (firebaseUser != null) {
            // log in
            // previously: LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            Log.d("TRUMP", "There is already a firebase user...logging in...STEP 1");
            User currentUser = User.convertFirebaseUserToNormalUser(firebaseUser);
            Log.d("TRUMP", "There is already a firebase user...logging in...STEP 2: "+currentUser.getId());
            loadNextStep(currentUser);
        } else {
            facebookLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

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
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Log.d("TRUMP", "There is already a firebase user...logging in...STEP A");
                                User currentUser = User.convertFirebaseUserToNormalUser(user);
                                Log.d("TRUMP", "There is already a firebase user...logging in...STEP B: "+currentUser.getId());
                                loadNextStep(currentUser);
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
                public void onError(FacebookException error) {
                    Log.e("LoginActivity", "facebook:onError", error);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // forward activity result to facebook login manager
        facebookLoginManager.onActivityResult(requestCode, resultCode, data);
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
            final Intent intent = new Intent(LoginActivity.this, ProfileCreationActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            startActivity(intent);
            finish();
        }
    }
}
