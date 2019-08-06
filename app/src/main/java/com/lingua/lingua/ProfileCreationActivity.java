package com.lingua.lingua;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/* FINALIZED, DOCUMENTED, and TESTED ProfileInfoSetupActivity allows a user to setup information relevant to their account. */

public class ProfileCreationActivity extends AppCompatActivity {
    private User currentUser;

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

    final Calendar calendar = Calendar.getInstance();

    SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        // associate views with java variables
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
        Log.d("ProfileCreationActivity", nextFragment);

        // setting up the birthdate field for to accept the date from a calendar popup
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        birthdateField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileCreationActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // set up toolbar
        Toolbar toolbar = findViewById(R.id.activity_profile_creation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit your profile");

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
                chip.setIconBackgroundColor(Color.WHITE);
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
                chip.setIconBackgroundColor(Color.WHITE);
            }
        }, ChipSpan.class));

        // set the continue button to be clickable
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // disable the button and change its text
                continueButton.setText("Saving");
                continueButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                continueButton.setEnabled(false);

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

    @Override
    protected void onResume() {
        super.onResume();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as live
        currentUser.setOnline(true);
        reference.child("online").setValue(currentUser.isOnline());
    }

    @Override
    protected void onStop() {
        super.onStop();

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());

        // mark user as dead
        currentUser.setOnline(false);
        reference.child("online").setValue(currentUser.isOnline());
    }

    private void saveData() {
        boolean isCompleteCheck = true;

        // deal with userName
        String userNameInput = nameField.getText().toString();

        if (userNameInput.length() >= 5 && userNameInput.contains(" ")) {
            currentUser.setUserName(userNameInput);
        } else {
            isCompleteCheck = false;
            nameField.setError("Please enter a valid full name.");
        }

        // deal with userBirthDate
        String userBirthDateInput = birthdateField.getText().toString();
        String userBirthDate = null;
        try {
            Date userBirthDateInputAsDate = inputDateFormat.parse(userBirthDateInput);
            userBirthDate = userBirthDateInputAsDate.toString();
            currentUser.setUserBirthDate(userBirthDate);
        } catch (ParseException exception) {
            isCompleteCheck = false;
            birthdateField.setError("Please enter a valid birth date. Format: mm/dd/yyyy");
        }

        // deal with userBiographyText
        String userBiographyTextInput = biographyField.getText().toString();

        if (userBiographyTextInput.length() >= 4) {
            currentUser.setUserBiographyText(userBiographyTextInput);
        } else {
            isCompleteCheck = false;
            biographyField.setError("Please enter a biography that is at least four characters.");
        }

        // deal with userOriginCountry
        ArrayList<String> userOriginCountryInput = (ArrayList) originCountryField.getChipValues();

        if (userOriginCountryInput.size() == 1) {
            currentUser.setUserOriginCountry(userOriginCountryInput.get(0));
        } else {
            isCompleteCheck = false;
            originCountryField.setError("Please enter an origin country. Only one origin country is allowed.");
        }

        // deal with knownLanguages
        ArrayList<String> knownLanguagesInput = (ArrayList) knownLanguagesField.getChipValues();
        HashSet<String> knownLanguagesInputAsSet = new HashSet<>();
        knownLanguagesInputAsSet.addAll(knownLanguagesInput);
        knownLanguagesInput.clear();
        knownLanguagesInput.addAll(knownLanguagesInputAsSet);
        Collections.sort(knownLanguagesInput);
        currentUser.setKnownLanguages(knownLanguagesInput);

        // deal with exploreLanguages
        ArrayList<String> exploreLanguagesInput = (ArrayList) exploreLanguagesField.getChipValues();
        HashSet<String> exploreLanguagesInputAsSet = new HashSet<>();
        exploreLanguagesInputAsSet.addAll(exploreLanguagesInput);
        exploreLanguagesInput.clear();
        exploreLanguagesInput.addAll(exploreLanguagesInputAsSet);
        Collections.sort(exploreLanguagesInput);
        currentUser.setExploreLanguages(exploreLanguagesInput);

        // deal with exploreCountries
        ArrayList<String> exploreCountriesInput = (ArrayList) exploreCountriesField.getChipValues();
        HashSet<String> exploreCountriesInputAsSet = new HashSet<>();
        exploreCountriesInputAsSet.addAll(exploreCountriesInput);
        exploreCountriesInput.clear();
        exploreCountriesInput.addAll(exploreCountriesInputAsSet);
        Collections.sort(exploreCountriesInput);
        currentUser.setExploreCountries(exploreCountriesInput);

        // deal with isComplete
        currentUser.setComplete(isCompleteCheck);

        // save to database
        if (currentUser.isComplete()) {
            // change the button's text
            continueButton.setText("Saved");
            continueButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            continueButton.setEnabled(false);

            // save the data
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
        } else {
            // re-enable the button because data was not saved
            continueButton.setText("Save");
            continueButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(110, 47, 222)));
            continueButton.setEnabled(true);
        }
    }


    // to get the date selected from the into the edit text field
    private void updateLabel() {
        birthdateField.setText(inputDateFormat.format(calendar.getTime()));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default_fragments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // intent to chat fragment
            if (nextFragment.equals("profile")) {
                Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                intent.putExtra("fragment", "profile");
                startActivity(intent);
            } else {
                // if we are creating profile and hit back button, log out
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(ProfileCreationActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
