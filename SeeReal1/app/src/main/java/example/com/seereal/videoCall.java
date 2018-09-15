package example.com.seereal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import static example.com.seereal.PlayRTCMain.getPlayRTC;

public class videoCall extends AppCompatActivity {


    private Context mContext;
    private PlayRTC playRTC;
    private PlayRTCMain playRTCMain;


    private PlayRTCVideoView localView = null;
    private PlayRTCVideoView remoteView = null;
    private PlayRTCMedia localMedia;
    private PlayRTCMedia remoteMedia;


    //public static PlayRTCMain playRTCMain;
    private AlertDialog closeAlertDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.video_call_main);
        Log.d("PlayRTC", "RTC Main 실행");
        //풀 스크린

        //playRTCMain = new PlayRTCMain();

        //권한설정
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPermission(MANDATORY_PERMISSIONS);
        }
        //

      /*  playRTCMain.createPlayRTCObserverInstance();

        playRTCMain.createPlayRTCInstance();

        // setToolbar();
        //  setFragmentNavigationDrawer();
        //  setOnClickEventListenerToButton();
        playRTCMain.createChannel();
        playRTCMain.connectChannel();*/

        playRTCMain = new PlayRTCMain();
        playRTC = getPlayRTC();

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
                for (int i = 0; i < cnt; i++) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        // Log.i(LOG_TAG, "Permission[" + permissions[i] + "] = PERMISSION_GRANTED");

                    } else {

                        // Log.i(LOG_TAG, "permission[" + permissions[i] + "] always deny");
                    }
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Make the videoView at the onWindowFocusChanged time.
        if (hasFocus && localView == null) {
            createVideoView();
        }
    }

    @Override
    protected void onDestroy() {

        // instance release
        if (playRTC != null) {
            // If you does not call playrtc.close(), playrtc instence is remaining every new call.
            // playrtc instence can not used again
            playRTC.close();
            playRTC = null;
        }

        // new v2.2.6
        if (localView != null) {
            localView.release();
        }
        // new v2.2.6
        if (remoteView != null) {
            remoteView.release();
        }

        FriendFragment.playRTCMain.playrtcObserver = null;
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (FriendFragment.playRTCMain.isCloseActivity) {
            super.onBackPressed();
        } else {
            createCloseAlertDialog();
            closeAlertDialog.show();
        }
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
            PlayRTCMain.createRemoteVideoView(myViewDimensions, myVideoViewGroup,remoteView);
        }

        if (localView == null) {
           PlayRTCMain.createLocalVideoView(myViewDimensions, myVideoViewGroup,localView);
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
                // Intent intent = new Intent(PlayRTCMain.this,MainActivity.class);
                // startActivity(intent);
                if (FriendFragment.playRTCMain.isChannelConnected == true) {
                    FriendFragment.playRTCMain.isCloseActivity = false;

                    // null means my user id.
                    playRTC.disconnectChannel(null);
                } else {
                    FriendFragment.playRTCMain.isCloseActivity = true;
                    onBackPressed();
                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
                FriendFragment.playRTCMain.isCloseActivity = false;
            }
        });

        // Create the Alert.
        closeAlertDialog = alertDialogBuilder.create();
    }



}
