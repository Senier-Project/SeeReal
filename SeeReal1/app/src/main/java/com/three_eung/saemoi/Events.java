package com.three_eung.saemoi;

import android.support.annotation.NonNull;

import com.three_eung.saemoi.infos.HousekeepInfo;
import com.three_eung.saemoi.infos.SavingInfo;

import java.util.ArrayList;

public class Events {
    private final ArrayList<HousekeepInfo> mHkList;
    private final ArrayList<SavingInfo> mSvList;

    public Events(@NonNull ArrayList<HousekeepInfo> mHkList, @NonNull ArrayList<SavingInfo> mSvList) {
        this.mHkList = mHkList;
        this.mSvList = mSvList;
    }

    public ArrayList<HousekeepInfo> getHkList() {
        return mHkList;
    }

    public ArrayList<SavingInfo> getSvList() {
        return mSvList;
    }
}
