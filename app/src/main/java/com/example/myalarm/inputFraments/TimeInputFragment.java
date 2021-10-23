package com.example.myalarm.inputFraments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimeInputFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimeViewModel timeViewModel;

    public TimeInputFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),this, hours,minutes,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        timeViewModel = new ViewModelProvider(requireActivity()).get(TimeViewModel.class);
        String time  = hourOfDay+":"+minute;
        timeViewModel.selectItem(time);

    }
}