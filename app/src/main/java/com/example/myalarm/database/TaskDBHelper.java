package com.example.myalarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 3;

    public TaskDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQLite_CREATE_RECORD_DATABASE = " CREATE TABLE " + TaskContracts.TaskEntry.TABLE_NAME + " ("
                + TaskContracts.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskContracts.TaskEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + TaskContracts.TaskEntry.COLUMN_TIME + " TEXT NOT NULL, "
                + TaskContracts.TaskEntry.COLUMN_REMARK +" TEXT, "
                + TaskContracts.TaskEntry.COLUMN_DATE +" TEXT NOT NULL, "
                + TaskContracts.TaskEntry.COLUMN_IMP_LEVEL +" INTEGER, "
                + TaskContracts.TaskEntry.COLUMN_NOTIFICATION +" INTEGER );";

        sqLiteDatabase.execSQL(SQLite_CREATE_RECORD_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //To update table name or column
        String SQLite_DELETE_TABLE= " DROP TABLE IF EXISTS "+ TaskContracts.TaskEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQLite_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
