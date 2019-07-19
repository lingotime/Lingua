package com.lingua.lingua;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hootsuite.nachos.NachoTextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Activity that allows the user to input their information relevant to the main functions of the app after getting past Auth
 */

public class ProfileCreationActivity extends AppCompatActivity {


    private ImageView profilePicture; // will be taken from OAuth
    private TextView fullName;
    private TextView username;
    private NachoTextView originCountry;
    private NachoTextView currentLanguages;
    private NachoTextView targetLanguages;
    private NachoTextView targetCountries;
    private Button btnSubmit;
    private EditText bio;
    public static final int CAMERA_ACTIVITY = 700; // return code for the profile picture activity



    // target languages will be an EditText and then separate them to form an ArrayList
    // their bio
    // target countries will be the same as target languages


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        Toolbar toolbar = findViewById(R.id.activity_profile_creation_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );

        //TODO: add a button to allow for the change in the profile image
        profilePicture = (ImageView) findViewById(R.id.activity_profile_creation_profileImage);
        // TODO: get the full name, username, and maybe profile picture from the sign up or login through Google or Facebook
//        fullName = (TextView) findViewById(R.id.activity_profile_creation_fullName);
//        username = (TextView) findViewById(R.id.activity_profile_creation_username);
        originCountry = (NachoTextView) findViewById(R.id.activity_profile_creation_originCountry);
        // TODO: split the strings by commas and place them into an array list of the same fields in the user
        currentLanguages = (NachoTextView) findViewById(R.id.activity_profile_creation_currentLanguages);
        targetLanguages = (NachoTextView) findViewById(R.id.activity_profile_creation_targetLanguages);
        targetCountries = (NachoTextView) findViewById(R.id.activity_profile_creation_targetCountries);
        bio = (EditText) findViewById(R.id.activity_profile_creation_bio);
        btnSubmit = (Button) findViewById(R.id.activity_profile_creation_submit);

        // TODO: create adapters with a list of the possibilities for autocompletion and set it upon creation

        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CountryInformation.COUNTRIES);

        ArrayAdapter<String> adapterLanguages = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CountryInformation.LANGUAGES);


        targetCountries.setAdapter(adapterCountries);
//        targetCountries.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        currentLanguages.setAdapter(adapterLanguages);
//        currentLanguages.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        targetLanguages.setAdapter(adapterLanguages);
//        targetLanguages.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        originCountry.setAdapter(adapterCountries);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Launch the activity that will allow the user to take a picture
                Intent intent = new Intent(ProfileCreationActivity.this, ProfilePicture.class);
                startActivityForResult(intent, CAMERA_ACTIVITY);
            }
        });



        // TODO: Set the onlick listener for the Submit button and place the info into the User class connected to the database

        // set the User fields upon submission
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                user.setOriginCountry(originCountry.getChipValues().get(0));
//                user.setCurrentLanguages(currentLanguages.getChipValues());
//                user.setTargetLanguages(targetLanguages.getChipValues());
//                user.setTargetCountries(targetCountries.getChipValues());
//                user.setBio(bio.getText().toString());
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_ACTIVITY) {
            // extract the URI string, parse it into a URI object and set bitmap to the profile image view
            String imagePath = data.getStringExtra("profilePicture");
            Uri imageUri = Uri.parse(imagePath);
            // load image into view
            // TODO: fix the method to load in the image - probably from the stored URI
            Glide.with(this)
                    .load(new File(imageUri.getPath()))
                    .into(profilePicture);
        }
    }
}
