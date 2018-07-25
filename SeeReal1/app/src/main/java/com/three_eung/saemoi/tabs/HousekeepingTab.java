package com.three_eung.saemoi.tabs;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.three_eung.saemoi.R;
import com.three_eung.saemoi.databinding.FragmentHousekeepBinding;

/**
 * Created by CH on 2018-02-18.
 */

public class HousekeepingTab extends Fragment {
    private static final String TAG = HousekeepingTab.class.getSimpleName();

    private FragmentHousekeepBinding mBinding;
    private HousekeepPagerAdapter mAdapter;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        HousekeepingTab housekeepingTab = new HousekeepingTab();
        housekeepingTab.setArguments(args);

        return housekeepingTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_housekeep, container, false);

        mAdapter = new HousekeepPagerAdapter(getChildFragmentManager());
        mBinding.housekeepPager.setAdapter(mAdapter);
        mBinding.housekeepPager.setCurrentItem(1);
        mBinding.housekeepTab.setupWithViewPager(mBinding.housekeepPager);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    class HousekeepPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_NUMBER = 3;

        public HousekeepPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return BudgetTab.newInstance();
                case 1:
                    return CalendarTab.newInstance();
                case 2:
                    return StatisticsTab.newInstance();
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

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "예산";
                case 1:
                    return "달력";
                case 2:
                    return "통계";
                default:
                    return null;
            }
        }
    }
}
