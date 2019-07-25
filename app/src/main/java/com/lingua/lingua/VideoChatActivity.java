package com.lingua.lingua;

import android.Manifest;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import org.webrtc.MediaCodecVideoDecoder;
import org.webrtc.MediaCodecVideoEncoder;

import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class VideoChatActivity extends AppCompatActivity {

    private VideoTokenGenerator tokenGenerator;
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
    private String accessToken;
    private VideoCodec videoCodec;



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

        Log.i(TAG, "Inflated the layout for video activity");
        requestPermissions();
    }

    public void connectToRoom(String roomName) {

        //Check if H.264 is supported in this device
        boolean isH264Supported = MediaCodecVideoDecoder.isH264HwSupported() &&
                MediaCodecVideoEncoder.isH264HwSupported();

        // Prefer H264 if it is hardware available for encoding and decoding
        videoCodec = isH264Supported ? (new H264Codec()) : (new Vp9Codec());
        Log.i(TAG, "The video codec has been set");

        ConnectOptions connectOptions = new ConnectOptions.Builder(tokenGenerator.token.toString())
                .roomName(roomName)
                .audioTracks(Collections.singletonList(localAudioTrack))
                .videoTracks(Collections.singletonList(localVideoTrack))
                .preferVideoCodecs(Collections.singletonList(videoCodec))
                .build();
        Log.i(TAG, "Connection options generated");
        room = Video.connect(this, connectOptions, new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                Log.i(TAG, "Connected to " + room.getName());
                // check if the user already has a participant - then fix the rendering of the video tracks
                if (room.getRemoteParticipants().size() > 0) {
                    // render the local participant's video into the publisher container - the smaller video view in the corner of the screen
                    moveLocalVideoToSmallView();

                    remoteVideoView.setMirror(false);
                    // for now, only allows for 2 people in the room and allows for them to alter their views from the main screen to the one toggled to the bottom left
                    RemoteVideoTrackPublication remoteVideoTrackPublication = room.getRemoteParticipants().get(0).getRemoteVideoTracks().get(0);
                    // only render tracks to which the local participant is subscribed
                    if (remoteVideoTrackPublication.isTrackSubscribed()) {
                        remoteVideoTrackPublication.getRemoteVideoTrack().addRenderer(remoteVideoView); // renders the added participant's video track to the main screen
                    }
                }
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

                    }

                    @Override
                    public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                        // render the local participant's video into the publisher container - the smaller video view in the corner of the screen
                        moveLocalVideoToSmallView();

                        remoteVideoView.setMirror(false);
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
        Log.i(TAG, "Requesting permissions");
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        Log.i(TAG, "Gathered permissions");
        if (EasyPermissions.hasPermissions(this, perms)) {
            // after permission is granted, initialise the video and audio tracks
            Log.i(TAG, "Permission granted");
            getVideoAndAudioTracks();
            connectToRoom("Test"); // and generate the token and the room
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
    }

}
