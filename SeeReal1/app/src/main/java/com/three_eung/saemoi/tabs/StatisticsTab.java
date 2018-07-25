package com.three_eung.saemoi.tabs;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.three_eung.saemoi.Events;
import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.databinding.FragmentStatisticsBinding;
import com.three_eung.saemoi.infos.HousekeepInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StatisticsTab extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private static final String TAG = StatisticsTab.class.getSimpleName();

    private FragmentStatisticsBinding mBinding;
    private ArrayList<HousekeepInfo> mHousekeepList = new ArrayList<>();
    private ArrayList<PieEntry> yValues;
    private volatile boolean isIncome = true;
    private Calendar calendar;
    private HashMap<String, Integer> mValueMap;

    public static Fragment newInstance() {
        StatisticsTab statisticsTab = new StatisticsTab();

        return statisticsTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);

        calendar = Calendar.getInstance();

        mBinding.statisIncome.setOnClickListener(this);
        mBinding.statisOutcome.setOnClickListener(this);

        mBinding.statisChart.setUsePercentValues(false);
        mBinding.statisChart.getDescription().setEnabled(false);
        mBinding.statisChart.setExtraOffsets(5, 10, 5, 5);

        mBinding.statisChart.setDragDecelerationFrictionCoef(0.95f);

        mBinding.statisChart.setDrawHoleEnabled(false);
        mBinding.statisChart.setHoleColor(Color.WHITE);
        mBinding.statisChart.setTransparentCircleRadius(61f);
        mBinding.statisChart.setClickable(false);

        yValues = new ArrayList<>();

        PieDataSet dataSet = new PieDataSet(yValues, "카테고리");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        mBinding.statisChart.setData(data);


        //mChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        updateData();

        return mBinding.getRoot();
    }

    private void updateData() {
        yValues.clear();
        mValueMap = new HashMap<>();

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                String category = housekeepInfo.getCategory();

                if (housekeepInfo.getIsIncome() && isIncome) {
                    Log.e(TAG, "updateData: " + housekeepInfo.getIsIncome() + "  " + isIncome);
                    if (mValueMap.containsKey(category)) {
                        int value = mValueMap.get(category) + housekeepInfo.getValue();
                        mValueMap.put(category, value);
                    } else {
                        mValueMap.put(category, housekeepInfo.getValue());
                    }
                } else if (!housekeepInfo.getIsIncome() && !isIncome) {
                    Log.e(TAG, "updateData: " + housekeepInfo.getIsIncome() + "  " + isIncome);
                    if (mValueMap.containsKey(category)) {
                        int value = mValueMap.get(category) + housekeepInfo.getValue();
                        mValueMap.put(category, value);
                    } else {
                        mValueMap.put(category, housekeepInfo.getValue());
                    }
                }
            }
        }

        for (String key : mValueMap.keySet()) {
            yValues.add(new PieEntry(mValueMap.get(key), key));
        }

        mBinding.statisChart.notifyDataSetChanged();
        mBinding.statisChart.invalidate();
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
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statis_income:
                isIncome = true;
                updateData();
                break;
            case R.id.statis_outcome:
                isIncome = false;
                updateData();
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
    }
}
