package com.lingua.lingua.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lingua.lingua.CameraUtil;
import com.lingua.lingua.CountryInformation;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

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

    private final String TAG = "ProfileFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_profile_appBar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        //TODO: Set the onclick function for the FAB - launches ProfileEdit fragment


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
}