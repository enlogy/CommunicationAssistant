package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;
import android.util.Log;

import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.bean.UpdateUrl;
import com.example.enlogty.communicationassistant.model.AboutModel;
import com.example.enlogty.communicationassistant.view.IAbout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by enlogty on 2018/1/18.
 */

public class AboutPresener extends BasePresenter<IAbout>{

    public void fetchUpdateApk(Context context){
        AboutModel model = new AboutModel(context);
        model.updateApk(new Callback<UpdateUrl>() {
            @Override
            public void onResponse(Call<UpdateUrl> call, Response<UpdateUrl> response) {
                UpdateUrl data = response.body();
                getView().fetchUpdateApk(data.getBody());
                Log.d("更新apk-url",data.getBody());
            }

            @Override
            public void onFailure(Call<UpdateUrl> call, Throwable t) {

            }
        });
    }
}
