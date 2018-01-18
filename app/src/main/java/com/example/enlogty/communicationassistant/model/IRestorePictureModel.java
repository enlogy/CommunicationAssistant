package com.example.enlogty.communicationassistant.model;

import android.content.Context;

import com.example.enlogty.communicationassistant.sqlite.bean.RestorePicture;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public interface IRestorePictureModel {
    void loadRestorePictureDB(Context context,RestorePictureListener restorePictureListener);
    public interface RestorePictureListener{
        void onComplete(List<RestorePicture> restorePictureList);
    }
}
