package example.com.seereal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTCFactory;
import com.sktelecom.playrtc.config.PlayRTCAudioConfig.AudioCodec;
import com.sktelecom.playrtc.config.PlayRTCConfig;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig.VideoCodec;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.observer.PlayRTCObserver;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//playrtc v2.2.0
//playrtc v2.2.0

public class PlayRTCMain extends AppCompatActivity {

    private Context mContext;
    private String MY_PROJECT_ID = "60ba608a-e228-4530-8711-fa38004719c1";

    private UserModel destinationUserModel;
    private AlertDialog closeAlertDialog;

    private PlayRTCObserver playrtcObserver;
    private PlayRTC playrtc;

    private boolean isCloseActivity = false;
    private boolean isChannelConnected = false;
    private PlayRTCVideoView localView = null;
    private PlayRTCVideoView remoteView = null;
    private PlayRTCMedia localMedia;
    private PlayRTCMedia remoteMedia;
    private String channelId;
    private String receivedId;

   // public static boolean isReceived = false;
    private RelativeLayout videoViewGroup;

    private String pushToken;
    private String name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_call_main);
        Log.d("PlayRTC", "RTC Main 실행");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pushToken = bundle.getString("token");
        name = bundle.getString("name");
        receivedId = bundle.getString("id");
        Log.d("JJ", "받은 채널 " + receivedId);


        destinationUserModel = new UserModel();
        destinationUserModel.pushToken = pushToken;
        destinationUserModel.userName = name;

        createPlayRTCObserverInstance();
        createPlayRTCInstance();
        if (ReceivedSingleton.getInstance().instanceOf(true))
            createChannel();
        else
            connectChannel(receivedId);

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Make the videoView at the onWindowFocusChanged time.
        if (hasFocus && this.localView == null) {
            createVideoView();
        }
    }

    @Override
    protected void onDestroy() {

        // instance release
        if (playrtc != null) {
            // If you does not call playrtc.close(), playrtc instence is remaining every new call.
            // playrtc instence can not used again
            playrtc.close();
            playrtc = null;
        }

        // new v2.2.6
        if (localView != null) {
            localView.release();
        }
        // new v2.2.6
        if (remoteView != null) {
            remoteView.release();
        }

        playrtcObserver = null;
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (isCloseActivity) {
            super.onBackPressed();
        } else {
            createCloseAlertDialog();
            closeAlertDialog.show();
        }
    }

    private void createPlayRTCObserverInstance() {

        playrtcObserver = new PlayRTCObserver() {

            @Override
            public void onConnectChannel(final PlayRTC obj, final String channelId, final String channelCreateReason, final String channelType) {
                super.onConnectChannel(obj, channelId, channelCreateReason, channelType);
                isChannelConnected = true;
                Log.d("JJ", "옵저버인스턴스에서 채널 아이디 " + channelId);
                sendFCM();

                // Fill the channelId to the channel_id TextView.


            }

            @Override
            public void onAddLocalStream(final PlayRTC obj, final PlayRTCMedia playRTCMedia) {
                super.onAddLocalStream(obj, playRTCMedia);
                localMedia = playRTCMedia;

                long delayTime = 0;

                localView.show(delayTime);
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(localView.getVideoRenderer());
            }

            @Override
            public void onAddRemoteStream(final PlayRTC obj, final String peerId, final String peerUserId, final PlayRTCMedia playRTCMedia) {
                super.onAddRemoteStream(obj, peerId, peerUserId, playRTCMedia);
                remoteMedia = playRTCMedia;

                long delayTime = 0;

                remoteView.show(delayTime);
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());

            }

            @Override
            public void onDisconnectChannel(final PlayRTC obj, final String disconnectReason) {
                super.onDisconnectChannel(obj, disconnectReason);
                isChannelConnected = false;
                ReceivedSingleton.getInstance().reset();

                // v2.2.5
                localView.bgClearColor();
                remoteView.bgClearColor();

                // Create PlayRTCMain instance again.
                // Because at the disconnect moment, the PlayRTCMain instance has removed.
                 createPlayRTCInstance();
            }

//            @Override
//            public void onOtherDisconnectChannel(final PlayRTCMain obj, final String peerId, final String peerUserId) {
//
//                // v2.2.5
//                remoteView.bgClearColor();
//
//
//            }
        };
    }

    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig config = createPlayRTCConfig();
            playrtc = PlayRTCFactory.createPlayRTC(config, playrtcObserver);
        } catch (UnsupportedPlatformVersionException e) {
            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
    }

    private PlayRTCConfig createPlayRTCConfig() {

        PlayRTCConfig config = PlayRTCFactory.createConfig();

        config.setAndroidContext(getApplicationContext());
        config.setProjectId(MY_PROJECT_ID);
        config.video.setEnable(true);
        /*
         * enum CameraType
         * - Front
         * - Back
         */
        config.video.setCameraType(PlayRTCVideoConfig.CameraType.Front);

        /*
         * enum VideoCodec
         * - VP8
         * - VP9
         * - H264 : You can use the device must support.
         */
        config.video.setPreferCodec(VideoCodec.VP8);

        // default resolution 640x480
        config.video.setMaxFrameSize(640, 480);
        config.video.setMinFrameSize(640, 480);


        config.audio.setEnable(true);   /* send audio stream */
        /* use PlayRTCAudioManager */
        config.audio.setAudioManagerEnable(true);

        /*
         * enum AudioCodec
         * - ISAC
         * - OPUS
         */
        config.audio.setPreferCodec(AudioCodec.OPUS);

        config.data.setEnable(true);    /* use datachannel stream */

        // Console logging setting
        config.log.console.setLevel(PlayRTCConfig.DEBUG);

        // File logging setting
        File logPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/" + getPackageName() + "/files/log/");
        if (logPath.exists() == false) {
            logPath.mkdirs();
        }
        config.log.file.setLogPath(logPath.getAbsolutePath());
        config.log.file.setLevel(PlayRTCConfig.DEBUG);

        return config;
    }

    private void createVideoView() {

        RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);

        if (localView != null) {
            return;
        }

        Point myViewDimensions = new Point();
        myViewDimensions.x = myVideoViewGroup.getWidth();
        myViewDimensions.y = myVideoViewGroup.getHeight();

        if (remoteView == null) {
            createRemoteVideoView(myViewDimensions, myVideoViewGroup);
        }

        if (localView == null) {
            createLocalVideoView(myViewDimensions, myVideoViewGroup);
        }
    }

    private void createLocalVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (localView == null) {
            // Create the video size variable.
            Point myVideoSize = new Point();
            myVideoSize.x = (int) (parentViewDimensions.x * 0.3);
            myVideoSize.y = (int) (parentViewDimensions.y * 0.3);


            // Create the view parameter.
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myVideoSize.x, myVideoSize.y);
            param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            param.setMargins(30, 30, 30, 30);

            // Create the localViews.
            // new v2.2.6
            localView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            // Set the z-order.
            localView.setZOrderMediaOverlay(true);
            // Background color
            // v2.2.5
            localView.setBgClearColor(225, 225, 225, 255);
            // Set the layout parameters.
            localView.setLayoutParams(param);

            // new v2.2.6
            localView.initRenderer();

            // Add the view to the parentVideoViewGrop.
            parentVideoViewGroup.addView(localView);
        }
    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (remoteView == null) {
            // Create the video size variable.
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;

            // Create the view parameters.
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            // Create the remoteView.
            // new v2.2.6
            remoteView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            // Background color
            // v2.2.5
            remoteView.setBgClearColor(200, 200, 200, 255);
            // Set the layout parameters.
            remoteView.setLayoutParams(param);

            // new v2.2.6
            remoteView.initRenderer();

            // Add the view to the videoViewGroup.
            parentVideoViewGroup.addView(remoteView);
        }
    }

    private void createCloseAlertDialog() {
        // Create the Alert Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set a Alert.
        alertDialogBuilder.setTitle(R.string.alert_title);
        alertDialogBuilder.setMessage(R.string.alert_message);
        alertDialogBuilder.setPositiveButton(R.string.alert_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();


                if (isChannelConnected == true) {
                    isCloseActivity = false;

                    // null means my user id.
                    playrtc.disconnectChannel(null);

                } else {
                    isCloseActivity = true;
                    onBackPressed();
                }

            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
                isCloseActivity = false;
            }
        });

        // Create the Alert.
        closeAlertDialog = alertDialogBuilder.create();
    }

    private void createChannel() {
        try {
            playrtc.createChannel(new JSONObject());
            ReceivedSingleton.getInstance().reset();

        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    private void connectChannel(String channel) {
        try {
            playrtc.connectChannel(channel, new JSONObject());

        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    void sendFCM() {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        //notificationModel.notification.title=destinationUserModel.userName+" requested video call to you.";
        //notificationModel.notification.text="Please help me";
        notificationModel.data.title = InitApp.sUser.getDisplayName() + " requested video call to you";
        notificationModel.data.text = playrtc.getChannelId();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyA3Bv1IEgeHVEGLajuQ0c7uaPe9ERPMMaI")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue((new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        }));
    }
}