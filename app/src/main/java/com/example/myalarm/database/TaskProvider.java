package com.example.myalarm.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TaskProvider extends ContentProvider {

    private TaskDBHelper mDbHelper;
    private static final int TASKS = 100;       // constant for querying entire database
    private static final int TASKS_ID = 101;    // constant fro querying a single task

    private static final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        // uri for matching entire database
        mMatcher.addURI(TaskContracts.CONTENT_AUTHORITY, TaskContracts.PATH_RECORDS, TASKS);
        // uri for matching single task
        mMatcher.addURI(TaskContracts.CONTENT_AUTHORITY, TaskContracts.PATH_RECORDS+"/#", TASKS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TaskDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = mMatcher.match(uri);
        switch(match){
            case TASKS:{
                cursor = mDatabase.query(TaskContracts.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TASKS_ID:{
                selection = TaskContracts.TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                /* SQLite implementation (SELECT database FROM _TABLE_NAME_ WHERE SELECTION =
                SELECTION_ARGS[] SORT BY sortOrder)*/
                cursor = mDatabase.query(TaskContracts.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new IllegalArgumentException("Query not found"+match);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = mMatcher.match(uri);
        switch(match){
            case TASKS: return TaskContracts.TaskEntry.CONTENT_LIST_TYPE;
            case TASKS_ID: return TaskContracts.TaskEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalArgumentException("Uri match not found for: "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = mMatcher.match(uri);
        switch(match){
            case TASKS:{
                return insertData(uri, contentValues);
            }
            default: throw new IllegalArgumentException("Task not supported");
        }
    }

    private Uri insertData(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long resultID =  database.insert(TaskContracts.TaskEntry.TABLE_NAME,
                null,
                contentValues);

        if(resultID != -1)
            Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Insertion failed", Toast.LENGTH_SHORT).show();

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, resultID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = mMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match){
            case TASKS: {
                rowsDeleted = database.delete(TaskContracts.TaskEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case TASKS_ID:{
                selection = TaskContracts.TaskEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TaskContracts.TaskEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            default: throw new IllegalArgumentException("Uri match not found");
        }

        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = mMatcher.match(uri);

        switch(match){
            case TASKS: return updateTask(uri, contentValues, selection, selectionArgs);
            case TASKS_ID:{
                selection = TaskContracts.TaskEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri, contentValues, selection, selectionArgs);
            }
            default: throw new IllegalArgumentException("Update failed for uri: "+uri);
        }
    }

    private int updateTask(Uri uri, ContentValues contentValues, String selection,
                           String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TaskContracts.TaskEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);

        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
