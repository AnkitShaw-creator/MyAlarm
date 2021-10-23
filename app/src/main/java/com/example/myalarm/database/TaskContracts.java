package com.example.myalarm.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContracts {

    public static final String CONTENT_AUTHORITY = "com.example.myalarm";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_RECORDS = "tasks";

    public TaskContracts(){}
    public static final class TaskEntry implements BaseColumns{

        //Uri for querying the database
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_RECORDS);

        //MIME type of a list of records
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_RECORDS;
        //MIME type of a single record
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_RECORDS;

        //constants for creating a database
        public static final String TABLE_NAME = "tasks";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIME = "task_time";
        public static final String COLUMN_REMARK = "task_remarks";
        public static final String COLUMN_IMP_LEVEL = "importance_level";
        public static final String COLUMN_NOTIFICATION = "notification";
    }
}
