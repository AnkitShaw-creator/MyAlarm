package com.example.myalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class settingActivity extends AppCompatActivity {

    private MaterialToolbar mToolbar;
    private EditText fName, lName;
    private SwitchMaterial notificationPref;
    public SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fName = findViewById(R.id.user_fname_et);
        lName = findViewById(R.id.user_lname_et);
        notificationPref = findViewById(R.id.notification_pref);
        notificationPref.setChecked(true);


        preferences =  getSharedPreferences("SETTINGS", MODE_PRIVATE);
        editor = preferences.edit();

        fName.setText(preferences.getString(String.valueOf(R.string.first_name), ""));
        lName.setText(preferences.getString(String.valueOf(R.string.last_name), ""));
        int checked = preferences.getInt(String.valueOf(R.string.notification), 1);
        if(checked==1){
            notificationPref.setChecked(true);
        }
        else{
            notificationPref.setChecked(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_changes:{
                saveChanges();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChanges() {
        String firstName = String.valueOf(fName.getText());
        String lastName = String.valueOf(lName.getText());
        int notificationChecked;
        if(notificationPref.isChecked()){
            notificationChecked = 1;
        }
        else{
            notificationChecked = 0;
        }

        editor.putString(String.valueOf(R.string.first_name), firstName);
        editor.putString(String.valueOf(R.string.last_name), lastName);
        editor.putInt(String.valueOf(R.string.notification), notificationChecked);
        editor.commit();
        Toast.makeText(this, "Preferences Updated successfully", Toast.LENGTH_SHORT).show();
    }
}