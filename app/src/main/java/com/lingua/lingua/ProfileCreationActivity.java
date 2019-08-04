package com.lingua.lingua;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputEditText;
import com.hootsuite.nachos.ChipConfiguration;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;
import com.lingua.lingua.models.User;
import org.parceler.Parcels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* FINALIZED, DOCUMENTED, and TESTED ProfileInfoSetupActivity allows a user to setup information relevant to their account. */

public class ProfileCreationActivity extends AppCompatActivity {
    private User currentUser;

    private TextView descriptionText;
    private ImageView profileImage;
    private EditText nameField;
    private EditText birthdateField;
    private TextInputEditText biographyField;
    private NachoTextView originCountryField;
    private NachoTextView knownLanguagesField;
    private NachoTextView exploreLanguagesField;
    private NachoTextView exploreCountriesField;
    private Button continueButton;

    private String nextFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        // associate views with java variables
        descriptionText = findViewById(R.id.activity_profile_info_setup_description_text);
        profileImage = findViewById(R.id.activity_profile_info_setup_profile_image);
        nameField = findViewById(R.id.activity_profile_info_setup_name_field);
        birthdateField = findViewById(R.id.activity_profile_info_setup_birthdate_field);
        biographyField = findViewById(R.id.activity_profile_info_setup_biography_field);
        originCountryField = findViewById(R.id.activity_profile_info_setup_origin_country_field);
        knownLanguagesField = findViewById(R.id.activity_profile_info_setup_known_languages_field);
        exploreLanguagesField = findViewById(R.id.activity_profile_info_setup_explore_languages_field);
        exploreCountriesField = findViewById(R.id.activity_profile_info_setup_explore_countries_field);
        continueButton = findViewById(R.id.activity_profile_info_setup_continue_button);

        // unwrap the current user and next fragment
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        nextFragment = getIntent().getStringExtra("fragment");

        // enable the profile image to be clickable
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the updated data
                saveData();

                // proceed to photo setup activity
                final Intent intent = new Intent(ProfileCreationActivity.this, ProfilePicture.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("fragment", nextFragment);
                startActivity(intent);
            }
        });

        // set adapters for each field with chip functionality
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CountryInformation.COUNTRIES);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CountryInformation.LANGUAGES);
        originCountryField.setAdapter(countriesAdapter);
        knownLanguagesField.setAdapter(languagesAdapter);
        exploreLanguagesField.setAdapter(languagesAdapter);
        exploreCountriesField.setAdapter(countriesAdapter);

        // set a flag icon with each country chip entered
        originCountryField.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                Drawable flagDrawable;
                try {
                    // attempt to load correct flag icon
                    String flagPhotoFileName = CountryInformation.COUNTRY_CODES.get(text.toString()) + "_round";
                    flagDrawable = getResources().getDrawable(getResources().getIdentifier(flagPhotoFileName, "drawable", getPackageName()));
                } catch (Exception exception) {
                    // load United Nations flag icon if error occurs
                    String flagPhotoFileName = "un_round";
                    flagDrawable = getResources().getDrawable(getResources().getIdentifier(flagPhotoFileName, "drawable", getPackageName()));
                }
                return new ChipSpan(context, text, flagDrawable, data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(true);
            }
        }, ChipSpan.class));

        // set a flag icon with each country chip entered
        exploreCountriesField.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                Drawable flagDrawable;
                try {
                    // attempt to load correct flag icon
                    String flagPhotoFileName = CountryInformation.COUNTRY_CODES.get(text.toString()) + "_round";
                    flagDrawable = getResources().getDrawable(getResources().getIdentifier(flagPhotoFileName, "drawable", getPackageName()));
                } catch (Exception exception) {
                    // load United Nations flag icon if error occurs
                    String flagPhotoFileName = "un_round";
                    flagDrawable = getResources().getDrawable(getResources().getIdentifier(flagPhotoFileName, "drawable", getPackageName()));
                }
                return new ChipSpan(context, text, flagDrawable, data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(true);
            }
        }, ChipSpan.class));

        // set the continue button to be clickable
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the updated data
                saveData();

                // proceed to main activity, if data was successfully saved to an acceptable level
                if (currentUser.isComplete()) {
                    final Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentUser));
                    intent.putExtra("fragment", nextFragment);
                    startActivity(intent);
                }
            }
        });

        // refactored - now should load user data with the flags associated with the chips
        loadInfo();
    }

    private void saveData() {
        boolean isCompleteCheck = true;

        // deal with userName
        String userNameInput = nameField.getText().toString();

        if (userNameInput.length() >= 5 && userNameInput.contains(" ")) {
            currentUser.setUserName(userNameInput);
        } else {
            isCompleteCheck = false;
            Toast.makeText(ProfileCreationActivity.this, "Please enter a valid full name.", Toast.LENGTH_LONG).show();
        }

        // deal with userBirthDate
        String userBirthDateInput = birthdateField.getText().toString();
        String userBirthDate = null;
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date userBirthDateInputAsDate = inputDateFormat.parse(userBirthDateInput);
            userBirthDate = userBirthDateInputAsDate.toString();
            currentUser.setUserBirthDate(userBirthDate);
        } catch (ParseException exception) {
            isCompleteCheck = false;
            Toast.makeText(this, "Please enter a valid birth date. Format: mm/dd/yyyy", Toast.LENGTH_LONG).show();
        }

        // deal with userBiographyText
        String userBiographyTextInput = biographyField.getText().toString();

        if (userBiographyTextInput.length() >= 4) {
            currentUser.setUserBiographyText(userBiographyTextInput);
        } else {
            isCompleteCheck = false;
            Toast.makeText(this, "Please enter a biography that is at least four characters.", Toast.LENGTH_LONG).show();
        }

        // deal with userOriginCountry
        ArrayList<String> userOriginCountryInput = (ArrayList) originCountryField.getChipValues();

        if (userOriginCountryInput.size() == 1) {
            currentUser.setUserOriginCountry(userOriginCountryInput.get(0));
        } else {
            isCompleteCheck = false;
            Toast.makeText(this, "Please enter an origin country. Only one origin country is allowed.", Toast.LENGTH_LONG).show();
        }

        // deal with knownLanguages
        ArrayList<String> knownLanguagesInput = (ArrayList) knownLanguagesField.getChipValues();
        currentUser.setKnownLanguages(knownLanguagesInput);

        // deal with exploreLanguages
        ArrayList<String> exploreLanguagesInput = (ArrayList) exploreLanguagesField.getChipValues();
        currentUser.setExploreLanguages(exploreLanguagesInput);

        // deal with exploreCountries
        ArrayList<String> exploreCountriesInput = (ArrayList) exploreCountriesField.getChipValues();
        currentUser.setExploreCountries(exploreCountriesInput);

        // deal with isComplete
        currentUser.setComplete(isCompleteCheck);

        // save to database
        if (currentUser.isComplete()) {
            Firebase.setAndroidContext(this);
            Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());
            databaseReference.child("userName").setValue(currentUser.getUserName());
            databaseReference.child("userBirthDate").setValue(currentUser.getUserBirthDate());
            databaseReference.child("userBiographyText").setValue(currentUser.getUserBiographyText());
            databaseReference.child("userOriginCountry").setValue(currentUser.getUserOriginCountry());
            databaseReference.child("knownLanguages").setValue(currentUser.getKnownLanguages());
            databaseReference.child("exploreLanguages").setValue(currentUser.getExploreLanguages());
            databaseReference.child("exploreCountries").setValue(currentUser.getExploreCountries());
            databaseReference.child("complete").setValue(currentUser.isComplete());
        }
    }

    protected void loadInfo() {
        // loads the user info from the current logged in user
        // prepopulate data from the current user
        if (currentUser.getUserName() != null) {
            nameField.setText(currentUser.getUserName());
        }

        if (currentUser.getUserBirthDate() != null) {
            try {
                SimpleDateFormat storedDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                Date userBirthDateAsDate = storedDateFormat.parse(currentUser.getUserBirthDate());
                SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                birthdateField.setText(displayDateFormat.format(userBirthDateAsDate));
            } catch (ParseException exception) {
                Log.e("ProfileSetupActivity", "There was an issue parsing the user's registered birth date.");
            }
        }

        if (currentUser.getUserBiographyText() != null) {
            biographyField.setText(currentUser.getUserBiographyText());
        }

        Glide.with(this).load(currentUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profileImage);

        if (currentUser.getUserOriginCountry() != null) {
            List<String> userOriginCountry = new ArrayList<>();
            userOriginCountry.add(currentUser.getUserOriginCountry());
            originCountryField.setText(userOriginCountry);
        }

        if (currentUser.getKnownLanguages() != null) {
            List<String> userPrimaryLanguages = currentUser.getKnownLanguages();
            knownLanguagesField.setText(userPrimaryLanguages);
        }

        if (currentUser.getExploreLanguages() != null) {
            List<String> userTargetLanguages = currentUser.getExploreLanguages();
            exploreLanguagesField.setText(userTargetLanguages);
        }

        if (currentUser.getExploreCountries() != null) {
            List<String> userTargetCountries = currentUser.getExploreCountries();
            exploreCountriesField.setText(userTargetCountries);
        }
    }
}
