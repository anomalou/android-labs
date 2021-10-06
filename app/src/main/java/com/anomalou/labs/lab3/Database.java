package com.anomalou.labs.lab3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static final String STUDENT_TABLE = "STUDENT";
    public static final String KEY = "ID";
    public static final String FIO_COLUMN = "FIO";
    public static final String NAME_COLUMN = "NAME";
    public static final String LNAME_COLUMN = "LNAME";
    public static final String PATRONYMIC = "PATRONYMIC";
    public static final String TIME_COLUMN = "ADD_TIME";

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format(
                "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT);",
                STUDENT_TABLE, KEY, FIO_COLUMN, TIME_COLUMN));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", STUDENT_TABLE));
        if(newVersion == 2){
            sqLiteDatabase.execSQL(String.format(
                    "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                    STUDENT_TABLE, KEY, NAME_COLUMN, LNAME_COLUMN, PATRONYMIC, TIME_COLUMN));
        }else{
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", STUDENT_TABLE));
        onCreate(db);
    }
}
