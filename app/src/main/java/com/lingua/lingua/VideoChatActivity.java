package com.lingua.lingua;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
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
import com.twilio.video.VideoView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class VideoChatActivity extends AppCompatActivity {

    private VideoTokenGenerator tokenGenerator;
    private Room room;
    private LocalAudioTrack localAudioTrack;
    private CameraCapturer cameraCapturer;
    private VideoView remoteVideoView;
    private VideoView localVideoView;
    private List<RemoteParticipant> remoteParticipants;
    private LocalParticipant localParticipant;
    private static final int RC_VIDEO_APP_PERM = 124;
    private ImageButton disconnect;


    private final static String TAG = "VideoChatActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        localVideoView = (VideoView) findViewById(R.id.activity_video_chat_publisher_container);
        remoteVideoView = (VideoView) findViewById(R.id.activity_video_chat_subscriber_container);
        disconnect = (ImageButton) findViewById(R.id.activity_video_chat_end_call);

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                room.disconnect();
            }
        });
        // receive an intent with the conversation object with the 2 users for this call,


        // generate the Twilio room and token
        tokenGenerator = new VideoTokenGenerator();
        if (tokenGenerator.token == null) {
            Log.d(TAG, "Token object not generated");
        } else {
            Log.d(TAG, "Token generated");
        }
        requestPermissions();
    }

    public void connectToRoom(String roomName) {
        ConnectOptions connectOptions = new ConnectOptions.Builder(tokenGenerator.token.toString())
                .roomName(roomName)
                .build();
        room = Video.connect(this, connectOptions, new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                Log.i(TAG, "Connected to " + room.getName());
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {
                Log.i(TAG, "failure to connect");
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
                Log.i(TAG, "disconnected");
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

                    }

                    @Override
                    public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                        // render the local participant's video into the publisher container
                        localVideoView.setVisibility(View.VISIBLE);
                        localVideoView.setMirror(true);
                        remoteVideoView.setMirror(false);
                        remoteVideoTrack.addRenderer(remoteVideoView); // renders the added participant's video track to the main screen
                    }

                    @Override
                    public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {

                    }

                    @Override
                    public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {

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
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {
                Log.i(TAG, "recording started");
            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {
                Log.i(TAG, "recording stopped");
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
            connectToRoom("User 1 & 2"); // and generate the token and the room
        } else {
            // prompt to ask for mic and camera permission
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }


    public void getVideoAndAudioTracks() {
        // Create an audio track
        localAudioTrack = LocalAudioTrack.create(this, true);

        // A video track requires an implementation of VideoCapturer
        cameraCapturer = new CameraCapturer(this,
                CameraCapturer.CameraSource.FRONT_CAMERA);

        // Create a video track
        LocalVideoTrack localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);

        // getting the publisher container (for the local participant) and setting visibility to gone before the other participant enters the chat)
        localVideoView.setVisibility(View.GONE);

        // Rendering a local video track requires an implementation of VideoRenderer
        // Render a local video track to preview your camera
        localVideoTrack.addRenderer(remoteVideoView);

        // Release the audio track to free native memory resources
        localAudioTrack.release();

        // Release the video track to free native memory resources
        localVideoTrack.release();
    }
}
