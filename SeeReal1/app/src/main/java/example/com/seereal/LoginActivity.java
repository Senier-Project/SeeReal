package example.com.seereal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by sh
 */

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mListener;
    final int RC_SIGN_IN = 1001; // 로그인 확인여부 코드
    private Toast toast;
    private SignInButton signInButton; //구글 로그인 버튼
    private GoogleApiClient mGoogleApiClient; //API 클라이언트

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //mAuth = FirebaseAuth.getInstance(); // 인스턴스 생성


        // InitApp.sUser = InitApp.sAuth.getCurrentUser();
        InitApp.sAuth = FirebaseAuth.getInstance();
        toast = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT);
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference mData = mDatabase.child("users").child(InitApp.sUser.getUid()).child("img");
        // final int imgNum;


        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                InitApp.sUser = InitApp.sAuth.getCurrentUser();

                // 인증된 유저가 존재할 경우 바로 Main액티비티로 넘긴 후 SignIn액티비티는 종료.
                if (InitApp.sUser != null) {
                    toast.setText(InitApp.sUser.getDisplayName() + "님 환영합니다.");
                    toast.show();

                    passPushTokenToServer();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    finish();
                } else {
                    initLogin();
                }

            }
        };


    }

    private void passPushTokenToServer() {
        String uid=InitApp.sUser.getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }

    private void initLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                // InitApp.sUser = InitApp.sAuth.getCurrentUser();
                //InitApp.sUser.getDisplayName()
                Log.d("susu", "이름 =" + account.getDisplayName());
                Log.d("susu", "이메일=" + account.getEmail());
                Log.d("susu", "getId()=" + account.getId());
                Log.d("susu", "getAccount()=" + account.getAccount());
                Log.d("susu", "getIdToken()=" + account.getIdToken());
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Log.w(TAG, "Google sign in failed: ");
            }
        }
    }

    public void onStart() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();

        InitApp.sAuth.addAuthStateListener(mListener); // 액티비티가 화면에 보일 때 인증 상태 리스너 추가.

        // Check if user is signed in (non-null) and update UI accordingly.
      /*  FirebaseUser currentUser = InitApp.sAuth.getCurrentUser();
        if(currentUser!=null){ // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }*/


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            InitApp.sAuth.removeAuthStateListener(mListener); // 액티비티가 더이상 화면에 보이지 않을 때(다른 액티비티 시작, 혹은 잠금화면 진입) 리스너 제거.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        InitApp.sAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
