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
    public  PlayRTCVideoView localView = null;
    public  PlayRTCVideoView remoteView = null;
    public  PlayRTCMedia localMedia=null;
    public  PlayRTCMedia remoteMedia=null;

    public boolean isCloseActivity = false;
    public boolean isChannelConnected = false;
    public static String channelId;

    private RelativeLayout videoViewGroup;

   /* public PlayRTCMain() {
        start();
    }
*/

   public PlayRTCMain(){
   }

   public PlayRTCMain(Context context,PlayRTCMedia localMedia, PlayRTCMedia remoteMedia, PlayRTCVideoView localView, PlayRTCVideoView remoteView){
       this.mContext= context;
       this.localMedia= localMedia;
       this.remoteMedia=remoteMedia;
       this.localView=localView;
       this.remoteView=remoteView;
   }


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
            createRemoteVideoView(myViewDimensions, myVideoViewGroup,remoteView);
        }

        if (localView == null) {
            createLocalVideoView(myViewDimensions, myVideoViewGroup,localView);
        }

    }

    public static void createLocalVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup,PlayRTCVideoView localView) {
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

    public static void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup, PlayRTCVideoView remoteView) {
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
}
