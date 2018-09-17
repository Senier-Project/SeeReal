package example.com.seereal;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class MyPageActivity extends AppCompatActivity {

    TextView userName, userEmail;
    EditText testEdit;

    Button changeBtn;
    ImageView profileImg;

    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("마이 페이지");

        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userName.setText(InitApp.sUser.getDisplayName());
        userEmail.setText(InitApp.sUser.getEmail());

        profileImg= (ImageView) findViewById(R.id.image_profile);

        changeBtn = (Button) findViewById(R.id.changeProfile);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgIcon();
            }
        });




        /*
        testEdit = (EditText) findViewById(R.id.testEdit);

        Button testBtn = (Button) findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFirebase = testEdit.getText().toString();

                //DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("info").child("category");
                DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("test");
                mRef.setValue(testFirebase);
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if(selected != 0) {
                    DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid());

                        mRef.child("img").setValue(selected);

                    Toast.makeText(getApplicationContext(), "사진 저장", Toast.LENGTH_LONG).show();
                } else if(selected == 0) {
                    Toast.makeText(getApplicationContext(), "이미지를 선택해주세요.", Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showImgIcon() { // 다이알로그 보여주는 부분
        FragmentManager fm = getSupportFragmentManager();
        ProfileimgDialog.newInstance(new ProfileimgDialog.CateListener() {
            @Override
            public void onSelectComplete(int value) {
                selected = value;
                profileImg.setImageResource(Utils.getProfileImgDrawable(selected));
                //  mBinding.cateAddImage.setImageResource(Utils.getCategoryDrawable(selected));
            }
        }).show(fm, "ProfileImg");
    }
}
