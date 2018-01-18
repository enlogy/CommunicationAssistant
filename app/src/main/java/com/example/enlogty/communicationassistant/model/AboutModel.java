package com.example.enlogty.communicationassistant.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.api.UpdateApkApi;
import com.example.enlogty.communicationassistant.bean.UpdateUrl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by enlogty on 2018/1/18.
 */

public class AboutModel implements IAboutModel{
    public AboutModel(Context context) {
        this.context = context;
    }

    private Context context;
    @Override
    public void updateApk(Callback<UpdateUrl> callback) {
        try {
        PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
        String server_ip = context.getResources().getString(R.string.server_ip);
        new Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                    .create(UpdateApkApi.class)
                    .getUpdateApkUrl(versionCode).enqueue(callback);
            Log.d("更新apk","model--end");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
