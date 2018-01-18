package com.example.enlogty.communicationassistant.model;

import android.content.Context;

import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.utils.SmsUtil;

import java.util.List;

/**
 * Created by enlogty on 2017/12/28.
 */

public class CloudSmsModel implements ICloudSms{
    public CloudSmsModel(Context context) {
        this.context = context;
    }

    private Context context;
    @Override
    public void loadCloudSms(loadCloudSmsListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmsUtil smsUtil = new SmsUtil(context);
                List<Sms> remoteDataAll = smsUtil.findRemoteDataAll();
                listener.onComplete(remoteDataAll);
            }
        }).start();
    }
}
