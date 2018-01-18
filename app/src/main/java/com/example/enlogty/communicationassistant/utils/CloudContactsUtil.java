package com.example.enlogty.communicationassistant.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.domain.User;
import com.example.enlogty.communicationassistant.rx.RxBus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudContactsUtil {
    public CloudContactsUtil(Context context) {
        this.context = context;
    }

    private Context context;
    /**
     * 服务已存数据
     */
    public  List<DataContact> getRemoteData(String localNumber){
        List<DataContact> contactList = new ArrayList<>();
        contactList.clear();
        try {
            OkHttpClient client = new OkHttpClient();
            String server_ip = context.getResources().getString(R.string.server_ip);
            Request request = new Request.Builder().url(server_ip+"cloud/downloadContactData").get().build();
            Response response = client.newCall(request).execute();
            String jsonbody = response.body().string();
            if (response.isSuccessful()) {
                response.close();
                Gson gson = new Gson();
                List<DataContact> contacts = gson.fromJson(jsonbody, new TypeToken<List<DataContact>>() {
                }.getType());
                if (contacts!=null){
                    for (DataContact contact : contacts){
                        if (contact.getLocalnumber().equals(localNumber)){
                            contactList.add(contact);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("网络联系人",contactList.toString());
        return contactList;
    }

    public  void uploadData(ContentResolver contentResolver ,String name,String number, String localnNumber,List<DataContact> remoteData){
        List<DataContact> localcontactList = new ArrayList<>();
        localcontactList.clear();
        if (remoteData !=null){
            for (DataContact contact2 : remoteData){
                if (contact2.getPhonenumber().equals(number)&&contact2.getLocalnumber().equals(localnNumber)){
                    return ;
                }
            }
        }
        DataContact contact = new DataContact();
            contact.setLocalnumber(localnNumber);
            contact.setName(name);
            contact.setPhonenumber(number);
            localcontactList.add(contact);

        Gson gson = new Gson();
        String json = gson.toJson(localcontactList);
        //上传数据
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("data",json)
                    .build();
            String server_ip = context.getResources().getString(R.string.server_ip);
            Request request = new Request.Builder().url(server_ip+"cloud/uploadContactData").post(requestBody).build();
            Response response = client.newCall(request).execute();
            SyncTask.getInstance().ContactUploadedCount++;
            RxBus.getInstance().post(SyncTask.getInstance());

            String jsonbody = response.body().string();
            if (response.isSuccessful()) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
