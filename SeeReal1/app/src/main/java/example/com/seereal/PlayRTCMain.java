package example.com.seereal;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTCFactory;
import com.sktelecom.playrtc.config.PlayRTCAudioConfig.AudioCodec;
import com.sktelecom.playrtc.config.PlayRTCConfig;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig.CameraType;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig.VideoCodec;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.observer.PlayRTCObserver;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import org.json.JSONObject;

import java.io.File;

//playrtc v2.2.0
//playrtc v2.2.0

public class PlayRTCMain extends AppCompatActivity {

    private Context mContext;
    private String MY_PROJECT_ID = "60ba608a-e228-4530-8711-fa38004719c1344";

    private Toolbar toolbar;


    public PlayRTCObserver playrtcObserver;
    private static PlayRTC playrtc;

    public boolean isCloseActivity = false;
    public boolean isChannelConnected = false;
    public PlayRTCVideoView localView = null;
    public PlayRTCVideoView remoteView = null;
    public PlayRTCMedia localMedia;
    public PlayRTCMedia remoteMedia;
    public static String channelId;

    private RelativeLayout videoViewGroup;

   /* public PlayRTCMain() {
        start();
    }
*/

   public PlayRTCMain(Context context){
       this.mContext= context;
   }


  /*  //권한설정용
    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
*/


  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
       //
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.video_call_main);
        Log.d("PlayRTC","RTC Main 실행");
        //풀 스크린


        //권한설정
        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            checkPermission(MANDATORY_PERMISSIONS);
        }
        //

        createPlayRTCObserverInstance();

        createPlayRTCInstance();

       // setToolbar();
      //  setFragmentNavigationDrawer();
      //  setOnClickEventListenerToButton();
        createChannel();
        connectChannel();


    }


    //권한설정
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    @SuppressLint("NewApi")
    private void checkPermission(String[] permissions) {

        requestPermissions(permissions, MY_PERMISSION_REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                int cnt = permissions.length;
                for(int i = 0; i < cnt; i++ ) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {

                        // Log.i(LOG_TAG, "Permission[" + permissions[i] + "] = PERMISSION_GRANTED");

                    } else {

                        // Log.i(LOG_TAG, "permission[" + permissions[i] + "] always deny");
                    }
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        // Make the videoView at the onWindowFocusChanged time.
        if (hasFocus && this.localView == null) {
            createVideoView();
        }
    }

    @Override
    protected void onDestroy() {

        // instance release
        if(FriendFragment.playrtc != null) {
            // If you does not call playrtc.close(), playrtc instence is remaining every new call.
            // playrtc instence can not used again
            FriendFragment.playrtc.close();
            FriendFragment.playrtc = null;
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
    }*/

  /*  void start() {
        createPlayRTCObserverInstance();
        createPlayRTCInstance();
        createChannel();
        connectChannel();
    }*/

    public void createPlayRTCObserverInstance() {
        playrtcObserver = new PlayRTCObserver() {

            @Override
            public void onConnectChannel(final PlayRTC obj, final String channelId, final String channelCreateReason, final String channelType) {
                super.onConnectChannel(obj, channelId, channelCreateReason, channelType);
                isChannelConnected = true;

                // Fill the channelId to the channel_id TextView.
                //TextView channelIdTextView = (TextView) findViewById(R.id.channel_id);
                //channelIdTextView.setText(channelId);
            }

            @Override
            public void onAddLocalStream(final PlayRTC obj, final PlayRTCMedia playRTCMedia) {
                super.onAddLocalStream(obj, playRTCMedia);
                localMedia = playRTCMedia;

                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(localView.getVideoRenderer());
            }

            @Override
            public void onAddRemoteStream(final PlayRTC obj, final String peerId, final String peerUserId, final PlayRTCMedia playRTCMedia) {
                super.onAddRemoteStream(obj, peerId, peerUserId, playRTCMedia);
                remoteMedia = playRTCMedia;

                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());

            }

            @Override
            public void onDisconnectChannel(final PlayRTC obj, final String disconnectReason) {
                super.onDisconnectChannel(obj, disconnectReason);
                isChannelConnected = false;

                // v2.2.5
                localView.bgClearColor();
                remoteView.bgClearColor();

                // Clean the channel_id TextView.
                //TextView ChannelIdTextView = (TextView) findViewById(R.id.channel_id);
                // ChannelIdTextView.setText(null);

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

    public void createPlayRTCInstance() {
        try {
            PlayRTCConfig config = createPlayRTCConfig();
            playrtc = PlayRTCFactory.createPlayRTC(config, playrtcObserver);
        } catch (UnsupportedPlatformVersionException e) {
            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            e.printStackTrace();
        }
    }

    public PlayRTCConfig createPlayRTCConfig() {

        PlayRTCConfig config = PlayRTCFactory.createConfig();

        config.setAndroidContext(mContext);
        config.setProjectId(MY_PROJECT_ID);
        config.video.setEnable(true);
        /*
         * enum CameraType
         * - Front
         * - Back
         */
        config.video.setCameraType(CameraType.Back);


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


    public void createVideoView() {

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

    public void createLocalVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (localView == null) {
            // Create the video size variable.
            Point myVideoSize = new Point();
            myVideoSize.x = (int) (parentViewDimensions.x * 0.3);
            myVideoSize.y = (int) (parentViewDimensions.y * 0.3);


            //For test
            /*TextView textView = new TextView(parentVideoViewGroup.getContext());
            textView.setText("LOCAL");
            setContentView(textView);*/

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

     /*       Button mButton = new Button(getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myVideoSize.x,myVideoSize.y);
            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0,0,0,50);
            mButton.setText("EXIT");
            mButton.setBackgroundResource(R.drawable.rounded_button);
            mButton.setLayoutParams(params);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createCloseAlertDialog();
                    closeAlertDialog.show();
                }
            });
            parentVideoViewGroup.addView(mButton);*/

        }
    }

    public void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
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




   /* private void createCloseAlertDialog() {
        // Create the Alert Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set a Alert.
        alertDialogBuilder.setTitle(R.string.alert_title);
        alertDialogBuilder.setMessage(R.string.alert_message);
        alertDialogBuilder.setPositiveButton(R.string.alert_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
               // Intent intent = new Intent(PlayRTCMain.this,MainActivity.class);
               // startActivity(intent);
                if (isChannelConnected == true) {
                    isCloseActivity = false;

                    // null means my user id.
                    FriendFragment.playrtc.disconnectChannel(null);
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
*/

    void createChannel() {
        try {

           playrtc.createChannel(new JSONObject());
        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    void connectChannel() {
        try {
            playrtc.connectChannel(channelId, new JSONObject());
        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    public static PlayRTC getPlayRTC(){
        return playrtc;
    }

}
