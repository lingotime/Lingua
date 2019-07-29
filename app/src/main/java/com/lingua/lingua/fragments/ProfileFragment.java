package com.lingua.lingua.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.lingua.lingua.R;
import com.lingua.lingua.activities.LoginActivity;
import com.lingua.lingua.activities.ProfileInfoSetupActivity;
import com.lingua.lingua.models.Country;
import com.lingua.lingua.models.User;
import org.parceler.Parcels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* FINALIZED, DOCUMENTED, and TESTED ProfileFragment displays the current user's information. */

public class ProfileFragment extends Fragment {
    private User currentUser;

    private TextView descriptionText;
    private Button editButton;
    private Button exitButton;
    private ImageView profileImage;
    private TextView nameText;
    private TextView birthdateText;
    private TextView genderText;
    private TextView biographyText;
    private TextView originCountryText;
    private TextView knownLanguagesText;
    private ChipGroup knownLanguagesChips;
    private Chip knownLanguagesChip;
    private TextView exploreLanguagesText;
    private ChipGroup exploreLanguagesChips;
    private Chip exploreLanguagesChip;
    private TextView knownCountriesText;
    private ChipGroup knownCountriesChips;
    private Chip knownCountriesChip;
    private TextView exploreCountriesText;
    private ChipGroup exploreCountriesChips;
    private Chip exploreCountriesChip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // associate views with java variables
        descriptionText = view.findViewById(R.id.fragment_profile_description_text);
        editButton = view.findViewById(R.id.fragment_profile_edit_button);
        exitButton = view.findViewById(R.id.fragment_profile_exit_button);
        profileImage = view.findViewById(R.id.fragment_profile_profile_image);
        nameText = view.findViewById(R.id.fragment_profile_name_text);
        birthdateText = view.findViewById(R.id.fragment_profile_birthdate_text);
        genderText = view.findViewById(R.id.fragment_profile_gender_text);
        biographyText = view.findViewById(R.id.fragment_profile_biography_text);
        originCountryText = view.findViewById(R.id.fragment_profile_origin_country_text);
        knownLanguagesText = view.findViewById(R.id.fragment_profile_known_languages_text);
        knownLanguagesChips = view.findViewById(R.id.fragment_profile_known_languages_chips);
        knownLanguagesChip = view.findViewById(R.id.fragment_profile_known_languages_chip);
        exploreLanguagesText = view.findViewById(R.id.fragment_profile_explore_languages_text);
        exploreLanguagesChips = view.findViewById(R.id.fragment_profile_explore_languages_chips);
        exploreLanguagesChip = view.findViewById(R.id.fragment_profile_explore_languages_chip);
        knownCountriesText = view.findViewById(R.id.fragment_profile_known_countries_text);
        knownCountriesChips = view.findViewById(R.id.fragment_profile_known_countries_chips);
        knownCountriesChip = view.findViewById(R.id.fragment_profile_known_countries_chip);
        exploreCountriesText = view.findViewById(R.id.fragment_profile_explore_countries_text);
        exploreCountriesChips = view.findViewById(R.id.fragment_profile_explore_countries_chips);
        exploreCountriesChip = view.findViewById(R.id.fragment_profile_explore_countries_chip);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // load the profile creation page if edit button is clicked
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getContext(), ProfileInfoSetupActivity.class);
                intent.putExtra("user", Parcels.wrap(currentUser));
                startActivity(intent);
            }
        });

        // log out if exit button is clicked
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                LoginManager.getInstance().logOut();

                final Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // populate data from the current user
        Glide.with(this).load(currentUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profileImage);

        nameText.setText(currentUser.getUserName());

        try {
            SimpleDateFormat storedDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date userBirthDateAsDate = storedDateFormat.parse(currentUser.getUserBirthDate());
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            birthdateText.setText("Birth Date: " + displayDateFormat.format(userBirthDateAsDate));
        } catch (ParseException exception) {
            birthdateText.setText("Birth Date: Birthday unavailable.");
            Log.e("ProfileFragment", "There was an issue parsing the user's registered birth date.");
        }

        genderText.setText("Gender: " + currentUser.getUserGender());

        biographyText.setText("Bio: " + currentUser.getUserBiographyText());

        originCountryText.setText("Origin Country: " + currentUser.getUserOriginCountry());

        if (currentUser.getKnownLanguages().isEmpty()) {
            knownLanguagesChip.setText("Add a language...");
            knownLanguagesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editButton.performClick();
                }
            });
        } else {
            knownLanguagesChips.removeView(knownLanguagesChip);
        }

        for (String knownLanguage : currentUser.getKnownLanguages()) {
            Chip knownLanguageChip = new Chip(getContext());
            knownLanguageChip.setText(knownLanguage);
            knownLanguagesChips.addView(knownLanguageChip);
        }

        if (currentUser.getExploreLanguages().isEmpty()) {
            exploreLanguagesChip.setText("Add a language...");
            exploreLanguagesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editButton.performClick();
                }
            });
        } else {
            exploreLanguagesChips.removeView(exploreLanguagesChip);
        }

        for (String exploreLanguage : currentUser.getExploreLanguages()) {
            Chip exploreLanguageChip = new Chip(getContext());
            exploreLanguageChip.setText(exploreLanguage);
            exploreLanguagesChips.addView(exploreLanguageChip);
        }

        if (currentUser.getKnownCountries().isEmpty()) {
            knownCountriesChip.setText("Add a country...");
            knownCountriesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editButton.performClick();
                }
            });
        } else {
            knownCountriesChips.removeView(knownCountriesChip);
        }

        for (String knownCountry : currentUser.getKnownCountries()) {
            Chip knownCountryChip = new Chip(getContext());
            knownCountryChip.setText(knownCountry);
            knownCountryChip.setChipIcon(getResources().getDrawable(getResources().getIdentifier(Country.COUNTRY_CODES.get(knownCountry) + "_round", "drawable", getActivity().getPackageName())));
            knownCountriesChips.addView(knownCountryChip);
        }

        if (currentUser.getExploreCountries().isEmpty()) {
            exploreCountriesChip.setText("Add a country...");
            exploreCountriesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editButton.performClick();
                }
            });
        } else {
            exploreCountriesChips.removeView(exploreCountriesChip);
        }

        for (String exploreCountry : currentUser.getExploreCountries()) {
            Chip exploreCountryChip = new Chip(getContext());
            exploreCountryChip.setText(exploreCountry);
            exploreCountryChip.setChipIcon(getResources().getDrawable(getResources().getIdentifier(Country.COUNTRY_CODES.get(exploreCountry) + "_round", "drawable", getActivity().getPackageName())));
            exploreCountriesChips.addView(exploreCountryChip);
        }
    }
}