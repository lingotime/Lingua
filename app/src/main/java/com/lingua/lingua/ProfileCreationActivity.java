package com.lingua.lingua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.lingua.lingua.models.LocalUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProfileCreationActivity extends AppCompatActivity {


    private ImageView profilePicture;
    private TextView fullName;
    private TextView username;
    private EditText originCountry;
    private MultiAutoCompleteTextView currentLanguages;
    private MultiAutoCompleteTextView targetLanguages;
    private MultiAutoCompleteTextView targetCountries;
    private EditText bio;
    private LocalUser user;
    private ArrayList<String> languages;
    private ArrayList<String> targetL;
    private ArrayList<String> targetC;
    // target languages will be an EditText and then separate them to form an ArrayList
    // their bio
    // target countries will be the same as target languages


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        user = new LocalUser();
        //TODO: add a button to allow for the change in the profile image
        profilePicture = (ImageView) findViewById(R.id.activity_profile_creation_profileImage);
        // TODO: get the full name, username, and maybe profile picture from the sign up or login through Google or Facebook
        fullName = (TextView) findViewById(R.id.activity_profile_creation_fullName);
        username = (TextView) findViewById(R.id.activity_profile_creation_username);
        originCountry = (EditText) findViewById(R.id.activity_profile_creation_originCountry);
        // TODO: split the strings by commas and place them into an array list of the same fields in the user
        currentLanguages = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_currentLanguages);
        targetLanguages = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_targetLanguages);
        targetCountries = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_targetCountries);
        bio = (EditText) findViewById(R.id.activity_profile_creation_bio);

        user.setOriginCountry(originCountry.getText().toString());
        String[] current = currentLanguages.getText().toString().split(",");

        // creating the array list of primary languages
        for (int index=0; index < current.length; index++) {
            languages.add(current[index].trim());
        }

        user.setCurrentLanguages(languages);

        // creating the array list of target languages
        String[] targetl = targetLanguages.getText().toString().split(",");
        for (int index=0; index < targetl.length; index++) {
            targetL.add(targetl[index].trim());
        }

        user.setTargetLanguages(targetL);

        // creating the array list of target countries
        String[] targetc = targetCountries.getText().toString().split(",");
        for (int index=0; index < targetc.length; index++) {
            targetC.add(targetl[index].trim());
        }

        user.setTargetCountries(targetC);

        user.setBio(bio.getText().toString());


    }

}
