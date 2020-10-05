package com.moodle;

import android.provider.BaseColumns;

public class TableData {

    public TableData() {

    }

    public static abstract class TableInfo implements BaseColumns {
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_SOLUTION = "solution";
        public static final String DATABASE_NAME = "Quiz";
        public static final String TABLE_NAME = "SavedSolution";
    }
}
