package com.example.myalarm;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.example.myalarm.notifications.AlertReceiver.CHANNEL_ID;
import static com.example.myalarm.notifications.AlertReceiver.CHANNEL_NAME;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myalarm.database.TaskContracts;
import com.example.myalarm.inputFraments.DateInputFragment;
import com.example.myalarm.inputFraments.DateViewModel;
import com.example.myalarm.inputFraments.TimeInputFragment;
import com.example.myalarm.inputFraments.TimeViewModel;
import com.example.myalarm.notifications.AlertReceiver;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class DataInputActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mDateButton;
    private Button mTimeButton;
    private TextView showTime, showDate;
    private EditText remarks;
    private TextInputEditText mEventName;
    private SwitchMaterial notificationSwitch;
    private RadioButton mHigh, mMed, mLow;

    private DateViewModel dateViewModel;
    private TimeViewModel timeViewModel;

    public SharedPreferences pref;

    private static final String TAG = "DataInputActivity";
    private Uri currentUri;
    private int priority = -1;
    private String date = "", time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        mToolbar = findViewById(R.id.data_input_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input date and time");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUri = getIntent().getData();

        mEventName = findViewById(R.id.user_name_et);
        mDateButton = findViewById(R.id.button_date);
        mTimeButton = findViewById(R.id.button_time);
        showDate = findViewById(R.id.date_tv);
        showTime = findViewById(R.id.time_tv);
        remarks = findViewById(R.id.remark_et);
        notificationSwitch = findViewById(R.id.switch_notification);
        mHigh = findViewById(R.id.rb_high);
        mMed = findViewById(R.id.rb_med);
        mLow = findViewById(R.id.rb_low);

        pref = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        int notify = pref.getInt(String.valueOf(R.string.notification), 1);
        notificationSwitch.setVisibility(notify);


        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        dateViewModel = new ViewModelProvider(this).get(DateViewModel.class);
        
        mDateButton.setOnClickListener(view -> showDateFragment());
        mTimeButton.setOnClickListener(view -> showTimeFragment());

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: Switch turned:"+isChecked);

            }
        });

        createChannel();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_high: {
                if (checked)
                    priority = 2;
                    break;
            }
            case R.id.rb_med: {
                if (checked)
                    priority = 1;
                    break;
            }
            case R.id.rb_low: {
                if (checked)
                    priority = 0;
                    break;
            }

        }
        Log.d(TAG, "onRadioButtonClicked: Imp_level: "+priority);
    }


    private void showTimeFragment() {
        DialogFragment timeFragment = new TimeInputFragment();
        timeFragment.show(getSupportFragmentManager(),"timePicker");

        timeViewModel.getSelectedItem().observe(this, string->{
            time = string;
            showTime.setText(time);
        });
    }

    private void showDateFragment() {
        DialogFragment dateFragment = new DateInputFragment();
        dateFragment.show(getSupportFragmentManager(), "datePicker");

        dateViewModel.getSelectedItem().observe(this, string ->{
            date = string;
            showDate.setText(date);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_input, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_to_db:{
                addDB();
                finish();
                return true;
            }
            case android.R.id.home:{
                NavUtils.navigateUpFromSameTask(DataInputActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDB() {
        String name = String.valueOf(mEventName.getText());
        String time = String.valueOf(showTime.getText());
        String date = String.valueOf(showDate.getText());
        String remark = String.valueOf(remarks.getText());
        boolean notification = notificationSwitch.isChecked();
        if(validateData(name, time, date, priority)){
            ContentValues values = new ContentValues();
            values.put(TaskContracts.TaskEntry.COLUMN_NAME, name);
            values.put(TaskContracts.TaskEntry.COLUMN_TIME, time);
            values.put(TaskContracts.TaskEntry.COLUMN_DATE, date);
            values.put(TaskContracts.TaskEntry.COLUMN_REMARK, remark);
            values.put(TaskContracts.TaskEntry.COLUMN_IMP_LEVEL, priority);
            if(notification) {
                values.put(TaskContracts.TaskEntry.COLUMN_NOTIFICATION, 1);
                startAlarm();
            }
            else
                values.put(TaskContracts.TaskEntry.COLUMN_NOTIFICATION, 0);

            if(currentUri != null){
                // For updating the content
            }
            else{
                Uri insertUri = getContentResolver().insert(TaskContracts.TaskEntry.CONTENT_URI, values);

                if ((insertUri != null)) {
                    Log.d(TAG, "addDB:  Data added successfully:" + insertUri);
                    Toast.makeText(getApplicationContext(), "Insertion Successful", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG, "addDB: Data Insertion failed");
                    Toast.makeText(getApplicationContext(), "Insertion failed", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else{
            // Display error message
        }
    }

    private boolean validateData(String name, String time, String date, int priority) {

        if(name.equals("") || time.equals("") || date.equals("") || priority == -1){
            return false;
        }
        else
            return true;
    }
    private void startAlarm(){
         /* Need to create something to cancel alarm as well*/
        StringTokenizer stDate = new StringTokenizer(date, "/");
        StringTokenizer stTime = new StringTokenizer(time, ":");
        ArrayList<Integer> a = new ArrayList<>();
        while(stDate.hasMoreTokens()){
            a.add(Integer.valueOf(stDate.nextToken())); // Date in 0, 1, 2
        }
        while(stTime.hasMoreTokens()){
            a.add(Integer.valueOf(stTime.nextToken())); // Time in 3, 4
        }
        a.set(1, a.get(1)-1);

        Log.d(TAG, "startAlarm: Time" + a.get(0) +" "+a.get(1)+" "+a.get(2)+" "+a.get(3)+" "+a.get(4));
        Calendar c = Calendar.getInstance();
        c.set(a.get(0), a.get(1), a.get(2), a.get(3), a.get(4), 0); // setting the calender instance with the time;
        Log.d(TAG, "startAlarm: alarm time: "+ c.getTimeInMillis());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "startAlarm: Alarm Set");
    }
    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
    private void createChannel() {
        NotificationChannel channel    = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.design_default_color_primary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setDescription("You have an event now!!!");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        Log.d(TAG, "createChannel: Channel created");
    }
}