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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.infos.SavingInfo;

import java.util.Calendar;

public class SavingInputDialog extends DialogFragment implements View.OnClickListener {
    private int mYear=0, mMonth=0, mDay=0;
    private String pickedDate;
    private Button dateButton;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private EditText titlevalue;
    private EditText unitValue;
    private InfoListener mListener;

    public static SavingInputDialog newInstance(InfoListener mListener) {
        SavingInputDialog inputDialog = new SavingInputDialog();
        inputDialog.mListener = mListener;

        return inputDialog;
    }

    public interface InfoListener {
        void onDataInputComplete(SavingInfo savingInfo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_saving, null);

        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener mListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                dateButton.setText(Utils.toYearMonthDay(calendar));
            }
        };

        dateButton = (Button)mView.findViewById(R.id.startDateButton);
        titlevalue = (EditText)mView.findViewById(R.id.newTitleEt);
        unitValue = (EditText)mView.findViewById(R.id.newUnitValueEt);

        dateButton.setText(Utils.toYearMonthDay(calendar));

        datePickerDialog = new DatePickerDialog(this.getContext(), mListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        dateButton.setOnClickListener(this);

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

    private void setValue() {
        int value = Integer.parseInt(unitValue.getText().toString());
        String title = titlevalue.getText().toString();

        Calendar now = Calendar.getInstance();
        now.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        String startDate = Utils.dateToString(now.getTime());

        SavingInfo savingInfo = new SavingInfo(title, value, 0, startDate);
        mListener.onDataInputComplete(savingInfo);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startDateButton:
                datePickerDialog.show();
        }
    }
}