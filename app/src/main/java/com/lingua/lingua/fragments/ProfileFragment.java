package com.lingua.lingua.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lingua.lingua.CameraUtil;
import com.lingua.lingua.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView ivProfile;
    TextView tvUsername;

    public String photoFileName = "photo.jpg";
    private File photoFile;
    private final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        // TODO: Set user information

//        tvUsername.setText(user.getUsername());

//        try {
//            ParseFile profilePicFile = user.fetchIfNeeded().getParseFile("profilePicture");
//            if (profilePicFile != null) {
//                RequestOptions requestOptionsMedia = new RequestOptions();
//                requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
//                Glide.with(this)
//                        .load(profilePicFile.getUrl())
//                        .apply(requestOptionsMedia)
//                        .into(ivProfile);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "activity result");
        if (requestCode == CameraUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                RequestOptions requestOptionsMedia = new RequestOptions();
                requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
                Glide.with(this)
                        .load(takenImage)
                        .apply(requestOptionsMedia)
                        .into(ivProfile);

                // TODO: set profile pic
            } else {
                Log.e(TAG, "Picture wasn't taken");
            }
        }
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = CameraUtil.getPhotoFileUri(getContext(), photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.lingua.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CameraUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

}