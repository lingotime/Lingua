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
import com.lingua.lingua.VideoChatActivity;

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

    private static final String FRIEND_REQUEST_NOTIFICATION = "friend-request";
    private static final String VIDEO_CHAT_NOTIFICATION = "video-chat";

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
        Map<String,String> data = message.getData();

        String title = data.get(NOTIFY_TITLE_DATA_KEY);
        String notificationType = checkTypeOfNotification(title);
        String body = data.get(NOTIFY_BODY_DATA_KEY);

        if (notificationType == FRIEND_REQUEST_NOTIFICATION) {
            showNotification(body);
        } else {
            String senderId = data.get("fromIdentity");
            String roomName = data.get("roomName");
            showVideoNotification(body, roomName);
        }
    }

    /**
     * Create and show a simple notification containing the GCM message.
     *
     * @param message GCM message received.
     */
    private void showNotification(String message) {

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

    // show a notification for video chat
    private void showVideoNotification(String message, String roomName) {
        Intent intent = new Intent(this, VideoChatActivity.class);
        intent.setAction("Launch Push Notification");
        intent.putExtra("roomName", roomName);
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

    // to distinguish between the friend request and video chat notifications
    private String checkTypeOfNotification(String title) {
        String[] words = title.split(" ");
        if (words[0] == "Friend") {
            // friend request
            return FRIEND_REQUEST_NOTIFICATION;
        }
        return VIDEO_CHAT_NOTIFICATION;
    }

}
