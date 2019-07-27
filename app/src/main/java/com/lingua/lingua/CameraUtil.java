package com.lingua.lingua;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
* Contains helper methods for getting image files from the camera and camera roll
*/

public class CameraUtil {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String TAG = "CameraUtil";
    // member variables concerned with the camera activity
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";
    File photoFile;
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    // Returns the File for a photo stored on disk given the fileName
    public static File getPhotoFileUri(Context context, String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MainActivity");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}