package com.lingua.lingua;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.hootsuite.nachos.NachoTextView;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import com.google.android.material.textfield.TextInputEditText;
import com.hootsuite.nachos.ChipConfiguration;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.tokenizer.ChipTokenizer;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity that allows the user to input their information relevant to the main functions of the app after getting past Auth
 */

public class ProfileCreationActivity extends AppCompatActivity {


    private ImageView profilePicture; // will be taken from OAuth
    private TextView fullName;
    private TextView username;
    private TextInputEditText dob;
    private NachoTextView originCountry;
    private NachoTextView currentLanguages;
    private NachoTextView targetLanguages;
    private NachoTextView targetCountries;
    private Button btnSubmit;
    private EditText bio;
    public static final int CAMERA_ACTIVITY = 700; // return code for the profile picture activity

    private User currentUser;

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
        dob = findViewById(R.id.activity_profile_creation_dob);
        originCountry = (NachoTextView) findViewById(R.id.activity_profile_creation_originCountry);
        // TODO: split the strings by commas and place them into an array list of the same fields in the user
        currentLanguages = (NachoTextView) findViewById(R.id.activity_profile_creation_currentLanguages);
        targetLanguages = (NachoTextView) findViewById(R.id.activity_profile_creation_targetLanguages);
        targetCountries = (NachoTextView) findViewById(R.id.activity_profile_creation_targetCountries);
        bio = (EditText) findViewById(R.id.activity_profile_creation_bio);
        btnSubmit = (Button) findViewById(R.id.activity_profile_creation_submit);

        // added by fausto - prepopulate data
        // prepopulate data from the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        if (currentUser.getBiographyText() != null) {
            bio.setText(currentUser.getBiographyText());
        }

        if (currentUser.getOriginCountry() != null) {
            originCountry.setText(currentUser.getOriginCountry());
        }

        if (currentUser.getKnownLanguages() != null) {
            currentLanguages.setText(currentUser.getKnownLanguages());
        }

        if (currentUser.getExploreLanguages() != null) {
            targetLanguages.setText(currentUser.getExploreLanguages());
        }

        if (currentUser.getExploreCountries() != null) {
            targetCountries.setText(currentUser.getExploreCountries());
        }

        // TODO: create adapters with a list of the possibilities for autocompletion and set it upon creation

        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CountryInformation.COUNTRIES);

        ArrayAdapter<String> adapterLanguages = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CountryInformation.LANGUAGES);


        targetCountries.setAdapter(adapterCountries);
        targetCountries.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                String fileName = CountryInformation.COUNTRY_CODES.get(text); // gets the lowercase country code, and therefore, the filename
                int id = getResources().getIdentifier(fileName, "drawable", getPackageName()); // gets the id for the image in order to find and return the drawable
                Drawable drawable = getResources().getDrawable(id);
                return new ChipSpan(context, text, drawable, data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                // alter the appearance of the chips and the chip icon backgrounds
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(true);
                chip.setIconBackgroundColor(R.color.veryLightGrey);
            }
        }, ChipSpan.class));


        currentLanguages.setAdapter(adapterLanguages);


        targetLanguages.setAdapter(adapterLanguages);


        originCountry.setAdapter(adapterCountries);
        // overrides the creation of the ChipSpan from the library used so that the chip has the icon of the countries' flags
        originCountry.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                String fileName = CountryInformation.COUNTRY_CODES.get(text); // gets the lowercase country code, and therefore, the filename
                int id = getResources().getIdentifier(fileName, "drawable", getPackageName()); // gets the id for the image in order to find and return the drawable
                Drawable drawable = getResources().getDrawable(id);
                return new ChipSpan(context, text, drawable, data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(true);
                chip.setIconBackgroundColor(R.color.veryLightGrey);
            }
        }, ChipSpan.class));


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Launch the activity that will allow the user to take a picture
                Intent intent = new Intent(ProfileCreationActivity.this, ProfilePicture.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                startActivityForResult(intent, CAMERA_ACTIVITY);
            }
        });

        // added by fausto
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                final Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                startActivity(intent);
            }
        });

        //TODO: Check if a user is already logged in and in this case, they've come from the profile fragment with a desire to edit their page. Prepopulate the edittexts before they enter anything
    }

    private void saveData() {
        currentUser.setBiographyText(bio.getText().toString());
        currentUser.setOriginCountry(originCountry.getChipValues().get(0));
        currentUser.setKnownLanguages((ArrayList) currentLanguages.getChipValues());
        currentUser.setExploreLanguages((ArrayList) targetLanguages.getChipValues());
        currentUser.setExploreCountries((ArrayList) targetCountries.getChipValues());
        // save
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getId()).setValue(currentUser);
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

    protected void loadInfo() {
        // loads the user info from the current logged in user
        Date userDob = null;
        List<String> userOriginCountry = new ArrayList<String>();
        List<String> userPrimaryLanguages = new ArrayList<String>();
        List<String> userTargetLanguages = new ArrayList<String>();
        List<String> userTargetCountries = new ArrayList<String>();
        String userBio = null;

        //dob.setText(userDob.toString());
        // the data fields with the chips must be entered as a list of Strings
        originCountry.setText(userOriginCountry);
        currentLanguages.setText(userPrimaryLanguages);
        targetCountries.setText(userTargetCountries);
        targetLanguages.setText(userTargetLanguages);
        bio.setText(userBio);
    }
}
