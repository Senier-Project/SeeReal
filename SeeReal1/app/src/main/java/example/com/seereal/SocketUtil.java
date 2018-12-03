/*
package example.com.seereal;

import android.app.Activity;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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


import static example.com.seereal.PlayRTCMain.sendBtn;

public class SocketUtil extends Activity {

    ServerSocket serversocket,serversocket2;

    Socket socket;
    ObjectInputStream is;
    ObjectOutputStream os;
    String helpeeMsg = "";
    boolean helpeeIsConnected = true;

    ObjectInputStream ois;
    ObjectOutputStream oos;
    InputStream drawIs;
    OutputStream drawOs;
    Path helpeePaths,helperPaths;
    private String receivedIP;
    private String receivedId;

    private int PORT = 10001;
    private int PORT2 =10002;

    Socket receiverSocket;    //클라이언트의 소켓
    DataInputStream receiver_is;
    DataOutputStream receiver_os;
    String helperMsg = "";
    boolean helperIsConnected = true;


    private EditText sendMs;
    private TextView printMs;
    private Button sendBtn;
    private Button drawSendBtn;
    private DrawOnTop mDraw;

    public SocketUtil(String receivedIp,String receivedId,EditText sendMs, TextView printMs, Button sendBtn, Button drawSendBtn,DrawOnTop draw){
        this.receivedIP = receivedIp;
        this.receivedId = receivedId;
        this.sendMs = sendMs;
        this.printMs=printMs;
        this.sendBtn= sendBtn;
        this.drawSendBtn = drawSendBtn;
        this.mDraw=draw;
    }

    public void CreateServerSocket() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    //서버소켓 생성.
                    serversocket = new ServerSocket(PORT);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    //서버에 접속하는 클라이언트 소켓 얻어오기(클라이언트가 접속하면 클라이언트 소켓 리턴)
                    socket = serversocket.accept(); //서버는 클라이언트가 접속할 때까지 여기서 대기...
                    //여기 까지 왔다는 것은 클라이언트가 접속했다는 것을 의미하므로
                    //클라이언트와 데이터를 주고 받기 위한 통로구축..

                    is = new ObjectInputStream(socket.getInputStream()); //클라이언트로 부터 메세지를 받기 위한 통로
                    os = new ObjectOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로

                } catch (IOException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //클라이언트가 접속을 끊을 때까지 무한반복하면서 클라이언트의 메세지 수신

                while (helpeeIsConnected) {
                    try {
                        helpeeMsg = (String)is.readObject();
                        Log.d("jj",helpeeMsg);
                    } catch (Exception e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //클라이언트로부터 읽어들인 메시지msg를 TextView에 출력..
                    //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
                    //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
                    //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            printMs.append("You" + "  " + helpeeMsg + "\n");
                        }
                    });
                    /////////////////////////////////////////////////////////////////////////////
                }//while..
            }//run method...
        }).start(); //Thread 실행..
    }

    public void CreateClientSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    //   ip= edit_ip.getText().toString();//IP 주소가 작성되어 있는 EditText에서 서버 IP 얻어오기
                    //     Log.d("bbumjun",ip+" :ip");
                    //서버와 연결하는 소켓 생성..

                    socket = new Socket(InetAddress.getByName(receivedIP), PORT);

                    //여기까지 왔다는 것을 예외가 발생하지 않았다는 것이므로 소켓 연결 성공..
                    //서버와 메세지를 주고받을 통로 구축
                    Log.d("jj","client socket created");
                    is = new ObjectInputStream(socket.getInputStream());
                    os = new ObjectOutputStream(socket.getOutputStream());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

                //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                while (true) {
                    try {
                        helperMsg = (String)is.readObject(); //서버 부터 메세지가 전송되면 이를 UTF형식으로 읽어서 String 으로 리턴

                        //서버로부터 읽어들인 메시지msg를 TextView에 출력..
                        //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
                        //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
                        //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                printMs.append(receivedId + ":  " + helperMsg + "\n");
                            }
                        });
                        //////////////////////////////////////////////////////////////////////////
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }//while
            }//run method...
        }).start();//Thread 실행..
    }

    public void addBtnListener(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  String sendText = InitApp.sUser.getDisplayName() + " :  " + sendMs.getText().toString();
                printMs.append("Me  : " + sendMs.getText().toString() + "\n");

                if (os == null) {
                    Log.d("jj","os is null");
                    return;
                }//클라이언트와 연결되어 있지 않다면 전송불가..

                //네트워크 작업이므로 Thread 생성
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //클라이언트로 보낼 메세지 EditText로 부터 얻어오기
                        String msg = sendMs.getText().toString();
                        try {
                            os.writeObject(msg);
                            //os.writeUTF(msg); //클라이언트로 메세지 보내기.UTF 방식으로(한글 전송가능...)
                            os.flush();   //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드..

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start(); //Thread 실행..

            }
        });

        drawSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (oos == null) {
                    Log.d("bbumjun01","oos isn't connected");
                    return; //클라이언트와 연결되어 있지 않다면 전송불가..
                }
                Log.d("bbumjun123","onclick 실행됨");
                //네트워크 작업이므로 Thread 생성
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //클라이언트로 보낼 메세지 EditText로 부터 얻어오기
                        ArrayList<Path> pathMsg = mDraw.getPaths();
                        try {
                            oos.writeObject(pathMsg);
                            oos.flush();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start(); //Thread 실행..
            }
        });
    }


}
*/
