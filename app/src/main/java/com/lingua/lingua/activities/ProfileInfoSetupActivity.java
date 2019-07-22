package com.lingua.lingua.activities;

import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hootsuite.nachos.NachoTextView;
import com.lingua.lingua.R;
import com.lingua.lingua.models.Country;
import com.lingua.lingua.models.Language;

import java.io.File;

/**
 * Activity that allows the user to input their information relevant to the main functions of the app after getting past Auth
 */

public class ProfileInfoSetupActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_profile_info_setup);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );

        //TODO: add a button to allow for the change in the profile image
        profilePicture = (ImageView) findViewById(R.id.activity_profile_info_setup_profile_image);
        // TODO: get the full name, username, and maybe profile picture from the sign up or login through Google or Facebook
//        fullName = (TextView) findViewById(R.id.activity_profile_creation_fullName);
//        username = (TextView) findViewById(R.id.activity_profile_creation_username);
        originCountry = (NachoTextView) findViewById(R.id.activity_profile_info_setup_origin_country_field);
        // TODO: split the strings by commas and place them into an array list of the same fields in the user
        currentLanguages = (NachoTextView) findViewById(R.id.activity_profile_info_setup_known_languages_field);
        targetLanguages = (NachoTextView) findViewById(R.id.activity_profile_info_setup_explore_languages_field);
        targetCountries = (NachoTextView) findViewById(R.id.activity_profile_info_setup_explore_countries_field);
        bio = (EditText) findViewById(R.id.activity_profile_info_setup_biography_field);
        btnSubmit = (Button) findViewById(R.id.activity_profile_info_setup_continue_button);

        // TODO: create adapters with a list of the possibilities for autocompletion and set it upon creation

        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Country.COUNTRIES);

        ArrayAdapter<String> adapterLanguages = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Language.LANGUAGES);


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
                Intent intent = new Intent(ProfileInfoSetupActivity.this, ProfilePhotoSetupActivity.class);
                startActivityForResult(intent, CAMERA_ACTIVITY);
            }
        });



        // TODO: Set the onlick listener for the Submit button and place the info into the User class connected to the database
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