package com.lingua.lingua.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lingua.lingua.MainActivity;
import com.lingua.lingua.R;

import java.util.Map;

public class NotifyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "NotifyFCMService";

    /*
     * The Twilio Notify message data keys are as follows:
     *  "twi_body"   // The body of the message
     *  "twi_title"  // The title of the message
     *
     * You can find a more detailed description of all supported fields here:
     * https://www.twilio.com/docs/api/notifications/rest/notifications#generic-payload-parameters
     */
    private static final String NOTIFY_TITLE_DATA_KEY = "twi_title";
    private static final String NOTIFY_BODY_DATA_KEY = "twi_body";

    @Override
    public void onNewToken(String s) {
        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: " + s);
        super.onNewToken(s);
    }

    /**
     * Called when message is received.
     *
     * @param message The remote message, containing from, and message data as key/value pairs.
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        /*
         * The Notify service adds the message body to the remote message data so that we can
         * show a simple notification.
         */
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        Log.d(TAG, data.toString());

        String title = data.get(NOTIFY_TITLE_DATA_KEY);
        String body = data.get(NOTIFY_BODY_DATA_KEY);

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Body: " + body);
        Log.d(TAG, "Title: " + title);

        showNotification(title, body);
    }

    /**
     * Create and show a simple notification containing the GCM message.
     *
     * @param message GCM message received.
     */
    private void showNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "notifications");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Lingua")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //if this is an Android Oreo device, set the notification channel
        String CHANNEL_ID = "notify_channel";
        String CHANNEL_NAME = "Notify Channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            Log.d(TAG, "setting channel");
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}