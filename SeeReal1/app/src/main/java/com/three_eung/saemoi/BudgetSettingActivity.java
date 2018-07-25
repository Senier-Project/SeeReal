package com.three_eung.saemoi;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.bind.BudgetBind;
import com.three_eung.saemoi.databinding.ActivityBudgetSettingBinding;
import com.three_eung.saemoi.databinding.ItemCategoryBudgetBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSettingActivity extends AppCompatActivity {
    private ArrayList<BudgetBind> mBindingList;
    private HashMap<String, Integer> mBudget;
    private HashMap<String, Integer> mCate;
    private BudgetSettingAdapter mAdapter;
    LinearLayoutManager layoutManager;
    ActivityBudgetSettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = (ActivityBudgetSettingBinding) DataBindingUtil.setContentView(this, R.layout.activity_budget_setting);

        setSupportActionBar(mBinding.setBudgetToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("예산 설정");

        //저장된 카테고리마다의 예산
        //카테고리 이름 - 예산 값 식비, 의류비, 유흥비
        mBudget = (HashMap<String, Integer>)((InitApp) getApplication()).getBudget();
        mCate = (HashMap<String, Integer>)((InitApp) getApplication()).getExCate();
        mBindingList = new ArrayList<>();

        for(String cate_key : mCate.keySet()) {
            if(mBudget.containsKey(cate_key)) {
                BudgetBind budgetBind = new BudgetBind(cate_key, Integer.toString(mBudget.get(cate_key)));

                mBindingList.add(budgetBind);
            } else {
                BudgetBind budgetBind = new BudgetBind(cate_key, "");

                mBindingList.add(budgetBind);
            }
        }

        if(mBudget.containsKey("total")) {
            mBinding.setBudgetTotal.setText(Utils.toCurrencyFormat(mBudget.get("total")));
        }

        mBinding.setBudgetTotal.addTextChangedListener(new TextWatcher() {
            String strAmount = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(strAmount)) {
                    strAmount = setStrDataToComma(s.toString().replace(",", ""));
                    mBinding.setBudgetTotal.setText(strAmount);
                    Editable e = mBinding.setBudgetTotal.getText();
                    Selection.setSelection(e, strAmount.length());
                }
            }

            private String setStrDataToComma(String str) {
                if(str.length() == 0)
                    return "";
                int value = Integer.parseInt(str);

                return Utils.toCurrencyFormat(value);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mAdapter = new BudgetSettingAdapter(this, mBindingList);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mBinding.setBudgetList.setHasFixedSize(true);
        mBinding.setBudgetList.setLayoutManager(layoutManager);
        mBinding.setBudgetList.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mBinding.setBudgetList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                HashMap<String, Integer> newBudget = new HashMap<>();

                String total = mBinding.setBudgetTotal.getText().toString().replace(",", "");

                if(!TextUtils.isEmpty(total))
                    newBudget.put("total", Integer.parseInt(total));

                for(int position = 0; position < mAdapter.getItemCount(); position++) {
                    View v = mBinding.setBudgetList.getLayoutManager().findViewByPosition(position);

                    if(v != null) {
                        String valueText = ((EditText) v.findViewById(R.id.item_cate_value)).getText().toString().replace(",", "");
                        if (!TextUtils.isEmpty(valueText)) {
                            String title = ((TextView) v.findViewById(R.id.item_cate_title)).getText().toString();
                            int value = Integer.parseInt(valueText);

                            newBudget.put(title, value);
                        }
                    }
                }

                DatabaseReference mRef = ((InitApp)getApplication()).sDatabase.getReference("users").child(((InitApp)getApplication()).sUser.getUid()).child("info").child("budget");
                mRef.setValue(newBudget);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    class BudgetSettingAdapter extends RecyclerView.Adapter<BudgetSettingAdapter.ViewHolder>{
        private Context mContext;
        private ArrayList<BudgetBind> items;
        private View.OnLongClickListener mListener;

        public BudgetSettingAdapter(Context context,ArrayList<BudgetBind> items) {
            this.mContext = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemCategoryBudgetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_category_budget, parent, false);

            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            viewHolder.itemBinding.itemCateValue.addTextChangedListener(new TextWatcher() {
                String strAmount = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.toString().equals(strAmount)) {
                        strAmount = setStrDataToComma(s.toString().replace(",", ""));
                        viewHolder.itemBinding.itemCateValue.setText(strAmount);
                        Editable e = viewHolder.itemBinding.itemCateValue.getText();
                        Selection.setSelection(e, strAmount.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }

                private String setStrDataToComma(String str) {
                    if(str.length() == 0)
                        return "";
                    int value = Integer.parseInt(str);

                    return Utils.toCurrencyFormat(value);
                }
            });

            viewHolder.itemBinding.setCate(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemCategoryBudgetBinding itemBinding;
            public ViewHolder(ItemCategoryBudgetBinding itemBinding) {
                super(itemBinding.getRoot());

                this.itemBinding = itemBinding;
            }
        }
    }
}