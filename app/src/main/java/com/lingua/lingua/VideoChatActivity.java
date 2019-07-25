package com.lingua.lingua;

import android.os.Bundle;
import android.util.Log;

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


public class VideoChatActivity extends AppCompatActivity {

    VideoTokenGenerator tokenGenerator;
    Room room;
    LocalAudioTrack localAudioTrack;
    CameraCapturer cameraCapturer;
    VideoView remoteVideoView;
    List<RemoteParticipant> remoteParticipants;
    LocalParticipant localParticipant;


    private final static String TAG = "VideoChatActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        // pass in an intent with the conversation object with the 2 users for this call,


        // generate the Twilio room and token
        tokenGenerator = new VideoTokenGenerator();
        if (tokenGenerator.token == null) {
            Log.d(TAG, "Token object not generated");
        } else {
            Log.d(TAG, "Token generated");
        }
        connectToRoom("User 1 & 2");
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


    public void getVideoAndAudioTracks() {
        // Create an audio track
        localAudioTrack = LocalAudioTrack.create(this, true);

        // A video track requires an implementation of VideoCapturer
        cameraCapturer = new CameraCapturer(this,
                CameraCapturer.CameraSource.FRONT_CAMERA);

        // Create a video track
        LocalVideoTrack localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);

        // Rendering a local video track requires an implementation of VideoRenderer
        videoView = (VideoView) findViewById(R.id.activity_video_call_publisher_container);

        // Render a local video track to preview your camera
        localVideoTrack.addRenderer(videoView);

        // Release the audio track to free native memory resources
        localAudioTrack.release();

        // Release the video track to free native memory resources
        localVideoTrack.release();
    }
}
