package com.three_eung.saemoi;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.three_eung.saemoi.databinding.ActivityMainBinding;
import com.three_eung.saemoi.tabs.HomeTab;
import com.three_eung.saemoi.tabs.HousekeepingTab;
import com.three_eung.saemoi.tabs.SavingTab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CH on 2018-02-18.
 * 이 액티비티는 인증과정이 선행된 후 나타나는 액티비티로서, 페이지의 틀과 프래그먼트만을 가지고 있다.
 * 실질적인 화면들은 모두 프래그먼트가 가지고 있다.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private ActivityMainBinding mBinding;

    private ActionBarDrawerToggle mToggle;

    private Bitmap bitmap;

    private MainPageAdapter mainPageAdapter;

    private TextView userName, userEmail;
    private CircleImageView profileImg;

    private FirebaseAuth.AuthStateListener mListener;

    private volatile boolean isQuit = true;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);    // Layout을 바인딩해준다.

        setFirebase();
        /*
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            startApp();
        }*/

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startApp();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한 승인 거부됨.", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한을 허용하지 않으면 결제 문자 연동을 할 수 없습니다.\n[설정] > [권한]에서 권한을 설정해주세요.")
                .setPermissions(Manifest.permission.RECEIVE_SMS)
                .check();
    }

    public void startApp() {
        setSupportActionBar(mBinding.toolBar);  // 기본 액션바 대신 커스텀 툴바로 교체.
        ActionBar actionBar = getSupportActionBar();    // 설정된 액션바를 가져옴
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // Drawer의 아이콘(왼쪽 상단 메뉴버튼) 설정해준다.
        actionBar.setDisplayHomeAsUpEnabled(true);  // Drawer 아이콘 활성화.

        initView();
    }

    private void initView() {
        mToggle = new ActionBarDrawerToggle(this, mBinding.drawer, R.string.open, R.string.close);  // Drawer 토글 동작 생성.
        mBinding.drawer.addDrawerListener(mToggle); // Drawer 토글 동작 등록.
        mToggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);   // Drawer 아이템 클릭 리스너 설정.
        mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mBinding.mainPager.setAdapter(mainPageAdapter);    // 뷰페이저에 커스터마이징된 어댑터 연결.
        //mBinding.mainPager.setCurrentItem(1);
        mBinding.mainTab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        mBinding.mainPager.setCurrentItem(1);
                        return true;
                    case R.id.bottom_calc:
                        mBinding.mainPager.setCurrentItem(0);
                        return true;
                    case R.id.bottom_saving:
                        mBinding.mainPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });    // 탭 레이아웃과 뷰페이저 연동.
        mBinding.mainTab.setSelectedItemId(R.id.bottom_home);
    }

    public void setFirebase() {
        InitApp.sDatabase = FirebaseDatabase.getInstance();
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                InitApp.sUser = InitApp.sAuth.getCurrentUser();

                // 유저가 없다고 확인될 경우, 액티비티를 종료하고 로그인 액티비티로 넘어감.
                if (InitApp.sUser == null) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else {    // 유저가 존재할 경우, Drawer 헤더에 유저 정보 표시.
                    initProfileImage();
                    userName = (TextView) findViewById(R.id.userName);
                    userEmail = (TextView) findViewById(R.id.userEmail);
                    userName.setText(InitApp.sUser.getDisplayName());
                    userEmail.setText(InitApp.sUser.getEmail());
                }
            }
        };

        //((InitApp)getApplication()).initDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();

        InitApp.sAuth.addAuthStateListener(mListener);  // 인증 상태 리스너 등록.
    }

    @Override
    public void onStop() {
        if (mListener != null) {
            InitApp.sAuth.removeAuthStateListener(mListener);   // 인증 상태 리스너 해제.
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(isQuit) {
            freeValue();
        }

        super.onDestroy();
    }

    private void freeValue() {
        ((InitApp)getApplication()).terminate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    /*
     * 뒤로 버튼 두번 누를 시 앱이 종료되게 함. (단, Drawer가 열려 있을 시에는 Drawer가 닫히게 함.)
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                isQuit = false;
                freeValue();

                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Drawer 내의 아이템이 선택 되었을 때의 이벤트.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_info:
               // startActivity(new Intent(MainActivity.this, Information.class));
                break;
            case R.id.nav_budget:
                startActivity(new Intent(MainActivity.this, BudgetSettingActivity.class));
                break;
            case R.id.nav_category:
                startActivity(new Intent(MainActivity.this, CategoryAddingActivity.class));
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
        }

        mBinding.drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initProfileImage() {
        if (InitApp.sUser.getPhotoUrl() != null) {
            final String mPhotoUrl = InitApp.sUser.getPhotoUrl().toString();
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(mPhotoUrl);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            mThread.start();

            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            profileImg = (CircleImageView) findViewById(R.id.profile_img);
            profileImg.setImageBitmap(bitmap);
        }
    }

    private void signOut() {
        List<? extends UserInfo> providerData = InitApp.sUser.getProviderData();

        // Firebase sign out
        InitApp.sAuth.signOut();

        for (UserInfo info : providerData) {
            switch (info.getProviderId()) {
                // 구글 인증을 받은 유저일 경우.
                case "google.com":
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "GoogleSignOutComplete");
                        }
                    });
                    break;

                // 카카오 인증을 받은 유저일 경우.
                case "firebase":
                    if (info.getUid().startsWith("kakao")) {
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.d(TAG, "KakaoSignOutComplete");
                            }
                        });
                    }
                    break;
            }
        }
    }

    public MainPageAdapter getAdapter() {
        return mainPageAdapter;
    }

    class MainPageAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_NUMBER = 3;

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HousekeepingTab.newInstance();
                case 1:
                    return HomeTab.newInstance();
                case 2:
                    return SavingTab.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }
    }
}