package com.example.enlogty.communicationassistant.api;

import com.example.enlogty.communicationassistant.bean.UpdateUrl;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by enlogty on 2018/1/18.
 */

public interface UpdateApkApi {
    @GET(value = "cloud/updateApk")
    Call<UpdateUrl> getUpdateApkUrl(@Query(value = "versionCode") int versionCode);
}
