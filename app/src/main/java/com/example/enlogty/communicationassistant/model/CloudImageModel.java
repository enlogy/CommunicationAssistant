package com.example.enlogty.communicationassistant.model;

import android.content.Context;

import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.utils.CloudImageUtil;

import java.util.List;

/**
 * Created by enlogty on 2017/12/1.
 */

public class CloudImageModel implements ICloudImageModel{
    public CloudImageModel(Context context) {
        this.context = context;
    }

    private Context context;
    @Override
    public void getDataFromPost(onLoadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CloudImage> data = new CloudImageUtil(context).getCloudImageData(User.localNumber);
                listener.onComplite(data);
            }
        }).start();


    }

}
