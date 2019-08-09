package com.lingua.lingua;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.User;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/* FINALIZED, DOCUMENTED, and TESTED ProfilePhotoSetupActivity allows a user to setup their account profile photo. */

public class ProfilePicture extends AppCompatActivity {
    private User currentUser;

    File localProfilePhotoFile;
    private FloatingActionButton takePhotoButton;
    private FloatingActionButton selectPhotoButton;
    private ImageView profilePreviewImage;
    private Button setProfilePhotoButton;
    private StorageReference specificStorageReference;
    private static final int CAMERA_REQUEST_CODE = 1034;
    private static final int PHOTO_GALLERY_REQUEST_CODE = 1046;
    private static final int RC_VIDEO_APP_PERM = 124;

    private String nextFragment;
    private Chat groupchat;
    private boolean isNewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        Firebase.setAndroidContext(this);

        Toolbar toolbar = findViewById(R.id.activity_profile_picture_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("chat")) {
            groupchat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));
            isNewGroup = getIntent().getBooleanExtra("isNewGroup", false);
            getSupportActionBar().setTitle("Set Group Picture");
        } else {
            getSupportActionBar().setTitle("Set Profile Picture");
        }

        // associate views with java variables
        takePhotoButton = findViewById(R.id.activity_profile_picture_camera_btn);
        selectPhotoButton = findViewById(R.id.activity_profile_picture_gallery_btn);
        profilePreviewImage = findViewById(R.id.activity_profile_photo_setup_profile_image_preview);
        setProfilePhotoButton = findViewById(R.id.activity_profile_photo_setup_set_photo_button);

        // unwrap the current user
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        nextFragment = getIntent().getStringExtra("fragment");

        // launch camera view if the "take photo" button is clicked
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request permission before accessing the camera, and then launch it
                requestPermissionsAndLaunchCamera();
            }
        });

        // launch photos view if the "select photo" button is clicked
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request permission before accessing the gallery, and then launch it
                requestPermissionsAndLaunchGallery();
            }
        });

        // load the current profile photo if one is available
        if (groupchat == null) {
            Glide.with(this).load(currentUser.getUserProfilePhotoURL()).centerCrop().placeholder(R.drawable.man).into(profilePreviewImage);
        } else {
            Glide.with(this).load(groupchat.getChatPhotoUrl()).centerCrop().placeholder(R.drawable.group_placeholder).into(profilePreviewImage);
        }

        // create references to cloud storage location
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        specificStorageReference = storageReference.child("profiles/" + currentUser.getUserID() + "_profile.jpg");
        if (groupchat != null) {
            specificStorageReference = storageReference.child("groupchats/" + groupchat.getChatID() + "_photo.jpg");
        }

        // save new profile photo and return to previous page if the "set profile photo" button is clicked
        setProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localProfilePhotoFile != null) {
                    // disable the button and change its text
                    setProfilePhotoButton.setText("Setting");
                    setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                    setProfilePhotoButton.setEnabled(false);

                    // get URI of the new profile photo
                    Uri localProfilePhotoFileURI = Uri.fromFile(localProfilePhotoFile);

                    // upload the new profile photo
                    UploadTask uploadTask = specificStorageReference.putFile(localProfilePhotoFileURI);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("ProfileSetupActivity", "The new photo was successfully uploaded.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // re-enable the button
                            setProfilePhotoButton.setText("Try Again");
                            setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(110,47,222)));
                            setProfilePhotoButton.setEnabled(true);

                            Log.e("ProfileSetupActivity", "The new photo failed to upload successfully.");
                        }
                    });

                    // generate a URL link to the new profile photo's cloud storage location
                    Task<Uri> generateURLTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (task.isSuccessful()) {
                                return specificStorageReference.getDownloadUrl();
                            } else {
                                // re-enable the button
                                setProfilePhotoButton.setText("Try Again");
                                setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(110,47,222)));
                                setProfilePhotoButton.setEnabled(true);

                                Log.e("ProfileSetupActivity", "There was an error generating the URL for the new photo.");
                                return null;
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // get the URL link for the user's new profile photo
                                Uri photoUri = task.getResult();

                                if (groupchat != null) {
                                    groupchat.setChatPhotoUrl(photoUri.toString());
                                } else {
                                    // update the user's profile photo with the new profile photo URL link
                                    currentUser.setUserProfilePhotoURL(photoUri.toString());
                                }

                                // disable the button and change its text
                                setProfilePhotoButton.setText("Set");
                                setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                                setProfilePhotoButton.setEnabled(false);

                                // save updates
                                Firebase reference = new Firebase("https://lingua-project.firebaseio.com/users/" + currentUser.getUserID());
                                reference.child("userProfilePhotoURL").setValue(currentUser.getUserProfilePhotoURL());

                                if (groupchat != null) {
                                    final Intent intent = new Intent(ProfilePicture.this, CreateGroupActivity.class);
                                    intent.putExtra("user", Parcels.wrap(currentUser));
                                    intent.putExtra("chat", Parcels.wrap(groupchat));
                                    intent.putExtra("isNewGroup", isNewGroup);
                                    startActivity(intent);
                                } else {
                                    // return to info setup activity
                                    final Intent intent = new Intent(ProfilePicture.this, ProfileCreationActivity.class);
                                    intent.putExtra("user", Parcels.wrap(currentUser));
                                    intent.putExtra("fragment", nextFragment);
                                    startActivity(intent);
                                }
                            } else {
                                // re-enable the button
                                setProfilePhotoButton.setText("Try Again");
                                setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(110,47,222)));
                                setProfilePhotoButton.setEnabled(true);

                                Log.e("ProfileSetupActivity", "There was an error generating the URL for the new photo.");
                            }
                        }
                    });
                } else {
                    // disable the button and change its text
                    setProfilePhotoButton.setText("Set");
                    setProfilePhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                    setProfilePhotoButton.setEnabled(false);

                    Toast.makeText(ProfilePicture.this, "There was no new photo.", Toast.LENGTH_SHORT);

                    if (groupchat != null) {
                        final Intent intent = new Intent(ProfilePicture.this, CreateGroupActivity.class);
                        intent.putExtra("user", Parcels.wrap(currentUser));
                        intent.putExtra("chat", Parcels.wrap(groupchat));
                        intent.putExtra("isNewGroup", isNewGroup);
                        startActivity(intent);
                    } else {
                        // return to info setup activity
                        final Intent intent = new Intent(ProfilePicture.this, ProfileCreationActivity.class);
                        intent.putExtra("user", Parcels.wrap(currentUser));
                        intent.putExtra("fragment", nextFragment);
                        startActivity(intent);
                    }
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap localProfilePhoto = rotateBitmapOrientation(localProfilePhotoFile.getAbsolutePath());
                Glide.with(this).load(localProfilePhoto).centerCrop().placeholder(R.drawable.man).into(profilePreviewImage);
            } else {
                Toast.makeText(this, "You did not take a photo.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_GALLERY_REQUEST_CODE) {
            if (data != null) {
                Uri localProfilePhotoURI = data.getData();

                localProfilePhotoFile = getProfilePhotoFile(this, localProfilePhotoURI);
                Bitmap localProfilePhoto  = rotateBitmapOrientation(getRealPathFromURI(localProfilePhotoURI));
                Glide.with(this).load(localProfilePhoto).placeholder(R.drawable.man).centerCrop().into(profilePreviewImage);
            }
        }
    }

    // getting the filepath from the URI
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    // to rotate the images to the correct orientation
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissionsAndLaunch() {
        String[] perms = { Manifest.permission.CAMERA };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // launch the activity for the camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            localProfilePhotoFile = getProfilePhotoFileFromCamera("profile_photo.jpg");

            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ProfilePicture.this, "com.lingua.fileprovider", localProfilePhotoFile));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        } else {
            // prompt to ask for mic and camera permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera.", RC_VIDEO_APP_PERM, perms);
        }
    }

    private void requestPermissionsAndLaunchCamera() {
        String[] perms = { Manifest.permission.CAMERA };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // launch the activity for the camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            localProfilePhotoFile = getProfilePhotoFileFromCamera("profile_photo.jpg");

            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ProfilePicture.this, "com.lingua.fileprovider", localProfilePhotoFile));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        } else {
            // prompt to ask for mic and camera permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera.", CAMERA_REQUEST_CODE, perms);
        }
    }

    private void requestPermissionsAndLaunchGallery() {
        String[] perms = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // launch the activity for the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, PHOTO_GALLERY_REQUEST_CODE);
            }
        } else {
            // prompt to ask for read and write photo permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your photo gallery.", PHOTO_GALLERY_REQUEST_CODE, perms);
        }
    }


    private File getProfilePhotoFileFromCamera(String fileName) {
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