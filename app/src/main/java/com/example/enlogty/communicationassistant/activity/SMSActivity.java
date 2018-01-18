package com.example.enlogty.communicationassistant.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.enlogty.communicationassistant.R;

/**
 * Created by enlogty on 2017/9/30.
 */

public class SMSActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null, null);
        while (cursor.moveToNext()){
            Log.d("SMSActivity",cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)));
            Log.d("SMSActivity",""+cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
            Log.d("SMSActivity",""+cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)));
            Log.d("SMSActivity",cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY)));
        }
        cursor.close();
        new Thread(){
            public void run(){
                try {
                    ContentResolver resolver=getContentResolver();
                    Uri url=Uri.parse("content://sms/");
                    ContentValues values=new ContentValues();
                    values.put("address", "13416331234");
                    values.put("type", 2);
                    values.put("date", System.currentTimeMillis());
                    values.put("body", "我是123456的，快开门");
                    resolver.insert(url, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }
}
