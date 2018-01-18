package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;

import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.model.CloudImageModel;
import com.example.enlogty.communicationassistant.model.ICloudImageModel;
import com.example.enlogty.communicationassistant.view.ICloudImage;

import java.util.List;

/**
 * Created by enlogty on 2017/11/30.
 */

public class CloudImagePresenter extends BasePresenter<ICloudImage>{

    public void fetch(Context context){
        CloudImageModel model = new CloudImageModel(context);
        model.getDataFromPost(new ICloudImageModel.onLoadListener() {
            @Override
            public void onComplite(List<CloudImage> data) {
                getView().getDataFromRemoteService(data);
            }
        });
    }
}
