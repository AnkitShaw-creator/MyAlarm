package com.example.myalarm.inputFraments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.myalarm.R;

import java.util.Calendar;

public class DateInputFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DateViewModel viewModel;

    public DateInputFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int weekDay = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        Log.d("DATE", "onCreateDialog: "+weekDay);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        viewModel= new ViewModelProvider(requireActivity()).get(DateViewModel.class);
        String date = year+"/"+(month+1)+"/"+dayOfMonth;
        viewModel.selectItem(date);

    }
}