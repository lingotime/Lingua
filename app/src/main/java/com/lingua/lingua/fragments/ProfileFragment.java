package com.lingua.lingua.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.lingua.lingua.CountryInformation;
import com.lingua.lingua.LoginActivity;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.ProfileCreationActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

/* FINALIZED, DOCUMENTED, and TESTED ProfileFragment displays the current user's information. */

public class ProfileFragment extends Fragment {
    Context context;
    private User currentUser;

    private ImageView profileImage;
    private TextView nameText;
    private TextView ageText;
    private TextView biographyText;
    private TextView willingToHostText;
    private TextView originCountryText;
    private TextView knownLanguagesText;
    private ChipGroup knownLanguagesChips;
    private Chip knownLanguagesChip;
    private TextView exploreLanguagesText;
    private ChipGroup exploreLanguagesChips;
    private Chip exploreLanguagesChip;
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

        // set the context
        context = getContext();

        // associate views with java variables
        profileImage = view.findViewById(R.id.fragment_profile_profile_image);
        nameText = view.findViewById(R.id.fragment_profile_name_text);
        ageText = view.findViewById(R.id.fragment_profile_age_text);
        biographyText = view.findViewById(R.id.fragment_profile_biography_text);
        willingToHostText = view.findViewById(R.id.fragment_profile_willing_to_host_text);
        originCountryText = view.findViewById(R.id.fragment_profile_origin_country_text);
        knownLanguagesText = view.findViewById(R.id.fragment_profile_known_languages_text);
        knownLanguagesChips = view.findViewById(R.id.fragment_profile_known_languages_chips);
        knownLanguagesChip = view.findViewById(R.id.fragment_profile_known_languages_chip);
        exploreLanguagesText = view.findViewById(R.id.fragment_profile_explore_languages_text);
        exploreLanguagesChips = view.findViewById(R.id.fragment_profile_explore_languages_chips);
        exploreLanguagesChip = view.findViewById(R.id.fragment_profile_explore_languages_chip);
        exploreCountriesText = view.findViewById(R.id.fragment_profile_explore_countries_text);
        exploreCountriesChips = view.findViewById(R.id.fragment_profile_explore_countries_chips);
        exploreCountriesChip = view.findViewById(R.id.fragment_profile_explore_countries_chip);

        // unwrap the current user
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));

        // set the toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_profile_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                // (Fausto's methods)

                if (id == R.id.menu_profile_fragment_edit_icon) {

                    // load the profile creation page if edit button is clicked
                    goToEdit();

                } else if (id == R.id.menu_profile_fragment_log_out_icon) {

                    // log out if exit button is clicked
                    FirebaseAuth.getInstance().signOut();

                    LoginManager.getInstance().logOut();

                    final Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                return ProfileFragment.super.onOptionsItemSelected(item);
            }
        });

        // populate data from the current user
        Glide.with(this).load(currentUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profileImage);

        nameText.setText(currentUser.getUserName());

        ageText.setText("Age: " + getAge(currentUser.getUserBirthDate()) + " years old");

        biographyText.setText("Bio: " + currentUser.getUserBiographyText());

        if (currentUser.getUserWillingToHost()) {
            willingToHostText.setText("Willing to host guests");
        } else {
            willingToHostText.setVisibility(View.GONE);
        }

        originCountryText.setText("Origin Country: " + currentUser.getUserOriginCountry());

        if (currentUser.getKnownLanguages().isEmpty()) {
            knownLanguagesChip.setText("Add a language...");
            knownLanguagesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToEdit();
                }
            });
        } else {
            knownLanguagesChips.removeView(knownLanguagesChip);
        }

        for (String knownLanguage : currentUser.getKnownLanguages()) {
            Chip knownLanguageChip = new Chip(context);
            knownLanguageChip.setClickable(false);
            knownLanguageChip.setText(knownLanguage);
            knownLanguagesChips.addView(knownLanguageChip);
        }

        if (currentUser.getExploreLanguages().isEmpty()) {
            exploreLanguagesChip.setText("Add a language...");
            exploreLanguagesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToEdit();
                }
            });
        } else {
            exploreLanguagesChips.removeView(exploreLanguagesChip);
        }

        for (String exploreLanguage : currentUser.getExploreLanguages()) {
            Chip exploreLanguageChip = new Chip(context);
            exploreLanguageChip.setClickable(false);
            exploreLanguageChip.setText(exploreLanguage);
            exploreLanguagesChips.addView(exploreLanguageChip);
        }

        if (currentUser.getExploreCountries().isEmpty()) {
            exploreCountriesChip.setText("Add a country...");
            exploreCountriesChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToEdit();
                }
            });
        } else {
            exploreCountriesChips.removeView(exploreCountriesChip);
        }

        for (String exploreCountry : currentUser.getExploreCountries()) {
            Chip exploreCountryChip = new Chip(context);
            exploreCountryChip.setText(exploreCountry);
            exploreCountryChip.setClickable(false);
            exploreCountryChip.setChipIcon(getResources().getDrawable(getResources().getIdentifier(CountryInformation.COUNTRY_CODES.get(exploreCountry) + "_round", "drawable", getActivity().getPackageName())));
            exploreCountriesChips.addView(exploreCountryChip);
        }
    }

    private void goToEdit() {
        // load the profile creation page
        Intent intent = new Intent(context, ProfileCreationActivity.class);
        intent.putExtra("user", Parcels.wrap(currentUser));
        intent.putExtra("fragment", "profile");
        startActivity(intent);
    }

    private int getAge(String birthDateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

            Date birthDate = dateFormat.parse(birthDateString);
            Date currentDate = new Date();

            return Period.between(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getYears();
        } catch (ParseException exception) {
            Log.e("ExploreAdapter", "There was an issue parsing a user's registered birth date.");
        }

        return 0;
    }
}
