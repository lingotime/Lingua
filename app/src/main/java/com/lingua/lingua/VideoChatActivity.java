package com.lingua.lingua;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.User;
import com.lingua.lingua.notifyAPI.Invite;
import com.lingua.lingua.notifyAPI.Notification;
import com.lingua.lingua.notifyAPI.TwilioFunctionsAPI;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.H264Codec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;
import com.twilio.video.Vp8Codec;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.webrtc.MediaCodecVideoDecoder;
import org.webrtc.MediaCodecVideoEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Chat used to generate the video chat rooms and tokens for 2 different users, at the moment. Multiway and monitoring will soon be enabled
 */

public class VideoChatActivity extends AppCompatActivity {

    private VideoTokenGenerator tokenGenerator;
    private Room room;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private CameraCapturer cameraCapturer;

    private LocalParticipant localParticipant;
    private static final int RC_VIDEO_APP_PERM = 124;

    // fields from the layout
    private ImageButton connectionButton;
    private ImageButton disconnectionButton;
    private VideoView remoteVideoView;
    private VideoView localVideoView;
    private ImageButton switchCameraButton;

    private final static String TAG = "VideoChatActivity";
    // just for the initial test
    private VideoCodec videoCodec;
    private String roomName;
    private String userId;
    private String username;
    private String chatId;
    private ArrayList<String> chatMembers; // excluding the current user
    private String videoChatLanguage;
    private Firebase reference;
    private ArrayList<String> possibleChatLanguages = new ArrayList<>();

    User currentUser;
    Chat currentChat;
    String[] languageChoices;

    // variables to keep track of the length of the call
    private long startTime = 0;
    private long endTime = 0;


    private Integer currentDuration; // represents the number of minutes the user has already spoken in the language selected

    // to handle the action attached to the intent - to distinguish between that coming from one of the chat activities and one coming from clicking a push notification
    private String intentAction;
    private final static String PUSH_NOTIFICATION_INTENT = "Launch Push Notification"; // launched from sending the notification
    private final static String CHAT_DETAILS_INTENT = "Launch From Chat Details"; // intent sent from the ChatDetailsActivity
    private final static String CHAT_FRAGMENT_INTENT = "Launch From Chat Fragment"; // intent sent from the Chat Fragment



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        // getting the current user from the shared preferences
        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        username = prefs.getString("userName", "");

        // check for the intent action to see if the intent was launched from the chats or from a push notification
        intentAction = getIntent().getAction();
        if (intentAction.equals(PUSH_NOTIFICATION_INTENT)) {
            roomName = getIntent().getStringExtra("roomName");
            Log.d(TAG, "Push notification arrived at the video chat activity");
        } else {
            // intent passed in from either the chat fragment or the chat details activity with these parcelable extras
            currentChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));
            currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
            chatId = currentChat.getChatID();
            roomName = chatId;
        }

        // the intent from the push notification will not have the user object
        if (currentChat != null) {
            chatMembers = currentChat.getChatParticipants();
            chatMembers.remove(userId);

            // to get all the possible explore languages from the users in the chat
            if (currentChat.getChatLanguages() != null) {
                possibleChatLanguages.addAll(currentChat.getChatLanguages());
            }
            possibleChatLanguages.add("Cultural Exchange");

            languageChoices = possibleChatLanguages.toArray(new String[possibleChatLanguages.size()]);
        }


        // setting up Firebase to receive the messages to be sent
        Firebase.setAndroidContext(VideoChatActivity.this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chatId);

        localVideoView = (VideoView) findViewById(R.id.activity_video_chat_publisher_container);
        remoteVideoView = (VideoView) findViewById(R.id.activity_video_chat_subscriber_container);
        connectionButton = (ImageButton) findViewById(R.id.activity_video_chat_connect);
        disconnectionButton = (ImageButton) findViewById(R.id.activity_video_chat_disconnect);
        switchCameraButton = (ImageButton) findViewById(R.id.activity_video_switch_camera);

        disconnectionButton.setVisibility(View.GONE); // hides if a call has not yet begun
        disconnectionButton.setEnabled(false);

        disconnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (room != null) {
                    room.disconnect();
                    disconnectActions();
                }
            }
        });

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });


        requestPermissions();
    }

    // gets the information for the chat containing the video chat from the database and generates a local Chat object
    private void queryAndUpdateHoursSpokenInfo() {
        String databaseURL = "https://lingua-project.firebaseio.com/users" + userId;

        // fetch user from database
        StringRequest databaseRequest = new StringRequest(Request.Method.GET, databaseURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject userJSONObject = new JSONObject(response);
                    JSONArray exploreLanguages = userJSONObject.getJSONArray("exploreLanguages");
                    if (exploreLanguages.toString().contains("\"exploreLanguages\":\""+videoChatLanguage+"\"")) {
                        JSONObject hoursSpokenObject = userJSONObject.getJSONObject("hoursSpokenByLanguage");
                        if (hoursSpokenObject != null) {
                            if (hoursSpokenObject.has(videoChatLanguage)) {
                                currentDuration = hoursSpokenObject.getInt(videoChatLanguage);
                            } else {
                                currentDuration = 0;
                            }
                            updateUserLanguageProgress(lengthOfCall(startTime, endTime));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("VideoChatCurrentChat", e.toString());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VideoChatCurrentChat", error.toString());
            }
        });

        RequestQueue databaseRequestQueue = Volley.newRequestQueue(this);
        databaseRequestQueue.add(databaseRequest);
    }



    private void callLanguageDialog() {
        // first prompt the user initiating the call for the intended language
        // a dialog box to allow the person initiating the call to select the language in which the call will be made
        AlertDialog.Builder languageSelection = new AlertDialog.Builder(VideoChatActivity.this);
        languageSelection.setTitle("Choose the language");
        languageSelection.setSingleChoiceItems(languageChoices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                videoChatLanguage = Arrays.asList(languageChoices).get(i);
            }
        });
        languageSelection.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Firebase.setAndroidContext(VideoChatActivity.this);
                Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/video-chats/" + roomName);
                databaseReference.setValue(videoChatLanguage);

                // send notifications to join to everyone in the chat
                for (int index = 0; index < chatMembers.size(); index++) {
                    Log.d("VideoPushNotifications", "Sending notification to " + chatMembers.get(index));
                    sendVideoChatNotification(chatMembers.get(index));
                }
            }
        });
        languageSelection.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(VideoChatActivity.this, "Video chat canceled", Toast.LENGTH_SHORT).show();
                // disconnect from the room if action not taken
                disconnectActions();
            }
        });

        AlertDialog dialog = languageSelection.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    // UI options to take when the user has been connected to the room
    private void connectActions() {
        connectionButton.setVisibility(View.GONE); // once connected, remove the button from view to prevent more connection attempts
        connectionButton.setEnabled(false);

        // enable button for disconnection to end the call
        disconnectionButton.setVisibility(View.VISIBLE);
        disconnectionButton.setEnabled(true);
    }


    // steps to be taken to adjust the view when the local participant is disconnected
    private void disconnectActions() {
        moveLocalVideoToMainView();
        connectionButton.setVisibility(View.VISIBLE);
        connectionButton.setEnabled(true);
        endTime = System.nanoTime();

        if (videoChatLanguage == null) {
            // query the language from the database and update it for the current user
            queryCallLanguage();
        } else if (!videoChatLanguage.equals("Cultural Exchange")) {
            // mark progress for the user if one of their explore languages is the one being spoken in the chat
            queryAndUpdateHoursSpokenInfo();
        }

        // return the user to the activity from which they came
        String intentAction = getIntent().getAction();
        if (intentAction.equals(CHAT_FRAGMENT_INTENT)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", Parcels.wrap(currentUser));
            intent.putExtra("fragment", "chats");
        } else if (intentAction.equals(CHAT_DETAILS_INTENT)) {
            Intent intent = new Intent(this, TextChatActivity.class);
            intent.putExtra("chat", Parcels.wrap(currentChat));
            intent.putExtra("user", Parcels.wrap(currentUser));
        }
    }

    // finds out which camera is being used and switches to the other one
    private void switchCamera() {
        if (cameraCapturer != null) {
            cameraCapturer.switchCamera();
        }
    }

    // calculates the length of the call and returns a value in minutes
    private double lengthOfCall(long start, long end) {
        long duration = TimeUnit.MINUTES.convert(end-start, TimeUnit.NANOSECONDS);
        return Math.floor(duration);
    }

    // storing the time spent speaking in the language chosen at the start of the activity
    private void updateUserLanguageProgress(double duration) {
        // update the local current user object
        // update in the database
        Firebase.setAndroidContext(this);
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users/" + userId);
        databaseReference.child("hoursSpokenByLanguage").child(videoChatLanguage).setValue(duration + currentDuration);
    }


    private void queryCallLanguage() {
        String url = "https://lingua-project.firebaseio.com/video-chats";
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            try {
                JSONObject object = new JSONObject(s);
                videoChatLanguage = object.getString(roomName);
                if (!videoChatLanguage.equals("Cultural Exchange")) {
                    // mark progress for the user if one of their explore languages is the one being spoken in the chat
                    queryAndUpdateHoursSpokenInfo();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            Log.e("VideoChatActivity", "" + volleyError);
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }


    private void connectToRoom(String roomName) {

        // generate the Twilio room and token with the given chat name and the current user as the first identity
        tokenGenerator = new VideoTokenGenerator(userId, roomName, this.getString(R.string.twilio_sid), this.getString(R.string.twilio_api), this.getString(R.string.twilio_secret_key));
        Log.i(TAG, tokenGenerator.JwtToken);

        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(tokenGenerator.JwtToken).roomName(roomName);
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }
        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));
        connectOptionsBuilder.enableAutomaticSubscription(true);

        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());

    }


    private void sendVideoChatNotification(String recipientId) {
        // creating the invite object
        Invite invite = new Invite(userId, roomName);
        // send notification
        Notification notification = new Notification("Join " + userId +" in " + roomName, username + " would like to video chat!", recipientId, invite.getMap());

        TwilioFunctionsAPI.notify(notification).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccess()) {
                    String message = "Sending notification failed: " + response.code() + " " + response.message();
                    Log.e("VideoPushNotification", message);
                } else {
                    Log.i("VideoPushNotification", "Sending notification success: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String message = "Sending notification failed: " + t.getMessage();
                Log.e("VideoPushNotification", message);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // after permission is granted, initialise the video and audio tracks
            getVideoAndAudioTracks();
            connectToRoom(roomName);
        } else {
            // prompt to ask for mic and camera permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }


    private void getVideoAndAudioTracks() {
        //Check if H.264 is supported in this device
        boolean isH264Supported = MediaCodecVideoDecoder.isH264HwSupported() &&
                MediaCodecVideoEncoder.isH264HwSupported();

        // Prefer H264 if it is hardware available for encoding and decoding
        videoCodec = isH264Supported ? (new H264Codec()) : (new Vp8Codec());

        // Create an audio track
        localAudioTrack = LocalAudioTrack.create(this, true);

        // A video track requires an implementation of VideoCapturer - could include the back camera
        cameraCapturer = new CameraCapturer(this,
                CameraCapturer.CameraSource.FRONT_CAMERA);

        // Create a video track using the device's front camera
        localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);

        // getting the publisher container (for the local participant) and setting visibility to gone before the other participant enters the chat)
        localVideoView.setVisibility(View.GONE);

        // show the camera output from the user in the main screen
        remoteVideoView.setMirror(true);
        // Rendering a local video track requires an implementation of VideoRenderer
        // Render a local video track to preview your camera
        localVideoTrack.addRenderer(remoteVideoView);
    }

    private void moveLocalVideoToSmallView() {
        // moves the local participant's local video track from the main view to make space for the remote participant
        if (localVideoView.getVisibility() == View.GONE) {
            localVideoView.setVisibility(View.VISIBLE);
            if (localVideoTrack != null) {
                localVideoTrack.removeRenderer(remoteVideoView); // remove from the main view where the remote participant will be added
                localVideoTrack.addRenderer(localVideoView); // add to the smaller view
            }
            localVideoView.setMirror(true);
        }
        remoteVideoView.setMirror(false);
    }



    private void moveLocalVideoToMainView() {
        // moves the local participant's video track from the smaller view when there is no longer a remote participant
        if (localVideoView.getVisibility() == View.VISIBLE) {
            localVideoView.setVisibility(View.GONE);
            if (localVideoTrack != null) {
                localVideoTrack.removeRenderer(localVideoView);
                localVideoTrack.addRenderer(remoteVideoView);
            }
            remoteVideoView.setMirror(true);
        }
        localVideoView.setMirror(false);
    }

    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (localVideoView.getVisibility() == View.VISIBLE) {
            return;
        }

        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);


            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }

        // Start listening for participant events
        remoteParticipant.setListener(remoteParticipantListener());

    }

    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToSmallView();
        remoteVideoView.setMirror(false);
        videoTrack.addRenderer(remoteVideoView);
    }

    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        moveLocalVideoToMainView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(remoteVideoView);
    }


    private void sendTextChat(String messageText) {
        String timestamp = new Date().toString();
        // save message
        Map<String, String> map = new HashMap<>();
        map.put("message", messageText);
        map.put("senderId", userId);
        map.put("timestamp", timestamp);
        reference.push().setValue(map);

        // set this message to be the lastMessage of the chat
        Firebase chatReference = new Firebase("https://lingua-project.firebaseio.com/chats/" + chatId);
        chatReference.child("lastMessage").setValue(username + ": " + messageText);
        chatReference.child("lastMessageAt").setValue(timestamp);
    }

    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                connectActions();
                localParticipant = room.getLocalParticipant();
                localParticipant.publishTrack(localVideoTrack);
                localParticipant.publishTrack(localAudioTrack);

                // check if the local participant is the first one in the room and there are no remote participants, allow them to set the language for the chat
                if (room.getRemoteParticipants().size() == 0) {
                    callLanguageDialog();
                }

                Log.i(TAG, "Connected to " + room.getName());
                // send a message to the other user to notify them of the creation of the room
                sendTextChat("Video chat with me!");
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {
                Log.i(TAG, "failure to connect: " + twilioException.toString());
                Log.i(TAG, twilioException.toString());
                Toast.makeText(VideoChatActivity.this, "Failure to connect", Toast.LENGTH_LONG).show();
                // send a message to the other user detailing an attempted call
                sendTextChat("I tried to call you :(");
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                Log.i(TAG, "reconnecting");
                // in the case that a connection failure caused the call to stop, restart tracking the user's progress
                startTime = System.nanoTime();
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                Log.i(TAG, "reconnected");
            }

            @Override
            public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
                localParticipant = null;
                disconnectActions();
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.i(TAG, "new participant connected" + remoteParticipant.getIdentity());
                // In the case that the connected callback is received, the LocalParticipant and the RemoteParticipant objects become available
                localParticipant = room.getLocalParticipant();
                // use getIdentity to receive the identity strings associated with each user
                // to set a listener for the new participant that has been added to the room
                addRemoteParticipant(remoteParticipant);
                remoteParticipant.setListener(remoteParticipantListener());
                startTime = System.nanoTime(); // gets the start time of the call
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.i(TAG, "participant disconnected" + remoteParticipant.getIdentity());
                // move the local participant to the main view
                removeRemoteParticipant(remoteParticipant);
                // disconnect the local participant if all the remote participants have been disconnected
                if (room.getRemoteParticipants().size() == 0) {
                    room.disconnect();
                    disconnectActions();
                }
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {
                Log.i(TAG, "recording started");
            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {
                Log.i(TAG, "recording stopped");
            }
        };
    }

    private boolean checkCameraAndMicPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        }
        return false;
    }

    // listener for the remote participant
    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getVideoTrack());
            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                if (remoteVideoTrackPublication.getRemoteVideoTrack() != null) {
                    remoteVideoTrackPublication.getRemoteVideoTrack().removeRenderer(remoteVideoView);
                }
                moveLocalVideoToMainView();
            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                // render the local participant's video into the publisher container - the smaller video view in the corner of the screen
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                remoteVideoTrack.removeRenderer(remoteVideoView); // removes the remote track from the view of the local participant once disconnected
                moveLocalVideoToMainView();

            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
            }

            @Override
            public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if H.264 is supported in this device
        boolean isH264Supported = MediaCodecVideoDecoder.isH264HwSupported() &&
                MediaCodecVideoEncoder.isH264HwSupported();

        // Prefer H264 if it is hardware available for encoding and decoding
        videoCodec = isH264Supported ? (new H264Codec()) : (new Vp8Codec());

        // recreate the video and audio tracks that were released when the app was put in the background
        if (localVideoTrack == null && checkCameraAndMicPermissions()) {
            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturer);
            localVideoTrack.addRenderer(localVideoView);

            // publish video and audio from the local user once they reopen the app
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
            }
        }
    }

    @Override
    protected void onPause() {
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        if (localVideoTrack != null) {
            // stop publishing video from the local participant's phone once they leave the app
            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
                localParticipant.unpublishTrack(localAudioTrack);
            }

            localVideoTrack.release();
            localVideoTrack = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // when activity is destroyed, release the audio and video tracks from the device, and disconnect the user from the room
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
        }

        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        super.onDestroy();
    }

}
