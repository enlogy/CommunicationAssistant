package com.example.enlogty.communicationassistant.model;

import com.example.enlogty.communicationassistant.domain.CloudImage;

import java.util.List;

/**
 * Created by enlogty on 2017/12/1.
 */

public interface ICloudImageModel {
     void getDataFromPost(onLoadListener listener);
     interface onLoadListener{
        void onComplite(List<CloudImage> data);
    }
}
