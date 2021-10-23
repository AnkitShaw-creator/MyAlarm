package com.example.myalarm;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myalarm.database.TaskContracts;

public class ItemListAdapter extends CursorAdapter {


    private TextView title, description, eventTiming;
    private ConstraintLayout mLayout;

    public ItemListAdapter(Context context, Cursor c){
        super(context, c, 0);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_single_layout,viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        title =view.findViewById(R.id.item_name_tv);
        description = view.findViewById(R.id.item_description);
        eventTiming = view.findViewById(R.id.event_timing_tv);
        mLayout = view.findViewById(R.id.item_layout);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_NAME));
        String remark = cursor.getString(cursor.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_REMARK));
        int imp_lvl = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_IMP_LEVEL));
        String time  = cursor.getString(cursor.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_TIME));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_DATE));

        title.setText(name);
        description.setText(remark);
        eventTiming.setText(date+" at "+time);

        switch(imp_lvl){
            case 0:
                mLayout.setBackgroundColor(Color.parseColor("#15D466"));
                break;
            case 1:
                mLayout.setBackgroundColor(Color.parseColor("#23C6C3"));
                break;
            case 2:
                mLayout.setBackgroundColor(Color.parseColor("#AC3E3E"));
        }

    }
}
