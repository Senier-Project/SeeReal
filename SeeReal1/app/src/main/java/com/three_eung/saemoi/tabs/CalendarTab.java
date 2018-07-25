package com.three_eung.saemoi.tabs;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.three_eung.saemoi.DailyListActivity;
import com.three_eung.saemoi.Events;
import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.bind.CalendarBind;
import com.three_eung.saemoi.databinding.FragmentCalendarBinding;
import com.three_eung.saemoi.databinding.ItemCalendarBinding;
import com.three_eung.saemoi.infos.DayInfo;
import com.three_eung.saemoi.infos.HousekeepInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by CH on 2018-02-18.
 */

public class CalendarTab extends Fragment implements View.OnClickListener {
    private static final String TAG = CalendarTab.class.getSimpleName();

    private FragmentCalendarBinding mBinding;
    private Calendar calendar;
    private String today;

    private ArrayList<DayInfo> calendarList = new ArrayList<DayInfo>();

    private enum DayOfTheWeek {
        SUN, MON, TUE, WED, THU, FRI, SAT
    }

    private CalendarAdapter mAdapter;
    private ArrayList<HousekeepInfo> mHousekeepList;

    public static Fragment newInstance() {
        CalendarTab calendarTab = new CalendarTab();

        return calendarTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);

        mBinding.calendarBtnPrev.setOnClickListener(this);
        mBinding.calendarBtnNext.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();

        StringBuffer buffer = new StringBuffer();
        buffer.append(calendar.get(Calendar.YEAR));
        buffer.append(calendar.get(Calendar.MONTH) + 1);
        buffer.append(calendar.get(Calendar.DATE));
        today = buffer.toString();

        mAdapter = new CalendarAdapter(calendarList);
        mBinding.calendarCalGrid.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        mHousekeepList = ((InitApp) (getActivity().getApplication())).getHousekeepList();
        EventBus.getDefault().register(this);
        refreshAdapter(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_btnPrev:
                refreshAdapter(-1);
                break;
            case R.id.calendar_btnNext:
                refreshAdapter(1);
        }
    }

    private void setList() {
        calendarList.clear();
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayNum; i++) {
            DayInfo dayInfo = new DayInfo();
            calendarList.add(dayInfo);
        }

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            DayInfo dayInfo = new DayInfo(i + 1);
            calendarList.add(dayInfo);
        }

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (date != null) {
                if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    int idx = dayNum + date.get(Calendar.DATE) - 2;

                    if (housekeepInfo.getIsIncome()) {
                        int newValue = calendarList.get(idx).getIncome() + housekeepInfo.getValue();
                        calendarList.get(idx).setIncome(newValue);
                    } else {
                        int newValue = calendarList.get(idx).getExpend() + housekeepInfo.getValue();
                        calendarList.get(idx).setExpend(newValue);
                    }
                }
            }
        }
    }

    public void refreshAdapter(int value) {
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, value);

        setList();

        mBinding.calendarMonth.setText(Utils.toYearMonth(calendar));

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();

        refreshAdapter(0);
    }

    class CalendarAdapter extends BaseAdapter implements View.OnClickListener {
        private ArrayList<DayInfo> items;

        class ViewHolder {
            private final ItemCalendarBinding itemBinding;
            private View view;

            public ViewHolder(ItemCalendarBinding itemBinding) {
                this.itemBinding = itemBinding;
                this.view = itemBinding.getRoot();
            }
        }

        public CalendarAdapter(ArrayList<DayInfo> items) {
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                ItemCalendarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar, parent, false);

                viewHolder = new ViewHolder(binding);

                viewHolder.view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.itemBinding.itemCalIn.setVisibility(View.INVISIBLE);
            viewHolder.itemBinding.itemCalEx.setVisibility(View.INVISIBLE);
            viewHolder.itemBinding.itemCalDay.setTextColor(Color.BLACK);

            DayInfo dayInfo = items.get(position);

            if (dayInfo.getDay() == 0) {
                viewHolder.view.setClickable(false);
            } else {
                viewHolder.view.setOnClickListener(this);
                if (dayInfo.getIncome() != 0 || dayInfo.getExpend() != 0) {
                    viewHolder.itemBinding.itemCalIn.setVisibility(View.VISIBLE);
                    viewHolder.itemBinding.itemCalEx.setVisibility(View.VISIBLE);
                }
            }

            if (position % 7 == DayOfTheWeek.SAT.ordinal()) {
                viewHolder.itemBinding.itemCalDay.setTextColor(Color.BLUE);
            } else if (position % 7 == DayOfTheWeek.SUN.ordinal()) {
                viewHolder.itemBinding.itemCalDay.setTextColor(Color.RED);
            }

            viewHolder.itemBinding.setCalendar(new CalendarBind(dayInfo));

            if (today.equals(calendar.get(Calendar.YEAR) + "" + (calendar.get(Calendar.MONTH) + 1) + "" + dayInfo.getDay())) {
                viewHolder.itemBinding.itemCalDay.setText("★ " + dayInfo.getDay());
            }

            return viewHolder.view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();

            int mDay;

            String dayStr = viewHolder.itemBinding.getCalendar().getDay();
            if (dayStr.contains("★")) {
                mDay = Integer.parseInt(dayStr.substring(2, dayStr.length()));
            } else {
                mDay = Integer.parseInt(dayStr);
            }

            Calendar now = Calendar.getInstance();
            now.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), mDay);

            Intent intent = new Intent(getActivity(), DailyListActivity.class);
            intent.putExtra("date", now.getTimeInMillis());
            startActivity(intent);
        }
    }
}