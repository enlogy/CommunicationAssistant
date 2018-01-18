package com.example.enlogty.communicationassistant.model;

import com.example.enlogty.communicationassistant.domain.Sms;

import java.util.List;

/**
 * Created by enlogty on 2017/12/28.
 */

public interface ICloudSms {
    void loadCloudSms(loadCloudSmsListener listener);
    interface loadCloudSmsListener{
        void onComplete(List<Sms> smsList);
    }

}
