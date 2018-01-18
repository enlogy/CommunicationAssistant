package com.example.enlogty.communicationassistant.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by enlogty on 2018/1/7.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper{

    private final String CREATE_RESTOREPICTURE = "create table restorepicture ("
            + "id integer primary key autoincrement, "
            + "filepath text ,"
            + "filename text)";
    private final String CREATE_SYNCHRORECORD = "create table synchrorecord (" +
            "id integer primary key autoincrement ," +
            "contactsize text ," +
            "smssize text ," +
            "picturesize text ," +
            "total text ," +
            "time text )";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RESTOREPICTURE);
        db.execSQL(CREATE_SYNCHRORECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
