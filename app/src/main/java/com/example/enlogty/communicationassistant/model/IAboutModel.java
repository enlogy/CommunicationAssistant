package com.example.enlogty.communicationassistant.model;

import com.example.enlogty.communicationassistant.bean.UpdateUrl;

import retrofit2.Callback;

/**
 * Created by enlogty on 2018/1/18.
 */

public interface IAboutModel {
    void updateApk(Callback<UpdateUrl> callback);
}
