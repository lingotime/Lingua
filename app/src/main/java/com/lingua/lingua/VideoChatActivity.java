package com.lingua.lingua;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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


import com.firebase.client.Firebase;
import com.lingua.lingua.models.User;
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


/**
 * Chat used to generate the video chat rooms and tokens for 2 different users, at the moment. Multiway and monitoring will soon be enabled
 */

public class VideoChatActivity extends AppCompatActivity {

    private VideoTokenGenerator tokenGenerator;
    private Room room;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private CameraCapturer cameraCapturer;
    private VideoView remoteVideoView;
    private VideoView localVideoView;
    private LocalParticipant localParticipant;
    private static final int RC_VIDEO_APP_PERM = 124;
    private ImageButton connectionButton;
    private ImageButton disconnectionButton;
    private final static String TAG = "VideoChatActivity";
    // just for the initial test
    private VideoCodec videoCodec;
    private String roomName;
    private String userId;
    private String username;
    private String chatId;
    private String receiverId; // id of the second user in the chat
    private String videoChatLanguage;
    private Firebase reference;
    private ArrayList<String> possibleChatLanguages;

    User currentUser;

    // variables to keep track of the length of the call
    private long startTime = 0;
    private long endTime = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        // getting the current user from the shared preferences
        SharedPreferences prefs = this.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        username = prefs.getString("userName", "");

        chatId = getIntent().getStringExtra("chatID");
        Log.d(TAG, chatId);
        String chatName = getIntent().getStringExtra("name"); // the room will be set to this name
        receiverId = getIntent().getStringExtra("otherUser");
        currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        roomName = chatId;
        receiverId = getIntent().getStringExtra("otherUser");
        possibleChatLanguages = getIntent().getStringArrayListExtra("languages"); // the languages from which the users will choose to speak in (or cultural exchange)'

        String[] languageChoices = possibleChatLanguages.toArray(new String[possibleChatLanguages.size()]);

        // setting up Firebase to receive the messages to be sent
        Firebase.setAndroidContext(VideoChatActivity.this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chatId);

        localVideoView = (VideoView) findViewById(R.id.activity_video_chat_publisher_container);
        remoteVideoView = (VideoView) findViewById(R.id.activity_video_chat_subscriber_container);
        connectionButton = (ImageButton) findViewById(R.id.activity_video_chat_connect);
        disconnectionButton = (ImageButton) findViewById(R.id.activity_video_chat_disconnect);


        disconnectionButton.setVisibility(View.GONE); // hides if a call has not yet begun
        disconnectionButton.setEnabled(false);


        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        Log.d(TAG, "Starting connection");
                        Log.d(TAG, chatId);
                        connectToRoom(chatId);
                        connectionButton.setVisibility(View.GONE); // once connected, remove the button from view to prevent more connection attempts
                        connectionButton.setEnabled(false);

                        // enable button for disconnection to end the call
                        disconnectionButton.setVisibility(View.VISIBLE);
                        disconnectionButton.setEnabled(true);
                    }
                });
                languageSelection.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(VideoChatActivity.this, "Video chat canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = languageSelection.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });


        disconnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (room != null) {
                    room.disconnect();
                    disconnectActions();
                }
            }
        });

        Log.i(TAG, "Inflated the layout for video activity");
        requestPermissions();
    }

    // steps to be taken to adjust the view when the local participant is disconnected
    private void disconnectActions() {
        moveLocalVideoToMainView();
        connectionButton.setVisibility(View.VISIBLE);
        connectionButton.setEnabled(true);
        endTime = System.nanoTime();

        // mark progress for the user if one of their explore languages is the one being spoken in the chat
        if (currentUser.getExploreLanguages().contains(videoChatLanguage)) {
            if (!videoChatLanguage.equals("Cultural Exchange")) {
                // adjust the user's language progress in this case
                updateUserLanguageProgress(lengthOfCall(startTime, endTime));
            }
        }
    }

    // calculates the length of the call and returns a value in minutes
    private double lengthOfCall(long start, long end) {
        long duration = TimeUnit.MINUTES.convert(end-start, TimeUnit.NANOSECONDS);
        return Math.floor(duration);
    }

    // storing the time spent speaking in the language chosen at the start of the activity
    private void updateUserLanguageProgress(double duration) {
        HashMap<String, Integer> hoursSpoken = currentUser.getHoursSpokenPerLanguage();
        if (hoursSpoken.containsKey(videoChatLanguage)) {
            // add to those already stored
            hoursSpoken.put(videoChatLanguage, hoursSpoken.get(videoChatLanguage) + (int) duration);
        } else {
            hoursSpoken.put(videoChatLanguage, (int) duration);
        }

        // update the local current user object
        currentUser.setHoursSpokenPerLanguage(hoursSpoken);

        // update in the database
        Firebase.setAndroidContext(this);
        Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
        databaseReference.child(currentUser.getUserID()).setValue(currentUser);
    }


    private void connectToRoom(String roomName) {

        // generate the Twilio room and token with the given chat name and the current user as the first identity
        tokenGenerator = new VideoTokenGenerator(userId, roomName);
        if (tokenGenerator.token == null) {
            Log.d(TAG, "Token object not generated");
        } else {
            Log.d(TAG, "Token generated");
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        Log.i(TAG, "Requesting permissions");
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        Log.i(TAG, "Gathered permissions");
        if (EasyPermissions.hasPermissions(this, perms)) {
            // after permission is granted, initialise the video and audio tracks
            Log.i(TAG, "Permission granted");
            getVideoAndAudioTracks();
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
                localParticipant = room.getLocalParticipant();
                localParticipant.publishTrack(localVideoTrack);
                localParticipant.publishTrack(localAudioTrack);

                Log.i(TAG, "Connected to " + room.getName());
                // send a message to the other user to notify them of the creation of the room
                sendTextChat("Video chat with me!");
                List<RemoteParticipant> remoteParticipants = room.getRemoteParticipants();
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {
                Log.i(TAG, "failure to connect");
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
                remoteVideoTrackPublication.getRemoteVideoTrack().removeRenderer(remoteVideoView);
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

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
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
