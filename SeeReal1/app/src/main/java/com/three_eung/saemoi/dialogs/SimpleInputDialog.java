package com.three_eung.saemoi.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.databinding.DialogSimpleBinding;
import com.three_eung.saemoi.infos.HousekeepInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SimpleInputDialog extends DialogFragment implements View.OnClickListener {
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private InfoListener mListener;
    private DatePickerDialog.OnDateSetListener dateListener;
    private ArrayAdapter<String> mAdapter;
    private HashMap<String, Integer> inCate, exCate;
    private ArrayList<String> mInCate, mExCate;
    private boolean isIncome;
    private DialogSimpleBinding mBinding;

    public static SimpleInputDialog newInstance(InfoListener mListener, boolean isIncome) {
        SimpleInputDialog inputDialog = new SimpleInputDialog();
        inputDialog.mListener = mListener;
        inputDialog.isIncome = isIncome;

        return inputDialog;
    }

    public interface InfoListener {
        void onDataInputComplete(HousekeepInfo housekeepInfo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_simple, null, false);

        calendar = Calendar.getInstance();

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                mBinding.dialogSimpleDatebtn.setText(Utils.toYearMonthDay(calendar));
            }
        };

        initView();

        mBinding.dialogSimpleDatebtn.setText(Utils.toYearMonthDay(calendar));

        builder.setView(mBinding.getRoot())
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setValue();
                    }
                }).setNegativeButton("취소", null);

        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void initView() {
        if(isIncome) {
            inCate = ((InitApp) (getActivity().getApplication())).getInCate();
            mInCate = new ArrayList<>();

            for (String key : inCate.keySet()) {
                mInCate.add(key);
            }
            mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, (ArrayList<String>) mInCate.clone());
            mBinding.dialogSimpleInclude.setVisibility(View.INVISIBLE);
            mBinding.dialogSimpleTitle.setText("간편 수입 입력");
        } else {
            exCate = ((InitApp) (getActivity().getApplication())).getExCate();
            mExCate = new ArrayList<>();

            for (String key : exCate.keySet()) {
                mExCate.add(key);
            }
            mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, (ArrayList<String>) mExCate.clone());
            mBinding.dialogSimpleInclude.setVisibility(View.VISIBLE);
            mBinding.dialogSimpleTitle.setText("간편 지출 입력");
        }
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.dialogSimpleCategory.setAdapter(mAdapter);

        datePickerDialog = new DatePickerDialog(this.getContext(), dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        mBinding.dialogSimpleDatebtn.setOnClickListener(this);
    }

    private void setValue() {
        boolean isCheck = mBinding.dialogSimpleCheck.isChecked();
        String category = mBinding.dialogSimpleCategory.getSelectedItem().toString();
        String memo = mBinding.dialogSimpleMemo.getText().toString();
        int value = Integer.parseInt(mBinding.dialogSimpleValue.getText().toString());

        Calendar now = Calendar.getInstance();
        now.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        if (isIncome) {
            isCheck = false;
        }

        String toDate = Utils.dateToString(now.getTime());
        HousekeepInfo housekeepInfo = new HousekeepInfo(category, value, isIncome, isCheck, memo, toDate);
        mListener.onDataInputComplete(housekeepInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_simple_datebtn:
                datePickerDialog.show();
        }
    }
}
