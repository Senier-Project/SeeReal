package example.com.seereal;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

//playrtc v2.2.0
//playrtc v2.2.0

public class PlayRTCMain extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
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
    private String receivedIP;
    private int PORT = 10001;
    private int PORT2 =10002;
    private String helpee_temp,helper_temp;
    private boolean colorChanged= false;
    private int color;
    private int changedColor;
    String helperMsg = "";
    boolean helperIsConnected = true;


    ServerSocket serversocket;

    Socket socket;
    DataInputStream is;
    DataOutputStream os;
    String helpeeMsg = "";
    char helpee_direction,helper_direction;
    boolean helpeeIsConnected = true;
    Vector<Float> x,y;

    ObjectInputStream ois;
    ObjectOutputStream oos;
    InputStream drawIs;
    OutputStream drawOs;
    ArrayList<Path> helpeePaths,helperPaths;

    // public static boolean isReceived = false;
    private RelativeLayout videoViewGroup;

    private String pushToken;
    private String name;

    private RelativeLayout toolBoxLayout;
    private ImageButton mClickObject;
    private ImageButton mRotateObject;
    private ImageButton mProhibitObject;
    private ImageButton mCheckObject;

    private Button sendBtn;
    private Button drawingSendBtn;
    private boolean isDrawInput = false;
    private TextView printMs;
    private EditText sendMs;
    RelativeLayout myVideoViewGroup;
    Utils util;
    DrawOnTop mDraw;


    LinearLayout objectLayout;


    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_call_main);
        Log.d("PlayRTC", "RTC Main 실행");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pushToken = bundle.getString("token");
        name = bundle.getString("name");
        receivedId = bundle.getString("id");
        receivedIP = bundle.getString("ip");
        Log.d("JJ", "받은 채널 " + receivedId);


        toolBoxLayout = (RelativeLayout) View.inflate(getApplicationContext(), R.layout.ar_toolbox, null);
        /*sendMs = toolBoxLayout.findViewById(R.id.sendMsg);
        sendBtn = toolBoxLayout.findViewById(R.id.socketButton);
        printMs = toolBoxLayout.findViewById(R.id.printMsg);*/



        destinationUserModel = new UserModel();
        destinationUserModel.pushToken = pushToken;
        destinationUserModel.userName = name;

        createPlayRTCObserverInstance();
        createPlayRTCInstance();
        if (ReceivedSingleton.getInstance().instanceOf(false)) {

            createChannel();
        } else {
            connectChannel(receivedId, receivedIP);

        }/*
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                switch (id) {
                    case R.id.item1:
                        Log.d("susu", "item1");
                        break;
                    case R.id.item2:
                        Log.d("susu", "item2");
                        break;
                    case R.id.item3:
                        Log.d("susu", "item3");
                        break;

                }
                return true;
            }
        });*/

        util = new Utils();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            //  ex.printStackTrace();
        }
        return null;
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
        //  android.os.Process.killProcess(android.os.Process.myPid());
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
                if (ReceivedSingleton.getInstance().instanceOf(true)) {
                    localView.show(delayTime);
                    // Link the media stream to the view.
                    playRTCMedia.setVideoRenderer(localView.getVideoRenderer());
                } else {
                    remoteView.show(delayTime);

                    playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());
                }





             /*   mClickObject.setOnTouchListener(mTouchListener);
                mRotateObject.setOnTouchListener(mTouchListener);
                mProhibitObject.setOnTouchListener(mTouchListener);
                mCheckObject.setOnTouchListener(mTouchListener);*/

                //toolBoxLayout.setOnDragListener(mDragListener);
            }

            View.OnTouchListener mTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ClipData clip = ClipData.newPlainText("", "");
                        view.startDrag(clip, new CanvasShadow(view, (int) motionEvent.getX(), (int) motionEvent.getY()), view, 0);
                        //   view.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    return false;
                }
            };
            View.OnDragListener mDragListener = new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent dragEvent) {
                    switch (dragEvent.getAction()) {

                        case DragEvent.ACTION_DRAG_STARTED:
                            return true;

                        case DragEvent.ACTION_DRAG_ENTERED:
                            return true;

                        case DragEvent.ACTION_DRAG_EXITED:
                            return true;

                        case DragEvent.ACTION_DROP:
                            View view = (View) dragEvent.getLocalState();
                            ViewGroup parent = (ViewGroup) view.getParent();
                            if (v == findViewById(R.id.obj_layout)) {
                                LinearLayout myVideoViewGroup = (LinearLayout) v;

                                parent.removeView(view);
                                myVideoViewGroup.bringToFront();
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                layoutParams.setMargins((int) dragEvent.getX(), (int) dragEvent.getY(), 0, 0);
                                Log.d("bbumjun2", String.valueOf((int) dragEvent.getX()) + ' ' + String.valueOf((int) dragEvent.getY()));
                                //  v.setVisibility(View.INVISIBLE);
                                myVideoViewGroup.addView(view, layoutParams);
                                view.setVisibility(View.VISIBLE);
                                Log.d("bbumjun1", String.valueOf(dragEvent.getX()) + ' ' + String.valueOf(dragEvent.getY()));
                            }
                            return true;

                        case DragEvent.ACTION_DRAG_ENDED:

                            if (dragEvent.getResult() == false) {//드래그 종료시 처음 숨겼던 뷰를 다시 보이도록 한다.
                                ((View) (dragEvent.getLocalState())).setVisibility(View.VISIBLE);

                            }
                            return true;
                    }
                    return false;
                }
            };

            @Override
            public void onAddRemoteStream(final PlayRTC obj, final String peerId, final String peerUserId, final PlayRTCMedia playRTCMedia) {
                super.onAddRemoteStream(obj, peerId, peerUserId, playRTCMedia);
                remoteMedia = playRTCMedia;

                long delayTime = 0;

                if (ReceivedSingleton.getInstance().instanceOf(true)) {

                    remoteView.show(delayTime);
                    // Link the media stream to the view.
                    playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());
                } else {
                    localView.show(delayTime);

                    playRTCMedia.setVideoRenderer(localView.getVideoRenderer());
                }
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
            //     e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            //         e.printStackTrace();
        } catch (AssertionError e) {
            //         e.printStackTrace();
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
        config.video.setCameraType(PlayRTCVideoConfig.CameraType.Back);

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

        myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);

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

    private void createLocalVideoView(final Point parentViewDimensions, final RelativeLayout parentVideoViewGroup) {
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

            toolBoxLayout.setVisibility(View.VISIBLE);
            toolBoxLayout.setBackgroundColor(Color.TRANSPARENT);

            addContentView(toolBoxLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));


            Button.OnClickListener mClickListener = new Button.OnClickListener(){
                @Override
                public void onClick(View view) {

                  colorChanged=true;
                  color=view.getId();
                         switch (view.getId()) {
                            case R.id.btn_red:
                                Log.d("jj", "red btn click");
                                mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#B71C1C"));
                                parentVideoViewGroup.addView(mDraw);
                                //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                break;
                            case R.id.btn_yellow:
                                Log.d("jj", "yellow btn click");
                                mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#FFEB3B"));
                                parentVideoViewGroup.addView(mDraw);
                                //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                break;
                            case R.id.btn_green:
                                Log.d("jj", "green btn click");
                                mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#388E3C"));
                                parentVideoViewGroup.addView(mDraw);
                                // mDraw.setColor(Color.parseColor("#388E3C"));
                                break;
                            case R.id.btn_blue:
                                Log.d("jj", "blue btn click");
                                mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#1E88E5"));
                                parentVideoViewGroup.addView(mDraw);
                                //mDraw.setColor(Color.parseColor("#1E88E5"));
                                break;
                            case R.id.btn_black:
                                Log.d("jj", "black btn click");
                                mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#000000"));
                                parentVideoViewGroup.addView(mDraw);
                                //mDraw.setColor(Color.parseColor("#000000"));
                                break;
                        }

                }
            };
            Button btn_red = toolBoxLayout.findViewById(R.id.btn_red);
            Button btn_yellow = toolBoxLayout.findViewById(R.id.btn_yellow);
            Button btn_green = toolBoxLayout.findViewById(R.id.btn_green);
            Button btn_blue = toolBoxLayout.findViewById(R.id.btn_blue);
            Button btn_black = toolBoxLayout.findViewById(R.id.btn_black);

            btn_red.setOnClickListener(mClickListener);
            btn_yellow.setOnClickListener(mClickListener);
            btn_green.setOnClickListener(mClickListener);
            btn_blue.setOnClickListener(mClickListener);
            btn_black.setOnClickListener(mClickListener);

            toolBoxLayout.bringToFront();

            Preview mPreview = new Preview(getApplicationContext(), myVideoSize.x, myVideoSize.y);

            Button btn_undo = util.create(getApplicationContext(), "Undo", Color.TRANSPARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_BOTTOM);
            Button btn_redo = util.create(getApplicationContext(), "Redo", Color.TRANSPARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_BOTTOM);
            btn_undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = true;
                    if (status == true) {
                        mDraw.onClickUndo();
                        status = false;
                    }
                }
            });

            //TO-DO

            btn_redo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = true;
                    if (status == true) {
                        mDraw.onClickRedo();
                        status = false;
                    }
                }
            });

            sendMs = util.createET(getApplicationContext(), 800, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_HORIZONTAL);
            sendBtn = util.create(getApplicationContext(), "Send", Color.TRANSPARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE,RelativeLayout.RIGHT_OF,sendMs.getId());
            printMs = util.createTV(getApplicationContext(), RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.CENTER_IN_PARENT);


            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //  String sendText = InitApp.sUser.getDisplayName() + " :  " + sendMs.getText().toString();
                    printMs.append("나 : "+sendMs.getText().toString() + "\n");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sendMs.getWindowToken(), 0);



                    if (os == null) return; //클라이언트와 연결되어 있지 않다면 전송불가..

                    //네트워크 작업이므로 Thread 생성

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //클라이언트로 보낼 메세지 EditText로 부터 얻어오기
                            String msg = sendMs.getText().toString();

                            try {
                                os.writeUTF("<text> "+msg); //클라이언트로 메세지 보내기.UTF 방식으로(한글 전송가능...)
                                os.flush();   //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드..
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                //          e.printStackTrace();
                            }
                        }
                    }).start(); //Thread 실행..
                }
            });

            mDraw= new DrawOnTop(getApplicationContext(),Color.parseColor("#B71C1C"));
            //   parentVideoViewGroup.addView(objectLayout);
            parentVideoViewGroup.addView(btn_redo);
            parentVideoViewGroup.addView(btn_undo);
            //parentVideoViewGroup.addView(mPreview);
            parentVideoViewGroup.addView(mDraw);
            parentVideoViewGroup.addView(printMs);
            parentVideoViewGroup.addView(sendBtn);
            parentVideoViewGroup.addView(sendMs);

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
            //    e.printStackTrace();
        }

        // 헬피(서버)쪽에서 그림을 받는 부분
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    //서버소켓 생성.
                    serversocket = new ServerSocket(PORT);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //      e.printStackTrace();
                }
                try {
                    //서버에 접속하는 클라이언트 소켓 얻어오기(클라이언트가 접속하면 클라이언트 소켓 리턴)
                    socket = serversocket.accept(); //서버는 클라이언트가 접속할 때까지 여기서 대기...
                    Log.d("bbumjun", " 헬피쪽 소켓 연결됨" + receivedIP);
                    //여기 까지 왔다는 것은 클라이언트가 접속했다는 것을 의미하므로
                    //클라이언트와 데이터를 주고 받기 위한 통로구축..
                    is = new DataInputStream(socket.getInputStream()); //클라이언트로 부터 메세지를 받기 위한 통로
                    os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //        e.printStackTrace();
                }
                //클라이언트가 접속을 끊을 때까지 무한반복하면서 클라이언트의 메세지 수신
                while (helpeeIsConnected) {
                    try {
                        helpee_temp=is.readUTF();
                        Log.d("bbumjun","helpee temp = "+helpee_temp);

                        if(helpee_temp.startsWith("<text> ")) {
                            helpeeMsg=helpee_temp.substring(7);
                        } else if(helpee_temp.startsWith("<draw>")){
                            x= new Vector<Float>();
                            y= new Vector<Float>();
                            String strX=is.readUTF();
                            String strY=is.readUTF();
                            String[] tempX=strX.split(" ");
                            String[] tempY=strY.split(" ");

                            for(int i=0;i<tempX.length;i++) {
                                x.add(Float.parseFloat(tempX[i]));
                                y.add(Float.parseFloat(tempY[i]));
                            }

                        }
                        else if(helpee_temp.startsWith("<color>")) {
                             changedColor = Integer.parseInt(is.readUTF());
                            Log.d("bbumjun","color value="+changedColor);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        //       e.printStackTrace();
                    }

                    //클라이언트로부터 읽어들인 메시지msg를 TextView에 출력..
                    //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
                    //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
                    //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TOO ADuto-generated method stub
                            if(helpee_temp.startsWith("<text>")) {

                                printMs.append("상대방 : " + helpeeMsg + "\n");
                            } else if(helpee_temp.startsWith("<draw>")) {
                                mDraw.draw2(x, y);
                            }
                                else if (helpee_temp.startsWith("<color>")) {
                                    switch (changedColor) {
                                        case R.id.btn_red:
                                            Log.d("jj", "red btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#B71C1C"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                            break;
                                        case R.id.btn_yellow:
                                            Log.d("jj", "yellow btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#FFEB3B"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                            break;
                                        case R.id.btn_green:
                                            Log.d("jj", "green btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#388E3C"));
                                            myVideoViewGroup.addView(mDraw);
                                            // mDraw.setColor(Color.parseColor("#388E3C"));
                                            break;
                                        case R.id.btn_blue:
                                            Log.d("jj", "blue btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#1E88E5"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#1E88E5"));
                                            break;
                                        case R.id.btn_black:
                                            Log.d("jj", "black btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#000000"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#000000"));
                                            break;
                                    }
                                }

                        }
                    });
                    /////////////////////////////////////////////////////////////////////////////
                }//while..
            }//run method...
        }).start(); //Thread 실행..

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(helpeeIsConnected) {
                    try {
                        if (mDraw.drawing == false) {
                            Log.d("bbumjun","헬피에서 좌표 보냄");
                            os.writeUTF("<draw>");
                            os.flush();
                            os.writeUTF(mDraw.getCoordX());
                            os.flush();
                            os.writeUTF(mDraw.getCoordY());
                            os.flush();
                            mDraw.drawing=true;
                        }
                        if(colorChanged==true) {
                            os.writeUTF("<color>");
                            os.writeUTF(String.valueOf(color));
                            colorChanged=false;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    private void connectChannel(String channel, String ip) {
        try {
            playrtc.connectChannel(channel, new JSONObject());
        } catch (RequiredConfigMissingException e) {
            //  e.printStackTrace();
        }
        final String helpeeIP = ip;
        //헬퍼(클라) 쪽에서 그림을 받는 부분

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method  stub

                try {
                    //   ip= edit_ip.getText().toString();//IP 주소가 작성되어 있는 EditText에서 서버 IP 얻어오기
                    //     Log.d("bbumjun",ip+" :ip");
                    //서버와 연결하는 소켓 생성..
                    socket = new Socket(InetAddress.getByName(helpeeIP), PORT);
                    Log.d("bbumjun", "헬퍼쪽 socket created" + helpeeIP);

                    //여기까지 왔다는 것을 예외가 발생하지 않았다는 것이므로 소켓 연결 성공..
                    //서버와 메세지를 주고받을 통로 구축
                    is = new DataInputStream(socket.getInputStream());
                    os = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    Log.d("bbumjun", "connect exception");


                    // TODO Auto-generated catch block
                    //         e.printStackTrace();
                }

                //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                while (true) {
                    try {
                        helper_temp = is.readUTF(); //서버 부터 메세지가 전송되면 이를 UTF형식으로 읽어서 String 으로 리턴
                        if(helper_temp!=null) {
                            Log.d("bbumjun","helper temp = "+helper_temp);
                        }
                        if(helper_temp.startsWith("<text> ")) {
                            helperMsg=helper_temp.substring(7);
                        } else if(helper_temp.startsWith("<draw>")){
                            x= new Vector<Float>();
                            y= new Vector<Float>();
                            String strX=is.readUTF();
                            String strY=is.readUTF();
                            String[] tempX=strX.split(" ");
                            String[] tempY=strY.split(" ");

                            for(int i=0;i<tempX.length;i++) {
                                x.add(Float.parseFloat(tempX[i]));
                                y.add(Float.parseFloat(tempY[i]));
                            }

                        } else if(helper_temp.startsWith("<color>")) {
                            changedColor = Integer.parseInt(is.readUTF());
                            Log.d("bbumjun","color value="+changedColor);

                        }
                        //서버로부터 읽어들인 메시지msg를 TextView에 출력..
                        //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
                        //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
                        //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if(helper_temp.startsWith("<text>")) {
                                    printMs.append("상대 : " + helperMsg + "\n");
                                } else if(helper_temp.startsWith("<draw>")) {

                                    mDraw.draw2(x, y);
                                }else if(helper_temp.startsWith("<color>")) {
                                    switch (changedColor) {
                                        case R.id.btn_red:
                                            Log.d("jj", "red btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#B71C1C"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                            break;
                                        case R.id.btn_yellow:
                                            Log.d("jj", "yellow btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#FFEB3B"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#FFEB3B"));
                                            break;
                                        case R.id.btn_green:
                                            Log.d("jj", "green btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#388E3C"));
                                            myVideoViewGroup.addView(mDraw);
                                            // mDraw.setColor(Color.parseColor("#388E3C"));
                                            break;
                                        case R.id.btn_blue:
                                            Log.d("jj", "blue btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#1E88E5"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#1E88E5"));
                                            break;
                                        case R.id.btn_black:
                                            Log.d("jj", "black btn send");
                                            mDraw = new DrawOnTop(getApplicationContext(), Color.parseColor("#000000"));
                                            myVideoViewGroup.addView(mDraw);
                                            //mDraw.setColor(Color.parseColor("#000000"));
                                            break;
                                    }

                                }
                            }
                        });
                        //////////////////////////////////////////////////////////////////////////
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        //      e.printStackTrace();
                    }
                }//while
            }//run method...
        }).start();//Thread 실행..

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(helperIsConnected) {
                    try {
                        if (mDraw.drawing == false) {
                            Log.d("bbumjun","헬퍼에서 좌표 보냄");
                            os.writeUTF("<draw>");
                            os.flush();
                            os.writeUTF(mDraw.getCoordX());
                            os.flush();
                            os.writeUTF(mDraw.getCoordY());
                            os.flush();
                            mDraw.drawing=true;
                        } else if(colorChanged==true) {
                            os.writeUTF("<color>");
                            os.writeUTF(String.valueOf(color));
                            colorChanged=false;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    void sendFCM() {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        //notificationModel.notification.title=destinationUserModel.userName+" requested video call to you.";
        //notificationModel.notification.text="Please help me";
        notificationModel.data.title = InitApp.sUser.getDisplayName() + " requested video call to you";
        notificationModel.data.text = playrtc.getChannelId();
        notificationModel.data.ip = getLocalIpAddress();
        Log.d("bbumjun", "넘기는 ip" + notificationModel.data.ip);
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