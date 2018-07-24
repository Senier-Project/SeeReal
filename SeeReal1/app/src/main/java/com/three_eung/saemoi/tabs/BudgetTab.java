package com.three_eung.saemoi.tabs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.three_eung.saemoi.Events;
import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.databinding.FragmentBudgetBinding;
import com.three_eung.saemoi.databinding.ItemBudgetBinding;
import com.three_eung.saemoi.infos.BudgetInfo;
import com.three_eung.saemoi.infos.HousekeepInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BudgetTab extends Fragment {
    private static final String TAG = BudgetTab.class.getSimpleName();

    private FragmentBudgetBinding mBinding;
    private ArrayList<HousekeepInfo> mHousekeepList;
    private volatile ArrayList<BudgetInfo> items;

    private HashMap<String, Integer> mBudget;
    private int totalBudget, usedBudget;
    private Calendar calendar;
    private BudgetAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        BudgetTab budgetTab = new BudgetTab();
        budgetTab.setArguments(args);

        return budgetTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_budget, container, false);

        mBinding.budgetUserName.setText(InitApp.sUser.getDisplayName());

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        items = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayout.VERTICAL);

        mAdapter = new BudgetAdapter(this.getContext(), items);

        mBinding.budgetList.setHasFixedSize(true);
        mBinding.budgetList.setLayoutManager(layoutManager);
        mBinding.budgetList.setAdapter(mAdapter);
    }

    private synchronized void updateData() {
        mBudget = (HashMap<String, Integer>) (((InitApp) (getActivity().getApplication())).getBudget());
        items.clear();
        if (mBudget.containsKey("total")) {
            mBinding.budgetNoContainer.setVisibility(View.GONE);
            mBinding.budgetContainer.setVisibility(View.VISIBLE);
            mBinding.budgetContainer.setFocusable(true);
            totalBudget = mBudget.get("total");
        } else {
            totalBudget = 0;
        }
        usedBudget = 0;

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (housekeepInfo.getIsBudget()) {
                if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    int value = housekeepInfo.getValue();
                    String category = housekeepInfo.getCategory();
                    boolean isIncome = housekeepInfo.getIsIncome();
                    boolean isExist = true;

                    if (!isIncome) {
                        if(mBudget.containsKey(category)) {
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCategory().equals(category)) {
                                    int temp = items.get(i).getValue() + value;

                                    items.get(i).setValue(temp);
                                    isExist = false;
                                    break;
                                }
                            }

                            if (isExist) {
                                BudgetInfo budgetInfo = new BudgetInfo(category, value);

                                items.add(budgetInfo);
                            }
                        }
                    }

                    if (isIncome) {
                        usedBudget -= value;
                    } else {
                        usedBudget += value;
                    }
                }
            }
        }

        BudgetInfo budgetInfo = new BudgetInfo("total", usedBudget);
        items.add(0, budgetInfo);

        mBinding.budgetRemain.setText(Utils.toCurrencyString(totalBudget - usedBudget));
        int availBudget = (int) ((double) (totalBudget - usedBudget) / (double) (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DATE) + 1));
        Log.e(TAG, "updateData: " + ((double) totalBudget / (double) calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + "   " + (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DATE) + 1));
        mBinding.budgetAvailable.setText(Utils.toCurrencyString(availBudget));
        mBinding.budgetToday.setText(Utils.toCurrencyFormat(availBudget));
        mBinding.budgetTotal.setText(Utils.toCurrencyString(totalBudget));

        Log.e(TAG, "updateData: " + totalBudget + "  " + availBudget + "  " + usedBudget);

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();
        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e(TAG, "onStart: ");

        mHousekeepList = ((InitApp) (getActivity().getApplication())).getHousekeepList();
        updateData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<BudgetInfo> items;

        public BudgetAdapter(Context context, ArrayList<BudgetInfo> items) {
            this.mContext = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemBudgetBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_budget, parent, false);

            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int itempos = position;

            String category = items.get(itempos).getCategory();
            int value = items.get(itempos).getValue();

            Log.e(TAG, "onBindViewHolder: " + category + "  " + itempos);

            if(category.equals("total"))
                viewHolder.itemBinding.itemBudgetName.setText("총 예산");
            else
                viewHolder.itemBinding.itemBudgetName.setText(category);


            if(mBudget.containsKey(category)) {
                viewHolder.itemBinding.itemBudgetText.setText(Utils.toCurrencyString(mBudget.get(category)) + " 중 " + Utils.toCurrencyString(value) + " 사용");
            }

            ViewGroup.LayoutParams underParams = viewHolder.itemBinding.itemBudgetUnder.getLayoutParams();
            underParams.width = viewHolder.itemBinding.itemBudgetText.getWidth();
            viewHolder.itemBinding.itemBudgetUnder.setLayoutParams(underParams);

            ViewGroup.LayoutParams blankParams = viewHolder.itemBinding.itemBudgetView.getLayoutParams();
            ViewGroup.LayoutParams barParams = viewHolder.itemBinding.itemBudgetBar.getLayoutParams();

            if (value < 0) {
                value = 0;
            }

            Log.e(TAG, "onBindViewHolder: " + category + "   " + mBudget.get(category));
            double ratio = 1.0;
            if (value != 0) {
                ratio = 1.0 - ((double) value / (double) mBudget.get(category));
            } else {
                ratio = 1.0;
            }

            if(ratio < 0.0)
                ratio = 0.0;

            blankParams.width = (int) ((double) (getActivity().getWindowManager().getDefaultDisplay().getWidth() - Utils.dpToPx(getContext(), 55)) * Math.abs(ratio)) + Utils.dpToPx(getContext(), 5);
            Log.e(TAG, "onBindViewHolder: " + ratio + "  " + blankParams.width + "  " + viewHolder.itemBinding.itemBudgetBar.getWidth() + "  " + barParams.width);
            viewHolder.itemBinding.itemBudgetView.setLayoutParams(blankParams);

            if (ratio >= 0.8) {
                viewHolder.itemBinding.itemBudgetBar.setBackgroundResource(R.color.income);
                viewHolder.itemBinding.itemBudgetImg.setImageResource(R.drawable.sae_money);
                if (position == 0)
                    mBinding.budgetImage.setImageResource(R.drawable.sae_money);
            } else if (ratio >= 0.6) {
                viewHolder.itemBinding.itemBudgetBar.setBackgroundResource(R.color.green);
                viewHolder.itemBinding.itemBudgetImg.setImageResource(R.drawable.sae_heart);
                if (position == 0)
                    mBinding.budgetImage.setImageResource(R.drawable.sae_heart);
            } else if (ratio >= 0.4) {
                viewHolder.itemBinding.itemBudgetBar.setBackgroundResource(R.color.yellow);
                viewHolder.itemBinding.itemBudgetImg.setImageResource(R.drawable.sae_lovely);
                if (position == 0)
                    mBinding.budgetImage.setImageResource(R.drawable.sae_lovely);
            } else if (ratio >= 0.2) {
                viewHolder.itemBinding.itemBudgetBar.setBackgroundResource(R.color.red);
                viewHolder.itemBinding.itemBudgetImg.setImageResource(R.drawable.sae_sweat);
                if (position == 0)
                    mBinding.budgetImage.setImageResource(R.drawable.sae_sweat);
            } else if (ratio >= 0) {
                viewHolder.itemBinding.itemBudgetBar.setBackgroundResource(R.color.outcome);
                viewHolder.itemBinding.itemBudgetImg.setImageResource(R.drawable.sae_angry);
                if (position == 0)
                    mBinding.budgetImage.setImageResource(R.drawable.sae_angry);
            }
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            else
                return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemBudgetBinding itemBinding;

            public ViewHolder(ItemBudgetBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }
        }
    }
}
