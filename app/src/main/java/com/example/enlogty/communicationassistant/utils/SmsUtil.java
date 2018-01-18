package com.example.enlogty.communicationassistant.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.enlogty.communicationassistant.BuildConfig;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.api.SmsApi;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.rx.RxBus;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by enlogty on 2017/9/30.
 */

public class SmsUtil {
    public SmsUtil(Context context) {
        this.context = context;
    }

    private Context context;
    private int count;
    public List<Sms> findRemoteDataAll(){
        List<Sms> list = new ArrayList<>();
        try {
            String url = context.getResources().getString(R.string.server_ip);
            //String url = "http://192.168.1.112:9000/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SmsApi smsApi = retrofit.create(SmsApi.class);
            Response<List<Sms>> response = smsApi.findAll(User.localNumber).execute();
            List<Sms> smsList = response.body();
            int count = smsList.size();
            for (int i=0;i<count;i++){
                if (smsList.get(i).getLocalNumber().equals(User.localNumber)){
                    list.add(smsList.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertSmsToRemoteDB(String localNumber, String address, long date, int type, String body) {

        String url = context.getResources().getString(R.string.server_ip);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SmsApi smsApi = retrofit.create(SmsApi.class);
        try {
            smsApi.insertSmsToRemoteDB(localNumber,address,date,type,body).execute();
            SyncTask.getInstance().SmsUploadedCount++;
            RxBus.getInstance().post(SyncTask.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Sms> querrySmsToList(ContentResolver resolver){
        ArrayList<Sms> list = new ArrayList<>();
        list.clear();
        Sms sms;
        Cursor cursor = resolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null, null);
        while (cursor.moveToNext()){
            sms = new Sms();
            sms.setAddress(cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)));
            sms.setDate(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
            sms.setType(cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)));
            sms.setBody(cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY)));
            list.add(sms);
        }
        cursor.close();
        //Log.d("本地短息",list.get(1).toString());
        return list;
    }
    public void writeToSmsDatabase(final ContentResolver resolver,List<Sms> smsList ,final String address, final int type, final long date, final String body, @Nullable recoverSmsLinstener listener){

                if (smsList != null){
                    for (Sms sms : smsList){
                        if (date == sms.getDate()){
                            if (listener != null){
                                count++;
                                listener.onSuccess(count);
                            }
                            return;
                        }
                    }
                }
                try {
                    Uri url=Uri.parse("content://sms/");
                    ContentValues values=new ContentValues();
                    values.put("address", address);
                    values.put("type", type);
                    values.put("date", date);
                    values.put("body", body);
                    resolver.insert(url, values);
                    if (listener == null){
                        SyncTask.getInstance().SmsDownloadedCount++;
                        RxBus.getInstance().post(SyncTask.getInstance());
                    }else {
                        count++;
                        listener.onSuccess(count);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
    public interface recoverSmsLinstener{
        void onSuccess(int count);
    }
}
