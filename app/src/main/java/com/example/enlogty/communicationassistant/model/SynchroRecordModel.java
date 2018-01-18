package com.example.enlogty.communicationassistant.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.enlogty.communicationassistant.sqlite.MySQLiteOpenHelper;
import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class SynchroRecordModel implements ISynchroRecordModel{
    @Override
    public void loadSynchroRecordDB(Context context,SynchroRecordListener synchroRecordListener) {
        //SynchroRecordDao synchroRecordDao = MyApplication.getInstances().getDaoSession().getSynchroRecordDao();
        List<SynchroRecord> synchroRecords = new ArrayList<>();
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context,"history.db",null,1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("synchrorecord", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                SynchroRecord sr = new SynchroRecord();
                String contactSize = cursor.getString(cursor.getColumnIndex("contactsize"));
                String smsSize = cursor.getString(cursor.getColumnIndex("smssize"));
                String pictureSize = cursor.getString(cursor.getColumnIndex("picturesize"));
                String total = cursor.getString(cursor.getColumnIndex("total"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                sr.setContactSize(contactSize);
                sr.setSmsSize(smsSize);
                sr.setPictureSize(pictureSize);
                sr.setTime(time);
                sr.setTotal(total);
                synchroRecords.add(sr);
            }while (cursor.moveToNext());
        }
        cursor.close();
        synchroRecordListener.onComplete(synchroRecords);
    }
}
