package com.example.myalarm;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.myalarm.database.TaskContracts;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "DetailActivity";

    private Toolbar mToolBar;
    private TextView mName, mDescription, mTime, mDate, mImpLvl;
    private static Uri currentUri;

    private static final int LOADER_ID  = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        currentUri = getIntent().getData();

        mToolBar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Task Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = findViewById(R.id.detail_item_Name);
        mDescription = findViewById(R.id.detail_description_tv);
        mDate = findViewById(R.id.detail_date_tv);
        mTime = findViewById(R.id.detail_time_tv);
        mImpLvl = findViewById(R.id.detail_imp_level);
        if(currentUri != null){
            LoaderManager.getInstance(this).initLoader(LOADER_ID,null, this);
        }

    }

    private void editItem(){
        Intent inputIntent = new Intent(DetailsActivity.this, DataInputActivity.class);
        startActivity(inputIntent);

        Log.d(TAG, "addItem: Item Added");
    }
    private void deleteItem(){
        Log.d(TAG, "addItem: Item deleted");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case android.R.id.home:{
                onBackPressed();
                finish();
            }
            case R.id.edit_item:{
                editItem();
                break;
            }
            case R.id.delete_item:{
                deleteItem();
                break;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = { TaskContracts.TaskEntry._ID,
                                TaskContracts.TaskEntry.COLUMN_NAME,
                                TaskContracts.TaskEntry.COLUMN_DATE,
                                TaskContracts.TaskEntry.COLUMN_TIME,
                                TaskContracts.TaskEntry.COLUMN_REMARK,
                                TaskContracts.TaskEntry.COLUMN_IMP_LEVEL};
        return new CursorLoader(this, currentUri, projection,null,null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;

        if(data.moveToFirst()){
            String name =  data.getString(data.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_NAME));
            String description = data.getString(data.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_REMARK));
            String time = data.getString(data.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_TIME));
            String date = data.getString(data.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_DATE));
            int impLvl = data.getInt(data.getColumnIndexOrThrow(TaskContracts.TaskEntry.COLUMN_IMP_LEVEL));

            mName.setText(name);
            mDescription.setText(description);
            mTime.setText(time);
            mDate.setText(date);
            mImpLvl.setText(String.valueOf(impLvl));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mName.setText("");
        mDescription.setText("");
        mDate.setText("");
        mTime.setText("");
        mImpLvl.setText("0");
    }
}