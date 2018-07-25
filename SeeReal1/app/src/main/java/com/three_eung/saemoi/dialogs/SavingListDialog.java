package com.three_eung.saemoi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.infos.SavingInfo;

import java.util.ArrayList;
import java.util.Calendar;

public class SavingListDialog extends DialogFragment {
    private View mView;
    private SavingInfo savingInfo;
    private TextView title, total, unit, count, startdate, duration, item1, item2, item3;
    private int durDay;

    public static SavingListDialog newInstance(SavingInfo savingInfo) {
        SavingListDialog savingListDialog = new SavingListDialog();
        savingListDialog.setData(savingInfo);

        return savingListDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_saving_list, null);

        initView();

        Calendar today = Calendar.getInstance();
        Calendar startDay = Calendar.getInstance();
        startDay.setTime(Utils.stringToDate(savingInfo.getStartDate()));

        durDay = (int) ((today.getTimeInMillis() - startDay.getTimeInMillis()) / (1000 * 60 * 60 * 24));

        setView();

        builder.setView(mView).setNegativeButton("닫기", null);

        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void initView() {
        title = (TextView) mView.findViewById(R.id.saving_list_title);
        total = (TextView) mView.findViewById(R.id.saving_list_total);
        unit = (TextView) mView.findViewById(R.id.saving_list_unit);
        count = (TextView) mView.findViewById(R.id.saving_list_count);
        startdate = (TextView) mView.findViewById(R.id.saving_list_start);
        duration = (TextView) mView.findViewById(R.id.saving_list_duration);

        item1 = (TextView) mView.findViewById(R.id.saving_list_item1);
        item2 = (TextView) mView.findViewById(R.id.saving_list_item2);
        item3 = (TextView) mView.findViewById(R.id.saving_list_item3);
    }

    private void setView() {
        title.setText(String.valueOf(savingInfo.getTitle()));
        total.setText(Utils.toCurrencyString(savingInfo.getValue() * savingInfo.getCount()));
        unit.setText(Utils.toCurrencyString(savingInfo.getValue()));
        count.setText(Integer.toString(savingInfo.getCount()) + "회");
        startdate.setText(String.valueOf(savingInfo.getStartDate()));
        duration.setText(Integer.toString(durDay) + "일");

        ArrayList<String> savedDate = savingInfo.getSavedDate();
        if (savedDate != null) {
            for (int i = savedDate.size() - 1; i >= 0; i--) {
                if (i == savedDate.size() - 1)
                    item1.setText(savedDate.get(i));
                if (i == savedDate.size() - 2)
                    item2.setText(savedDate.get(i));
                if (i == savedDate.size() - 3) {
                    item3.setText(savedDate.get(i));
                    break;
                }
            }
        }
    }

    public void setData(SavingInfo savingInfo) {
        this.savingInfo = savingInfo;
    }
}
