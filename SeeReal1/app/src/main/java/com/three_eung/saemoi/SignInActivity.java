package com.three_eung.saemoi;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.three_eung.saemoi.databinding.ActivitySignBinding;
import com.three_eung.saemoi.dialogs.LoadingDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CH on 2018-02-18.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SignInActivity.class.getName();

    private Toast toast;
    private ActivitySignBinding mBinding;

    // Firebase Variables.
    private FirebaseAuth.AuthStateListener mListener;

    // Google Auth Variables.
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_SIGN_IN = 9001;

    private LoadingDialog mProgressDialog;

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        setTheme(R.style.SignTheme);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign);

        InitApp.sAuth = FirebaseAuth.getInstance();

        toast = Toast.makeText(SignInActivity.this, "", Toast.LENGTH_SHORT); // Toast를 많이 띄우기 때문에 미리 하나만 생성해둠(효율성).
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                InitApp.sUser = InitApp.sAuth.getCurrentUser();

                // 인증된 유저가 존재할 경우 바로 Main액티비티로 넘긴 후 SignIn액티비티는 종료.
                if (InitApp.sUser != null) {
                    toast.setText(InitApp.sUser.getDisplayName() + "님 환영합니다.");
                    toast.show();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    mProgressDialog = null;
                    finish();
                } else {
                    initSignIn();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        InitApp.sAuth.addAuthStateListener(mListener); // 액티비티가 화면에 보일 때 인증 상태 리스너 추가.
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mListener != null) {
            InitApp.sAuth.removeAuthStateListener(mListener); // 액티비티가 더이상 화면에 보이지 않을 때(다른 액티비티 시작, 혹은 잠금화면 진입) 리스너 제거.
        }
        hideProgressDialog();
        mProgressDialog = null;
        toast.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso); // google-services.json내의 클라이언트 정보를 가져와서 이메일 기반 인증으로 설정.

        mBinding.buttonGoogleSignin.setOnClickListener(this); // 구글 로그인 버튼 온클릭 리스너 설정.

        Session.getCurrentSession().addCallback(new KakaoSessionCallback()); // 카카오 세션 콜백 추가.

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_login);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBinding.buttonGoogleSignin.setEnabled(false);
                mBinding.buttonKakaoSignin.setEnabled(false);
                mBinding.layoutLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.buttonGoogleSignin.setEnabled(true);
                mBinding.buttonKakaoSignin.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBinding.layoutLogin.startAnimation(animation);
        mBinding.layoutLogin.invalidate();
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent(); // 구글의 인텐트 가져옴.
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN); // 구글 자체의 인텐트로 액티비티 시작. (작동 과정은 이해할 수 없으나 아마 구글 유저 선택 창 띄우는 듯)
    }

    private void (GoogleSignInAccount acct) {
        showProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null); // Google의 credential(일종의 인증정보?)을 가져옴.
        signInCredential(credential); // 가져온 credential로 파이어베이스 인증 시작.
    }

    /*
     * credential로 파이어베이스 인증 시작.
     * 성공시 성공했다는 토스트메시지와 파이어베이스 유저 가져옴.
     * 실패시 실패 토스트 띄움.
     */
    private void signInCredential(AuthCredential credential) {
        InitApp.sAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            toast.setText("구글 로그인 성공");
                            toast.show();
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            toast.setText("구글 로그인 실패");
                            toast.show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 카카오 인증일 경우 다음 과정을 진행할 필요 없으므로 종료.
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        // requestCode가 구글 인증일 경우. (startActivityForResult에서 던져준 값이 requestCode)
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); // 구글 유저 선택창에서 가져온 계정 정보.
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); // 구글 계정 정보로 파이어베이스 인증.
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed: ", e);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_google_signin:
                signInGoogle();
                break;
        }
    }

    // ProgressDialog
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(this);
        }

        if(!SignInActivity.this.isFinishing())
            mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /*
     * 미리 구축한 카카오 토큰 -> 파이어베이스 토큰 변환 서버를 활용하여
     * 파이어베이스 토큰 획득.
     */
    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.kakao_auth_domain) + "/verifyToken";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            /*
             * 서버로부터 반환받은 Response값(JSON파일)에서 파이어베이스 토큰 추출.
             */
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    source.setResult(firebaseToken);
                } catch (Exception e) {
                    source.setException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                source.setException(error);
            }
        }) {
            /*
             * 서버로 보내는 Request에 카카오 access 토큰을 파라미터로 담음.
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };
        queue.add(request);
        return source.getTask();
    }

    private class KakaoSessionCallback implements ISessionCallback {
        /*
         * 카카오 세션을 성공적으로 열었을 경우.
         */
        @Override
        public void onSessionOpened() {
            showProgressDialog();
            String accessToken = Session.getCurrentSession().getAccessToken(); // 카카오 accessToken을 가져옴.
            getFirebaseJwt(accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                @Override
                public Task<AuthResult> then(@NonNull Task<String> task) throws Exception { // 가져온 카카오 토큰을 서버로 보내 파이어베이스로 변환, 성공 시 해당 토큰으로 파이어베이스 인증한 정보 반환.
                    String firebaseToken = task.getResult();
                    return InitApp.sAuth.signInWithCustomToken(firebaseToken);
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        toast.setText("카카오 로그인 성공");
                        toast.show();
                    } else {
                        hideProgressDialog();
                        toast.setText("카카오 로그인 실패");
                        toast.show();
                        if (task.getException() != null) {
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                }
            });
        }

        /*
         * 카카오 세션 자체를 열지 못했을 경우.
         */
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.e(TAG, exception.toString());
                hideProgressDialog();
            }
        }
    }
}
