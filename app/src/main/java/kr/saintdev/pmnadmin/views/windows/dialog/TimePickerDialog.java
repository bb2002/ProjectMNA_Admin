package kr.saintdev.pmnadmin.views.windows.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import kr.saintdev.pmnadmin.R;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-31
 */

public class TimePickerDialog extends Dialog {
    TimePicker picker = null;
    Button pickerOk = null;
    int id = 0;

    public TimePickerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);

        this.picker = findViewById(R.id.dialog_timepicker);
        this.pickerOk = findViewById(R.id.dialog_time_select);

        this.pickerOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public String getPickedTime() {
        return String.format(Locale.KOREA, "%02d:%02d", this.picker.getCurrentHour(), this.picker.getCurrentMinute());
    }

    public int getCurrectId() {
        return this.id;
    }

    public void setCurrectId(int id) {
        this.id = id;
    }
}
