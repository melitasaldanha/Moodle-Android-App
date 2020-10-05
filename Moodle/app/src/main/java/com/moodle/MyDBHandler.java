package com.moodle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import  com.moodle.TableData.TableInfo;

public class MyDBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public String CREATE_QUERY = "CREATE TABLE " + TableInfo.TABLE_NAME + "("
            + TableInfo.COLUMN_QUESTION + " TEXT PRIMARY KEY,"
            + TableInfo.COLUMN_SOLUTION + " TEXT"
            + ");";

    public MyDBHandler(Context context) {
        super(context, TableInfo.DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("MyDBHandler","Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_QUERY);
        Log.d("MyDBHandler","Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void add(MyDBHandler dbHandler, SavedSolution savedSolution) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableInfo.COLUMN_QUESTION, savedSolution.getQuestion());
        values.put(TableInfo.COLUMN_SOLUTION, savedSolution.getSolution());
        long result = db.insert(TableInfo.TABLE_NAME, null, values);
        Log.d("MyDBHandler",
                "Solution added: "+savedSolution.getQuestion() + " - " + savedSolution.getSolution());
    }

    public String retrieve(MyDBHandler dbHandler, String question) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String[] columns = {TableInfo.COLUMN_QUESTION, TableInfo.COLUMN_SOLUTION};
        Cursor cursor = db.query(TableInfo.TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();

        boolean flag = false;
        String solution = "";

        if(cursor.getCount()>0) {
            do {
                if (question.equals(cursor.getString(0))) {
                    flag = true;
                    solution = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        Log.d("MyDBHandler" , "Data Retrieved: "+solution);
        return solution;
    }

    public void update(MyDBHandler dbHandler, SavedSolution savedSolution) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.execSQL("UPDATE " + TableInfo.TABLE_NAME
                + " SET " + TableInfo.COLUMN_SOLUTION + "='" + savedSolution.getSolution() + "'"
                + " WHERE " + TableInfo.COLUMN_QUESTION + "='" + savedSolution.getQuestion() + "'"
                + ";");
        db.close();
        Log.d("MyDBHandler",
                "Updated Solution: "+savedSolution.getQuestion() + " - " + savedSolution.getSolution());
    }

    public void delete(MyDBHandler dbHandler, String question) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String selection = TableInfo.COLUMN_QUESTION + " LIKE ?";
        String[] args = {question};
        db.delete(TableInfo.TABLE_NAME, selection, args);

        Log.d("MyDBHandler", "Deleted: " + question);
    }
}
