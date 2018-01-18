package com.example.enlogty.communicationassistant.model;

import android.content.Context;

import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.utils.CloudContactsUtil;

import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudContactModel implements ICloudContactModel{
    public CloudContactModel(Context context) {
        this.context = context;
    }

    private Context context;
    @Override
    public void loadCloudContact(final CloudContactListener listener ,String localNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DataContact> dataContacts = new CloudContactsUtil(context).getRemoteData(localNumber);
                listener.onComplete(dataContacts);
            }
        }).start();
        //访问得到数据

    }
}
