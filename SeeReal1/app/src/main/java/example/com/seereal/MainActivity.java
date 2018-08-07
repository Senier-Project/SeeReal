package example.com.seereal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Bitmap bitmap;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    String uid = InitApp.sUser.getUid();

    //DatabaseReference mData = mDatabase.child("users").child(uid).child("img");

    TextView userName, userEmail;
    ImageView profileImg;
    private FirebaseAuth.AuthStateListener mListener;

    TextView testText;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public static int width;
    public static int height;

    public static String userID;


    @Override
    protected void onStop() {
        if (mListener != null) {
            InitApp.sAuth.removeAuthStateListener(mListener);   // 인증 상태 리스너 해제.
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();


        InitApp.sAuth.addAuthStateListener(mListener);
        Log.d("susu","uid"+uid);
        /*
       mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                testText.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w( "Failed to read value.", databaseError.toException());
            }
        });*/
    }
    //  TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("susu","onCreate");

        setFirebase();

        testText = (TextView) findViewById(R.id.testText);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        BottomNavigationView bottomNavigationItemView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(this);
        bottomNavigationItemView.setSelectedItemId(R.id.navi_freind);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

       /* mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                switch(id){
                    case R.id.nav_notify :
                        Toast.makeText(MainActivity.this, "공지사항", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_myPage :
                        Toast.makeText(MainActivity.this, "마이페이지", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, MyPageActivity.class));
                        break;
                    case R.id.nav_setting :
                        Toast.makeText(MainActivity.this, "설정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_sign_out :
                        Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        //FirebaseAuth.getInstance().signOut();
                        signOut();
                        break;
                    case R.id.nav_info :
                        Toast.makeText(MainActivity.this, "개발자정보", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),InfoActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });



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
                            Log.d("susu", "GoogleSignOutComplete");
                        }
                    });
                    break;

            }
        }
    }


    public void setFirebase() {
        Log.d("susu","firebase처음");
        InitApp.sDatabase = FirebaseDatabase.getInstance();
        mListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                InitApp.sUser = InitApp.sAuth.getCurrentUser();
                Log.d("susu","setFirebase들어옴");
                // 유저가 없다고 확인될 경우, 액티비티를 종료하고 로그인 액티비티로 넘어감.
                if (InitApp.sUser == null) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // 유저가 존재할 경우, Drawer 헤더에 유저 정보 표시.
                    //Log.d("susu","userName"+InitApp.sUser.getDisplayName());
                    userName = (TextView) findViewById(R.id.userName);
                    userEmail = (TextView) findViewById(R.id.userEmail);
                    profileImg = (ImageView) findViewById(R.id.imageView);

                    userName.setText(InitApp.sUser.getDisplayName());
                    userEmail.setText(InitApp.sUser.getEmail());

                    //profileImg.setImageResource(Utils.getProfileImgDrawable(((InitApp) getApplication()).getProfileImg()));
                    //(InitApp) getApplication()).getProfileImg())

                   // Log.d("susu","initApp"+((InitApp) getApplication()).getProfileImg());

                    DatabaseReference mDatabase = InitApp.sDatabase.getReference();
                    String uid = InitApp.sUser.getUid();
                    DatabaseReference mData = mDatabase.child("users").child(uid).child("img");


                    mData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int selected = dataSnapshot.getValue(Integer.class);
                            //selectF = selected;
                            profileImg.setImageResource(Utils.getProfileImgDrawable(selected));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w( "Failed to read value.", databaseError.toException());
                        }
                    });

                  // profileImg.setImageResource(R.drawable.profile_0);

                    //사용자 ID 받기
                    userID = userEmail.getText().toString().replaceAll("@gmail.com","");
                }
            }
        };

       // ((InitApp)getApplication()).initDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navi_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navi_server:
                // tv.setText("SERVER");
                fragment = new ServerFragment();
                break;
            case R.id.navi_more:
                fragment = new MoreFragment();
                //tv.setText("더보기이ㅣ");
                break;
            case R.id.navi_freind:
                fragment = FriendFragment.newInstance();
                break;

            default:
                return false;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

}


