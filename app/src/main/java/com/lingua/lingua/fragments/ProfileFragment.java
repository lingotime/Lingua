package com.lingua.lingua.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lingua.lingua.CountryInformation;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.ProfileCreationActivity;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

/*
Fragment that displays the user's profile and allows them to edit it
*/

public class ProfileFragment extends Fragment {

    AppCompatImageView userImage;
    TextView userName;

    AppCompatImageView flagImage;
    ChipGroup origin;
    ChipGroup targetLangs;
    ChipGroup targetCounts;
    ChipGroup primaryLangs;
    TextView dob;
    FloatingActionButton editFab;
    public final int EDIT_REQUEST_CODE = 250;

    private final String TAG = "ProfileFragment";

    User currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_profile_appBar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

        dob = view.findViewById(R.id.fragment_profile_dob);
        userImage = view.findViewById(R.id.fragment_profile_userImage);
        userName = view.findViewById(R.id.fragment_profile_userName);
        flagImage = view.findViewById(R.id.fragment_profile_originFlag);
        origin = view.findViewById(R.id.fragment_profile_origin);
        targetLangs = view.findViewById(R.id.fragment_profile_targetLangChips);
        targetCounts = view.findViewById(R.id.fragment_profile_targetCountChips);
        primaryLangs = view.findViewById(R.id.fragment_profile_primaryChips);
        editFab = view.findViewById(R.id.fragment_profile_FAB);

        // TODO: Allow for the creation from the explore page to see other profiles, with User passed as Parcelables. In this scenario, the FAB visibility will be set to GONE

        //TODO: Set the onclick function for the FAB - launches ProfileCreation activity
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile(view);
            }
        });


        // TODO: Set user information, and format the date for display

        //TODO: Set the action bar title as the name of the user, and include their picture
    }

    // TODO: Format the flags so they fit more snugly into the chips and the collapsing toolbar
    // add the country chips to the layout along with their flags
    public void addCountryChips(String country, ChipGroup chipGroup) {
        // creating each of the chips to represent the countries and the languages
        Chip chip = new Chip(getContext());
        chip.setText(country);

        // getting the drawable that represents the flag for the given country
        String fileName = CountryInformation.COUNTRY_CODES.get(country);
        int id = getContext().getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
        Drawable drawable = getContext().getResources().getDrawable(id);

        // convert the drawable to an icon compat

        chip.setChipIcon(drawable);

        //adding the new chip to the given ChipGroup
        chipGroup.addView(chip);
    }

    // add the language chips
    public void addLangChips(String language, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        chip.setText(language);
        chipGroup.addView(chip);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void editProfile(View view) {
        Intent intent = new Intent(getContext(), ProfileCreationActivity.class);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
}