package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;

import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.model.CloudSmsModel;
import com.example.enlogty.communicationassistant.model.ICloudSms;
import com.example.enlogty.communicationassistant.view.ISms;

import java.util.List;

/**
 * Created by enlogty on 2017/12/28.
 */

public class CloudSmsPresenter extends BasePresenter<ISms>{

    public void fetchSms(Context context){
        CloudSmsModel  model = new CloudSmsModel(context);
        model.loadCloudSms(new ICloudSms.loadCloudSmsListener() {
            @Override
            public void onComplete(List<Sms> smsList) {
                ((ISms)getView()).loadSms(smsList);
            }
        });
    }
}
