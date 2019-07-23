package com.lingua.lingua.activities;

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
import android.widget.Switch;
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
import com.lingua.lingua.R;
import com.lingua.lingua.models.Country;
import com.lingua.lingua.models.Language;
import com.lingua.lingua.models.User;
import org.parceler.Parcels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/* FINALIZED, DOCUMENTED, and TESTED ProfileInfoSetupActivity allows a user to setup information relevant to their account. */
/* TODO: make chip group for origin country field, fix Date serialization issues. */

public class ProfileInfoSetupActivity extends AppCompatActivity {
    private User currentUser;

    private TextView descriptionText;
    private ImageView profileImage;
    private EditText nameField;
    private EditText birthdateField;
    private TextView boyText;
    private Switch genderSwitch;
    private TextView girlText;
    private TextInputEditText biographyField;
    private TextInputEditText originCountryField;
    private NachoTextView knownLanguagesField;
    private NachoTextView exploreLanguagesField;
    private NachoTextView knownCountriesField;
    private NachoTextView exploreCountriesField;
    private Button continueButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info_setup);

        // associate views with java variables
        descriptionText = findViewById(R.id.activity_profile_info_setup_description_text);
        profileImage = findViewById(R.id.activity_profile_info_setup_profile_image);
        nameField = findViewById(R.id.activity_profile_info_setup_name_field);
        birthdateField = findViewById(R.id.activity_profile_info_setup_birthdate_field);
        boyText = findViewById(R.id.activity_profile_info_setup_boy_text);
        genderSwitch = findViewById(R.id.activity_profile_info_setup_gender_switch);
        girlText = findViewById(R.id.activity_profile_info_setup_girl_text);
        biographyField = findViewById(R.id.activity_profile_info_setup_biography_field);
        originCountryField = findViewById(R.id.activity_profile_info_setup_origin_country_field);
        knownLanguagesField = findViewById(R.id.activity_profile_info_setup_known_languages_field);
        exploreLanguagesField = findViewById(R.id.activity_profile_info_setup_explore_languages_field);
        knownCountriesField = findViewById(R.id.activity_profile_info_setup_known_countries_field);
        exploreCountriesField = findViewById(R.id.activity_profile_info_setup_explore_countries_field);
        continueButton = findViewById(R.id.activity_profile_info_setup_continue_button);

        // unwrap the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // prepopulate data from the current user
        if (currentUser.getUserName() != null) {
            nameField.setText(currentUser.getUserName());
        }

        if (currentUser.getUserBirthDate() != null) {
            birthdateField.setText(currentUser.getUserBirthDate().getMonth() + "/" + currentUser.getUserBirthDate().getDay() + "/" + currentUser.getUserBirthDate().getYear());
        }

        if (currentUser.getUserGender() != null) {
            if (currentUser.getUserGender().equals("Girl")) {
                genderSwitch.setChecked(true);
            } else {
                genderSwitch.setChecked(false);
            }
        }

        if (currentUser.getUserBiographyText() != null) {
            biographyField.setText(currentUser.getUserBiographyText());
        }

        if (currentUser.getUserOriginCountry() != null) {
            originCountryField.setText(currentUser.getUserOriginCountry());
        }

        Glide.with(this).load(currentUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profileImage);

        if (currentUser.getKnownLanguages() != null) {
            knownLanguagesField.setText(currentUser.getKnownLanguages());
        }

        if (currentUser.getExploreLanguages() != null) {
            exploreLanguagesField.setText(currentUser.getExploreLanguages());
        }

        if (currentUser.getKnownCountries() != null) {
            knownCountriesField.setText(currentUser.getKnownCountries());
        }

        if (currentUser.getExploreCountries() != null) {
            exploreCountriesField.setText(currentUser.getExploreCountries());
        }

        // enable the profile image to be clickable
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the updated data
                saveData();

                // proceed to photo setup activity
                final Intent intent = new Intent(ProfileInfoSetupActivity.this, ProfilePhotoSetupActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                startActivity(intent);
            }
        });

        // set adapters for each field with chip functionality
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Country.COUNTRIES);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Language.LANGUAGES);
        knownLanguagesField.setAdapter(languagesAdapter);
        exploreLanguagesField.setAdapter(languagesAdapter);
        knownCountriesField.setAdapter(countriesAdapter);
        exploreCountriesField.setAdapter(countriesAdapter);

        // set a flag icon with each country chip entered
        knownCountriesField.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                Drawable flagDrawable;
                try {
                    // attempt to load correct flag icon
                    String flagPhotoFileName = Country.COUNTRY_CODES.get(text.toString()) + "_round";
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
                    String flagPhotoFileName = Country.COUNTRY_CODES.get(text.toString()) + "_round";
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
                    final Intent intent = new Intent(ProfileInfoSetupActivity.this, MainActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentUser));
                    startActivity(intent);
                }
            }
        });
    }

    private void saveData() {
        boolean isCompleteCheck = true;

        // deal with userName
        String userNameInput = nameField.getText().toString();

        if (userNameInput.length() >= 5 && userNameInput.contains(" ")) {
            currentUser.setUserName(userNameInput);
        } else {
            isCompleteCheck = false;
            Toast.makeText(ProfileInfoSetupActivity.this, "Please enter a valid full name.", Toast.LENGTH_LONG).show();
        }

        // deal with userBirthDate
        String userBirthDateInput = birthdateField.getText().toString();

        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date userBirthDateInputAsDate = inputDateFormat.parse(userBirthDateInput);
            currentUser.setUserBirthDate(userBirthDateInputAsDate);
        } catch (ParseException exception) {
            isCompleteCheck = false;
            Toast.makeText(ProfileInfoSetupActivity.this, "Please enter a valid birth date. Format: mm/dd/yyyy", Toast.LENGTH_LONG).show();
        }

        // deal with userGender
        boolean userGenderInput = genderSwitch.isChecked();

        if (userGenderInput) {
            currentUser.setUserGender("Girl");
        } else {
            currentUser.setUserGender("Boy");
        }

        // deal with userBiographyText
        String userBiographyTextInput = biographyField.getText().toString();

        if (userBiographyTextInput.length() >= 4) {
            currentUser.setUserBiographyText(userBiographyTextInput);
        } else {
            isCompleteCheck = false;
            Toast.makeText(ProfileInfoSetupActivity.this, "Please enter a biography that is at least four characters.", Toast.LENGTH_LONG).show();
        }

        // deal with userOriginCountry
        String userOriginCountryInput = originCountryField.getText().toString();

        if (userOriginCountryInput.length() >= 4) {
            currentUser.setUserOriginCountry(userOriginCountryInput);
        } else {
            isCompleteCheck = false;
            Toast.makeText(ProfileInfoSetupActivity.this, "Please enter a valid country name.", Toast.LENGTH_LONG).show();
        }

        // deal with knownLanguages
        ArrayList<String> knownLanguagesInput = (ArrayList) knownLanguagesField.getChipValues();
        currentUser.setKnownLanguages(knownLanguagesInput);

        // deal with exploreLanguages
        ArrayList<String> exploreLanguagesInput = (ArrayList) exploreLanguagesField.getChipValues();
        currentUser.setExploreLanguages(exploreLanguagesInput);

        // deal with knownCountries
        ArrayList<String> knownCountriesInput = (ArrayList) knownCountriesField.getChipValues();
        currentUser.setKnownCountries(knownCountriesInput);

        // deal with exploreCountries
        ArrayList<String> exploreCountriesInput = (ArrayList) exploreCountriesField.getChipValues();
        currentUser.setExploreCountries(exploreCountriesInput);

        // deal with isComplete
        currentUser.setComplete(isCompleteCheck);

        // save
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }
}