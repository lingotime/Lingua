package com.lingua.lingua;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.opentok.MediaMode;
import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.SessionProperties;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.exception.OpenTokException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoCallActivity extends AppCompatActivity implements com.opentok.android.Session.SessionListener, PublisherKit.PublisherListener{

    int apiKey = 46389502; // YOUR API
    String apiSecret = String.valueOf(R.string.tokbox_secret);
    OpenTok opentok = new OpenTok(apiKey, apiSecret);
    private static final String LOG_TAG = VideoCallActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private com.opentok.android.Session mSession; // defined in the OpenTok SDK, represents a session and includes methods for interacting with the session
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private ImageButton endCall;
    String sessionId;
    String token;

    // remember to close the OpenTok object with opentok.close()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        // A session that uses the OpenTok Media Router:
        try {
            Session session = opentok.createSession(new SessionProperties.Builder()
                    .mediaMode(MediaMode.ROUTED)
                    .build());
            // Store this sessionId in the database for later use:
            sessionId = session.getSessionId();
            // Generate a token by calling the method on the Session (returned from createSession)
            token = session.generateToken();
            // now both user need to be added to the session
            requestPermissions();
            opentok.close();
        } catch (OpenTokException e) {
            e.printStackTrace();
        }
    }

    // boilerplate code to use EasyPermissions library which will allow app to access the user's camera and video.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout)findViewById(R.id.activity_video_call_publisher_container);
            mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
            endCall = (ImageButton) findViewById(R.id.activity_video_call_end_call);

            // initialize and connect to the session
            mSession = new com.opentok.android.Session.Builder(this, String.valueOf(apiKey), sessionId).build(); // instantiates a Session object and takes the parameters: context, session ID and token
            // Session.Builder.build() returns a new session
            mSession.setSessionListener(this); // sets the object that will implement the SessionListener interface which includes callback methods that are called in response to Session events
            mSession.setConnectionListener(new com.opentok.android.Session.ConnectionListener() {
                @Override
                public void onConnectionCreated(com.opentok.android.Session session, Connection connection) {
                    // new client to the session
                    // TODO: find the user's name and create a prompt saying that this person has entered the chat
                    Toast.makeText(VideoCallActivity.this, "Someone entered the call", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnectionDestroyed(com.opentok.android.Session session, Connection connection) {
                    Toast.makeText(VideoCallActivity.this, "Someone leaved the call", Toast.LENGTH_SHORT).show();
                }
            });
            mSession.connect(token); // connects the client app to the OpenTok session

            endCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // disconnect this user from the call
                    mSession.disconnect();
                }
            });

        } else {
            EasyPermissions.requestPermissions(this, "Lingua needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    // SessionListener methods

    @Override
    public void onConnected(com.opentok.android.Session session) {
        // called when the client connects to the OpenTok session
        Log.i(LOG_TAG, "Session Connected");

        // TODO: Create the prompt here language learning -> select which language will be spoken

        // in order to publish an audio-video stream to the session after connection
        mPublisher = new Publisher.Builder(this).name("Users 1 & 2").build(); // instantiates a Publisher object
        mPublisher.setPublisherListener(this); // sets the PublicListener interface that contains callback methods in response to publisher-related events

        mPublisherViewContainer.addView(mPublisher.getView()); // adds the view captured by the device's camera as a subview of the mPublisherViewContainer
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(com.opentok.android.Session session) {
        Log.i(LOG_TAG, "Session Disconnected");

        // TODO: Record the length of the call for the user trying to learn the spoken language
        // TODO: Create a pop-up telling them how long they've spoken for
    }

    @Override
    public void onStreamReceived(com.opentok.android.Session session, Stream stream) {
        // called when another client publishes a stream to the Session
        Log.i(LOG_TAG, "Stream Received");

        // to allow the client to subscribe to (view) a stream published by another client
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber); // subscribes to the stream that was just received
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(com.opentok.android.Session session, Stream stream) {
        // called when another client stops publishing a stream to the Session
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews(); // removes a subscriber's view once the stream has dropped
        }
    }

    @Override
    public void onError(com.opentok.android.Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    // PublisherListener methods

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }


}
