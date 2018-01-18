package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;

import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.fragment.RestorePictureFragment;
import com.example.enlogty.communicationassistant.sqlite.bean.RestorePicture;
import com.example.enlogty.communicationassistant.model.IRestorePictureModel;
import com.example.enlogty.communicationassistant.model.RestorePictureModel;
import com.example.enlogty.communicationassistant.view.IRestorePicture;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class RestorePicturePresenter extends BasePresenter<IRestorePicture>{
    private RestorePictureModel model = new RestorePictureModel();
    public void fetch(Context context){
        model.loadRestorePictureDB(context, new IRestorePictureModel.RestorePictureListener() {
            @Override
            public void onComplete(List<RestorePicture> restorePictureList) {
                ((RestorePictureFragment)getView()).getRestorePictureData(restorePictureList);
            }
        });
    }
}
