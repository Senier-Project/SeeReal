package com.three_eung.saemoi;

import android.support.v4.app.Fragment;

import com.three_eung.saemoi.infos.HousekeepInfo;
import com.three_eung.saemoi.infos.SavingInfo;

import java.util.ArrayList;

public abstract class CustomFragment extends Fragment {
    public abstract void setHousekeepData(ArrayList<HousekeepInfo> housekeepData);
    public abstract void setSavingData(ArrayList<SavingInfo> savingData);
}