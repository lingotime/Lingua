package com.lingua.lingua.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;
import org.parceler.Parcels;
import java.io.File;
import java.io.IOException;

/* FINALIZED, DOCUMENTED, and TESTED ProfilePhotoSetupActivity allows a user to setup their account profile photo. */

public class ProfilePhotoSetupActivity extends AppCompatActivity {
    private User currentUser;

    File localProfilePhotoFile;
    private Button takePhotoButton;
    private Button selectPhotoButton;
    private ImageView profilePreviewImage;
    private Button setProfilePhotoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_setup);

        // associate views with java variables
        takePhotoButton = findViewById(R.id.activity_profile_photo_setup_take_photo_button);
        selectPhotoButton = findViewById(R.id.activity_profile_photo_setup_select_photo_button);
        profilePreviewImage = findViewById(R.id.activity_profile_photo_setup_profile_image_preview);
        setProfilePhotoButton = findViewById(R.id.activity_profile_photo_setup_set_photo_button);

        // unwrap the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // launch camera view if the "take photo" button is clicked
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                localProfilePhotoFile = getProfilePhotoFile("profile_photo.jpg");

                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ProfilePhotoSetupActivity.this, "com.lingua.fileprovider", localProfilePhotoFile));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1034);
                }
            }
        });

        // launch photos view if the "select photo" button is clicked
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1046);
                }
            }
        });

        // load the current profile photo if one is available
        Glide.with(this).load(currentUser.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePreviewImage);

        // save new profile photo and return to previous page if the "set profile photo" button is clicked
        setProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localProfilePhotoFile != null) {
                    // get URI of the new profile photo
                    Uri localProfilePhotoFileURI = Uri.fromFile(localProfilePhotoFile);

                    // create references to cloud storage location
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference specificProfilesStorageReference = storageReference.child("profiles/" + currentUser.getUserID() + "_profile.jpg");

                    // upload the new profile photo
                    UploadTask uploadTask = specificProfilesStorageReference.putFile(localProfilePhotoFileURI);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("ProfileSetupActivity", "The new profile photo was successfully uploaded.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ProfileSetupActivity", "The new profile photo failed to upload successfully.");
                        }
                    });

                    // generate a URL link to the new profile photo's cloud storage location
                    Task<Uri> generateURLTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (task.isSuccessful()) {
                                return specificProfilesStorageReference.getDownloadUrl();
                            } else {
                                Log.e("ProfileSetupActivity", "There was an error generating the URL for the new profile photo.");
                                return null;
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // get the URL link for the user's new profile photo
                                Uri profilePhotoURI = task.getResult();

                                // update the user's profile photo with the new profile photo URL link
                                currentUser.setUserProfilePhotoURL(profilePhotoURI.toString());

                                // save updates
                                Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
                                databaseReference.child(currentUser.getUserID()).setValue(currentUser);

                                // return to info setup activity
                                final Intent intent = new Intent(ProfilePhotoSetupActivity.this, ProfileInfoSetupActivity.class);
                                intent.putExtra("user", Parcels.wrap(currentUser));
                                startActivity(intent);
                            } else {
                                Log.e("ProfileSetupActivity", "There was an error generating the URL for the new profile photo.");
                            }
                        }
                    });
                } else {
                    // return to info setup activity
                    final Intent intent = new Intent(ProfilePhotoSetupActivity.this, ProfileInfoSetupActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentUser));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1034) {
            if (resultCode == RESULT_OK) {
                Bitmap localProfilePhoto = BitmapFactory.decodeFile(localProfilePhotoFile.getAbsolutePath());

                Matrix rotationMatrix = new Matrix();
                rotationMatrix.postRotate(-90);
                localProfilePhoto = Bitmap.createBitmap(localProfilePhoto, 0, 0, localProfilePhoto.getWidth(), localProfilePhoto.getHeight(), rotationMatrix, true);

                Glide.with(this).load(localProfilePhoto).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePreviewImage);
            } else {
                Toast.makeText(this, "You did not take a photo.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 1046) {
            if (data != null) {
                Uri localProfilePhotoURI = data.getData();

                localProfilePhotoFile = getProfilePhotoFile(this, localProfilePhotoURI);

                try {
                    Bitmap localProfilePhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), localProfilePhotoURI);

                    Glide.with(this).load(localProfilePhoto).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePreviewImage);
                } catch (IOException e) {
                    Log.e("ProfileSetupActivity", "There was an error reading the selected photo file.");
                }
            }
        }
    }

    private File getProfilePhotoFile(String fileName) {
        // get the photo directory
        File mediaStorageDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Lingua");

        // create the photo directory if it does not exist
        if (!mediaStorageDirectory.exists() && !mediaStorageDirectory.mkdirs()) {
            Log.e("ProfileSetupActivity", "There was an error creating a photo directory.");
        }

        // get the profile photo file
        File file = new File(mediaStorageDirectory.getPath() + File.separator + fileName);

        return file;
    }

    private File getProfilePhotoFile(Context context, Uri uri) {
        // use a cursor to get profile photo selection
        Cursor cursor = null;

        try {
            String[] projection = {MediaStore.Images.Media.DATA};

            cursor = context.getContentResolver().query(uri, projection, null, null, null);

            cursor.moveToFirst();

            // get the profile photo file
            File file = new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

            return file;
        } catch (Exception exception) {
            Log.e("ProfileSetupActivity", "There was an error fetching the selected photo file.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
}