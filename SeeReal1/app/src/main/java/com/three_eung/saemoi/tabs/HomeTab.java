package com.three_eung.saemoi.tabs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.CardAdapter;
import com.three_eung.saemoi.CardItem;
import com.three_eung.saemoi.Events;
import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.ShadowTransformer;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.databinding.FragmentHomeBinding;
import com.three_eung.saemoi.dialogs.SimpleInputDialog;
import com.three_eung.saemoi.infos.HousekeepInfo;
import com.three_eung.saemoi.infos.SavingInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HomeTab extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeTab.class.getSimpleName();

    private FragmentHomeBinding mBinding;
    private int testSaving;

    private ArrayList<HousekeepInfo> mHousekeepList = new ArrayList<>();
    private ArrayList<SavingInfo> mSavingList = new ArrayList<>();
    private HashMap<String, Integer> mBudget = new HashMap<>();

    private CardPagerAdapter mAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private SparseArray<CardItem> items;

    private int totalBudget;
    private Calendar calendar;

    private DatabaseReference mRef;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);
        HomeTab homeTab = new HomeTab();
        homeTab.setArguments(args);

        return homeTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        mBinding.homeUid.setText(InitApp.sUser.getDisplayName());

        calendar = Calendar.getInstance();

        items = new SparseArray<>();

        mAdapter = new CardPagerAdapter(getContext(), items);

        mCardShadowTransformer = new ShadowTransformer(mBinding.homeCardpager, mAdapter);

        mBinding.homeCardpager.setAdapter(mAdapter);
        mBinding.homeCardpager.setPageTransformer(false, mCardShadowTransformer);
        mBinding.homeCardpager.setOffscreenPageLimit(2);
        mBinding.homeIn.setOnClickListener(this);
        mBinding.homeEx.setOnClickListener(this);

        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("housekeeping");

        //TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.home_indicator);
        //tabLayout.setupWithViewPager(mPager, true);

        return mBinding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mSavingList = events.getSvList();
        this.mHousekeepList = events.getHkList();
        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e(TAG, "onStart: ");

        mHousekeepList = ((InitApp) (getActivity().getApplication())).getHousekeepList();
        mSavingList = ((InitApp) (getActivity().getApplication())).getSavingList();
        updateData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    private void updateData() {
        mBudget = (HashMap<String, Integer>)((InitApp) (getActivity().getApplication())).getBudget();
        Log.e(TAG, "updateData: " + mBudget);

        if(mBudget.containsKey("total"))
            totalBudget = mBudget.get("total");
        else
            totalBudget = 0;

        int usedBudget = 0;
        int income = 0, expend = 0;

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                int value = housekeepInfo.getValue();
                boolean isIncome = housekeepInfo.getIsIncome();

                if (date.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                    if (isIncome) {
                        income += value;
                    } else {
                        expend += value;
                    }
                }

                if (housekeepInfo.getIsBudget()) {
                    if (isIncome) {
                        usedBudget -= value;
                    } else {
                        usedBudget += value;
                    }
                }
            }
        }

        int availBudget = (int) ((double) (totalBudget - usedBudget) / (double) (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DATE) + 1));

        CardItem housekeepItem = new CardItem(0);
        housekeepItem.getItemList().put("first_item", Utils.toCurrencyString(availBudget));
        housekeepItem.getItemList().put("second_item", Utils.toCurrencyString(income));
        housekeepItem.getItemList().put("third_item", Utils.toCurrencyString(expend));

        items.put(0, housekeepItem);

        int totalSaving = 0;
        int todayCount = 0;
        int todaySaving = 0;

        for (SavingInfo savingInfo : mSavingList) {
            totalSaving += savingInfo.getValue() * savingInfo.getCount();
            String start = savingInfo.getStartDate();
            ArrayList<String> savingList = savingInfo.getSavedDate();
            if(savingList != null) {
                for (String value : savingList) {
                    Calendar date = Calendar.getInstance();
                    date.setTime(Utils.stringToDate(value));
                    if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && date.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                        todayCount++;
                        todaySaving += savingInfo.getValue();
                    }
                }
            }
        }

        Log.e(TAG, "total: " + totalSaving);

        testSaving = totalSaving;

        CardItem savingItem = new CardItem(1);
        savingItem.getItemList().put("first_item", Utils.toCurrencyString(totalSaving));
        savingItem.getItemList().put("second_item", Utils.toCurrencyString(todaySaving));
        savingItem.getItemList().put("third_item", String.valueOf(todayCount)+"회");

        items.put(1, savingItem);

        mAdapter.notifyDataSetChanged();
    }

    public ArrayList<HousekeepInfo> getSavingList() {
        return mHousekeepList;
    }

    public int getTestSaving() {
        return testSaving;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getChildFragmentManager();
        SimpleInputDialog inputDialog = null;

        switch (v.getId()){
            case R.id.home_in :
                inputDialog = SimpleInputDialog.newInstance(new SimpleInputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(HousekeepInfo housekeepInfo) {
                        if (housekeepInfo != null){
                            mRef.push().setValue(housekeepInfo);
                        }
                    }
                }, true);
                break;
            case R.id.home_ex:
                inputDialog = SimpleInputDialog.newInstance(new SimpleInputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(HousekeepInfo housekeepInfo) {
                        if (housekeepInfo != null) {
                            mRef.push().setValue(housekeepInfo);
                        }
                    }
                }, false);
                break;

        }

        inputDialog.show(fm, "SimpleInputDialog");
    }

    class CardPagerAdapter extends PagerAdapter implements CardAdapter {
        private Context mContext;
        private SparseArray<View> mViews;
        private SparseArray<CardItem> items;
        private float mBaseElevation;

        public CardPagerAdapter(Context context, SparseArray<CardItem> items) {
            this.mContext = context;
            this.items = items;
            this.mViews = new SparseArray<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_maincard, container, false);

            CardItem item = items.get(position, null);
            if(item != null) {
                bind(item, view);
            }
            CardView cardView = (CardView) view.findViewById(R.id.cardView);

            if (mBaseElevation == 0) {
                mBaseElevation = cardView.getCardElevation();
            }
            cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
            container.addView(view);

            mViews.put(position, view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mViews.remove(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return mViews.indexOfValue((View)object);
        }

        @Override
        public float getBaseElevation() {
            return mBaseElevation;
        }

        @Override
        public CardView getCardViewAt(int position) {
            return (CardView)mViews.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void bind(CardItem item, View view) {
            TextView title = (TextView) view.findViewById(R.id.card_title);
            TextView first_title = (TextView) view.findViewById(R.id.card_first);
            TextView first_item = (TextView) view.findViewById(R.id.card_first_item);
            TextView second_title = (TextView) view.findViewById(R.id.card_second);
            TextView second_item = (TextView) view.findViewById(R.id.card_second_item);
            TextView third_title = (TextView) view.findViewById(R.id.card_third);
            TextView third_item = (TextView) view.findViewById(R.id.card_third_item);

            title.setText(item.getTitle());
            first_title.setText(item.getItemList().get("first_key"));
            first_item.setText(item.getItemList().get("first_item"));
            second_title.setText(item.getItemList().get("second_key"));
            second_item.setText(item.getItemList().get("second_item"));
            third_title.setText(item.getItemList().get("third_key"));
            third_item.setText(item.getItemList().get("third_item"));
        }

        @Override
        public void startUpdate(ViewGroup container) {
        }

        @Override
        public void finishUpdate(ViewGroup container) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public void notifyDataSetChanged() {
            int key = 0;
            for(int i=0; i < mViews.size(); i++) {
                key = mViews.keyAt(i);
                View view = mViews.get(key);

                bind(items.get(key), view);
            }

            super.notifyDataSetChanged();
        }
    }
}