package com.lingua.lingua.supports;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/* Contains helper methods for getting image files from the camera and camera roll. */

public class CameraUtility {
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public final static String TAG = "CameraUtility";

    // member variables concerned with the camera activity
    public final String APP_NAME = "Lingua";
    public final String PHOTO_FILE_NAME = "photo.jpg";

    File photoFile;

    // Returns File for a photo stored on the disk, given the file name
    public static File getPhotoFileUri(Context context, String fileName) {
        // Get safe storage directory for photos.
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