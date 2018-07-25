package com.three_eung.saemoi;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.databinding.ActivityCategoryAddingBinding;
import com.three_eung.saemoi.dialogs.CategoryDialog;

public class CategoryAddingActivity extends AppCompatActivity {
    private int selected;
    private ActivityCategoryAddingBinding mBinding;

    /*
    이미지뷰 가져올때 image.setImageResource(Utils.getCategoryDrawable(숫자));
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = (ActivityCategoryAddingBinding) DataBindingUtil.setContentView(this, R.layout.activity_category_adding);

        setSupportActionBar(mBinding.cateAddToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("카테고리 추가");

        mBinding.cateAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryIcon();
            }
        });
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
                if(!TextUtils.isEmpty(mBinding.cateAddName.getText().toString()) && selected != 0) {
                    DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("info").child("category");
                    if(mBinding.cateAddIn.isChecked())
                        mRef.child("in").child(mBinding.cateAddName.getText().toString()).setValue(selected);
                    if(mBinding.cateAddEx.isChecked())
                        mRef.child("ex").child(mBinding.cateAddName.getText().toString()).setValue(selected);
                    Toast.makeText(getApplicationContext(), "내 카테고리에 저장 -> . <-", Toast.LENGTH_LONG).show();
                } else if(selected == 0) {
                    Toast.makeText(getApplicationContext(), "이미지를 선택해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "카테고리 이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCategoryIcon() { // 다이알로그 보여주는 부분
        FragmentManager fm = getSupportFragmentManager();
        CategoryDialog.newInstance(new CategoryDialog.CateListener() {
            @Override
            public void onSelectComplete(int value) {
                selected = value;
                mBinding.cateAddImage.setImageResource(Utils.getCategoryDrawable(selected));
            }
        }).show(fm, "Category");
    }
}
