package com.lingua.lingua;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.twilio.http.TwilioRestClient;
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
import com.twilio.video.VideoView;
import com.twilio.video.Vp9Codec;

import org.webrtc.MediaCodecVideoDecoder;
import org.webrtc.MediaCodecVideoEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.twilio.Twilio;
import com.twilio.rest.video.v1.RoomFetcher;

/**
 * Chat used to generate the video chat rooms and tokens for 2 different users, at the moment. Multiway and monitoring will soon be enabled
 */

public class VideoChatActivity extends AppCompatActivity {

    private VideoTokenGenerator tokenGenerator;
    private VideoTokenGenerator secondTokenGenerator; // for the second user in the chat
    private Room room;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private CameraCapturer cameraCapturer;
    private VideoView remoteVideoView;
    private VideoView localVideoView;
    private List<RemoteParticipant> remoteParticipants;
    private LocalParticipant localParticipant;
    private static final int RC_VIDEO_APP_PERM = 124;
    private ImageButton disconnect;
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
    private TwilioRestClient client = new TwilioRestClient.Builder("lingua", "lingua")
            .accountSid("AC7fe7368cc5a508f2c044c3eb1df3a52b")
            .build();




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
        roomName = chatId;
        receiverId = getIntent().getStringExtra("otherUser");
        videoChatLanguage = getIntent().getStringExtra("language");

        // setting up Firebase to receive the messages to be sent
        Firebase.setAndroidContext(VideoChatActivity.this);
        reference = new Firebase("https://lingua-project.firebaseio.com/messages/" + chatId);

        localVideoView = (VideoView) findViewById(R.id.activity_video_chat_publisher_container);
        remoteVideoView = (VideoView) findViewById(R.id.activity_video_chat_subscriber_container);
        disconnect = (ImageButton) findViewById(R.id.activity_video_chat_end_call);

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                room.disconnect();
                // launch intent to return to go to the ChatDetailsActivity
                Intent intent = new Intent(VideoChatActivity.this, ChatDetailsActivity.class);
                startActivity(intent);
            }
        });
        // receive an intent with the conversation object with the 2 users for this call,


        // generate the Twilio room and token with the given chat name and the current user as the first identity
        tokenGenerator = new VideoTokenGenerator(userId, roomName);
        if (tokenGenerator.token == null) {
            Log.d(TAG, "Token object not generated");
        } else {
            Log.d(TAG, "Token generated");
        }

        Log.i(TAG, "Inflated the layout for video activity");
        requestPermissions();
    }

    public void connectToRoom(String roomName, VideoTokenGenerator tokenGenerator) {

        //Check if H.264 is supported in this device
        boolean isH264Supported = MediaCodecVideoDecoder.isH264HwSupported() &&
                MediaCodecVideoEncoder.isH264HwSupported();

        // Prefer H264 if it is hardware available for encoding and decoding
        videoCodec = isH264Supported ? (new H264Codec()) : (new Vp9Codec());

        // try to see if the room already exists

        ConnectOptions connectOptions = new ConnectOptions.Builder(tokenGenerator.JwtToken)
                .roomName(roomName)
                .audioTracks(Collections.singletonList(localAudioTrack))
                .videoTracks(Collections.singletonList(localVideoTrack))
                .preferVideoCodecs(Collections.singletonList(videoCodec))
                .enableAutomaticSubscription(true)
                .build();


        com.twilio.rest.video.v1.Room room = new RoomFetcher(chatId).fetch(client);

        Room room = Video.connect(this, connectOptions, roomListener());

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
            connectToRoom(roomName, tokenGenerator); // and generate the token and the room
        } else {
            // prompt to ask for mic and camera permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }


    public void getVideoAndAudioTracks() {
        // Create an audio track
        localAudioTrack = LocalAudioTrack.create(this, true);

        // A video track requires an implementation of VideoCapturer - could include the back camera
        cameraCapturer = new CameraCapturer(this,
                CameraCapturer.CameraSource.FRONT_CAMERA);

        // Create a video track using the device's front camera
        localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);

        // getting the publisher container (for the local participant) and setting visibility to gone before the other participant enters the chat)
        localVideoView.setVisibility(View.GONE);

        // Rendering a local video track requires an implementation of VideoRenderer
        // Render a local video track to preview your camera
        localVideoTrack.addRenderer(remoteVideoView);
        // show the camera output from the user in the main screen
        remoteVideoView.setMirror(true);
    }

    public void moveLocalVideoToSmallView() {
        // moves the local participant's local video track from the main view to make space for the remote participant
        if (localVideoView.getVisibility() == View.GONE) {
            localVideoView.setVisibility(View.VISIBLE);
            if (localVideoTrack != null) {
                localVideoTrack.removeRenderer(remoteVideoView);
                localVideoTrack.addRenderer(localVideoView);
            }
            localVideoView.setMirror(true);
        }
        remoteVideoView.setMirror(false);
    }


    public void moveLocalVideoToMainView() {
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

    public void sendTextChat(String messageText) {
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
                // get the local participant and publish their tracks
                if (localVideoTrack != null && localAudioTrack != null) {
                    room.getLocalParticipant().publishTrack(localAudioTrack);
                    room.getLocalParticipant().publishTrack(localVideoTrack);
                }

                Log.i(TAG, "Connected to " + room.getName());


                // send a message to the other user to notify them of the creation of the room
                sendTextChat("Video chat with me!");

                // check if the user already has a participant - then fix the rendering of the video tracks
                if (room.getRemoteParticipants().size() > 0) {
                    // render the local participant's video into the publisher container - the smaller video view in the corner of the screen
                    moveLocalVideoToSmallView();

                    // for now, only allows for 2 people in the room and allows for them to alter their views from the main screen to the one toggled to the bottom left
                    RemoteParticipant remoteParticipant = room.getRemoteParticipants().get(0);
                    RemoteVideoTrackPublication remoteVideoTrackPublication = remoteParticipant.getRemoteVideoTracks().get(0);
                    Log.d(TAG, String.valueOf(remoteVideoTrackPublication.isTrackSubscribed()));
                    Log.d(TAG, String.format("Number of remote video tracks: %s", room.getRemoteParticipants().get(0).getRemoteVideoTracks().size()));
                    // only render tracks to which the local participant is subscribed
                    Toast.makeText(VideoChatActivity.this, String.format("Remote participants: %s", room.getRemoteParticipants().size()), Toast.LENGTH_SHORT).show();
                    if (remoteVideoTrackPublication.isTrackSubscribed()) {
                        Toast.makeText(VideoChatActivity.this, "Track subscribed", Toast.LENGTH_LONG).show();
                        RemoteVideoTrack remoteVideoTrack = remoteVideoTrackPublication.getRemoteVideoTrack();
                        remoteVideoTrack.addRenderer(remoteVideoView); // renders the added participant's video track to the main screen
                    }
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
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                Log.i(TAG, "reconnected");
            }

            @Override
            public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
                if (localVideoTrack != null) {
                    localParticipant.unpublishTrack(localVideoTrack);
                    localParticipant.unpublishTrack(localAudioTrack);
                }
                Log.i(TAG, "disconnected");
                // These tracks are released to ensure that memory allocated to them is freed
                localAudioTrack.release();
                localVideoTrack.release();
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.i(TAG, "new participant connected" + remoteParticipant.getIdentity());
                // In the case that the connected callback is received, the LocalParticipant and the RemoteParticipant objects become available
                localParticipant = room.getLocalParticipant();
                remoteParticipants = room.getRemoteParticipants();
                // use getIdentity to receive the identity strings associated with each user
                // to set a listener for the new participant that has been added to the room
                remoteParticipant.setListener(new RemoteParticipant.Listener() {
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

                    }

                    @Override
                    public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                        remoteVideoTrackPublication.getRemoteVideoTrack().removeRenderer(remoteVideoView);
                    }

                    @Override
                    public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                        // render the local participant's video into the publisher container - the smaller video view in the corner of the screen
                        moveLocalVideoToSmallView();
                        remoteVideoTrack.addRenderer(remoteVideoView); // renders the added participant's video track to the main screen
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
                });
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.i(TAG, "participant disconnected" + remoteParticipant.getIdentity());
                // move the local participant to the main view
                moveLocalVideoToMainView();
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

}
