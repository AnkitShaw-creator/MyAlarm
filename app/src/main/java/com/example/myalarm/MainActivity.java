package com.example.myalarm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myalarm.settingActivity;

import com.example.myalarm.database.TaskContracts;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //private Toolbar mToolbar;
    private ListView mListView;
    private ItemListAdapter mAdapter;
    private TextView displayHeader;
    private MaterialButton addButton;
    private ImageButton deleteButton, settingButton;

    public SharedPreferences pref;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("SETTINGS",MODE_PRIVATE);
        setUpUI();
    }
    private void setUpUI(){
       //mToolbar = findViewById(R.id.toolbarMain);
        mListView = findViewById(R.id.main_list);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        settingButton = findViewById(R.id.settings_button);
        displayHeader = findViewById(R.id.display_header);

        if(!pref.contains(String.valueOf(R.string.first_name))){
            Toast.makeText(this, "Using default", Toast.LENGTH_SHORT).show();
        }
        String userName = pref.getString(String.valueOf(R.string.first_name), "Allen");
        displayHeader.setText(String.format("Hi %s", userName));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inputIntent = new Intent(MainActivity.this, DataInputActivity.class);
                startActivity(inputIntent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAll();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(MainActivity.this, settingActivity.class);
                startActivity(settingIntent);
            }
        });


        mAdapter = new ItemListAdapter(this, null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Uri contentUri = ContentUris.withAppendedId(TaskContracts.TaskEntry.CONTENT_URI, l);
            Intent detailIntent = new Intent(MainActivity.this, DetailsActivity.class);
            detailIntent.setData(contentUri);
            startActivity(detailIntent);
        });


        // initialising the loader
        LoaderManager.getInstance(this).initLoader(LOADER_ID,null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userName = pref.getString(String.valueOf(R.string.first_name), "Allen");
        displayHeader.setText(String.format("Hi %s", userName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userName = pref.getString(String.valueOf(R.string.first_name), "Allen");
        displayHeader.setText(String.format("Hi %s", userName));

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.add_item:{
                Intent inputIntent = new Intent(MainActivity.this, DataInputActivity.class);
                startActivity(inputIntent);
                break;
            }
            case R.id.delete_item:{
                deleteAll();
                break;

            }
            default:
                throw new IllegalArgumentException();
        }
        return super.onOptionsItemSelected(item);
    }
    */

    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(TaskContracts.TaskEntry.CONTENT_URI, null,null);

        if(rowsDeleted != 0){
            Toast.makeText(getApplicationContext(), "Tasks Deleted Successfully", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_LONG).show();
    }

    /** Loader to show populate the main List view with the events added to the database*/

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {TaskContracts.TaskEntry._ID,
                TaskContracts.TaskEntry.COLUMN_NAME,
                TaskContracts.TaskEntry.COLUMN_TIME,
                TaskContracts.TaskEntry.COLUMN_DATE,
                TaskContracts.TaskEntry.COLUMN_REMARK,
                TaskContracts.TaskEntry.COLUMN_IMP_LEVEL};
        return  new CursorLoader(getApplicationContext(),TaskContracts.TaskEntry.CONTENT_URI,
                projection,null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}