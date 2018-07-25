package com.three_eung.saemoi.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;

import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.infos.HousekeepInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by CH on 2018-02-21.
 */

public class InputDialog extends DialogFragment implements View.OnClickListener {
    private View mView;
    private Calendar calendar;
    private Button dateButton;
    private DatePickerDialog datePickerDialog;
    private Spinner categorySpinner;
    private EditText valueText, memoText;
    private InfoListener mListener;
    private DatePickerDialog.OnDateSetListener dateListener;
    private ArrayAdapter<String> mAdapter;
    private TableRow mInclude;
    private RadioGroup mRadioGroup;
    private CheckBox mBudgetChk;
    private HashMap<String, Integer> inCate, exCate;
    private ArrayList<String> mInCate, mExCate;

    public static InputDialog newInstance(InfoListener mListener) {
        InputDialog inputDialog = new InputDialog();
        inputDialog.mListener = mListener;

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
        mView = inflater.inflate(R.layout.dialog_housekeep, null);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getArguments().getLong("date"));

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                dateButton.setText(Utils.toYearMonthDay(calendar));
            }
        };

        initView();

        mRadioGroup.check(R.id.dialog_housekeep_in);

        dateButton.setText(Utils.toYearMonthDay(calendar));

        builder.setView(mView)
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
        categorySpinner = (Spinner)mView.findViewById(R.id.dialog_housekeep_category);
        dateButton = (Button)mView.findViewById(R.id.dialog_housekeep_datebtn);
        valueText = (EditText)mView.findViewById(R.id.dialog_housekeep_value);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.dialog_housekeep_group);
        mBudgetChk = (CheckBox) mView.findViewById(R.id.dialog_housekeep_check);
        memoText = (EditText) mView.findViewById(R.id.dialog_housekeep_memo);
        mInclude = (TableRow) mView.findViewById(R.id.dialog_housekeep_include);

        mRadioGroup.check(R.id.dialog_housekeep_in);

        inCate = ((InitApp)(getActivity().getApplication())).getInCate();
        exCate = ((InitApp)(getActivity().getApplication())).getExCate();

        mInCate = new ArrayList<>();
        mExCate = new ArrayList<>();

        for(String key : inCate.keySet()) {
            mInCate.add(key);
        }

        for(String key : exCate.keySet()) {
            mExCate.add(key);
        }

        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, (ArrayList<String>)mInCate.clone());
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(mAdapter);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.dialog_housekeep_in:
                        mAdapter.clear();
                        mAdapter.addAll((ArrayList<String>)mInCate.clone());
                        mAdapter.notifyDataSetChanged();
                        mInclude.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.dialog_housekeep_out:
                        mAdapter.clear();
                        mAdapter.addAll((ArrayList<String>)mExCate.clone());
                        mAdapter.notifyDataSetChanged();
                        mInclude.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        datePickerDialog = new DatePickerDialog(this.getContext(), dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        dateButton.setOnClickListener(this);
    }

    private void setValue() {
        boolean isIncome = mRadioGroup.getCheckedRadioButtonId() == R.id.dialog_housekeep_in;
        boolean isCheck = mBudgetChk.isChecked();
        String category = categorySpinner.getSelectedItem().toString();
        String memo = memoText.getText().toString();
        int value = Integer.parseInt(valueText.getText().toString());

        Calendar now = Calendar.getInstance();
        now.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        if(isIncome) {
            isCheck = false;
        }

        String toDate = Utils.dateToString(now.getTime());
        HousekeepInfo housekeepInfo = new HousekeepInfo(category, value, isIncome, isCheck, memo, toDate);
        mListener.onDataInputComplete(housekeepInfo);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.dialog_housekeep_datebtn:
                datePickerDialog.show();
        }
    }
}