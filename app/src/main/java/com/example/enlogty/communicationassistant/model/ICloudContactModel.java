package com.example.enlogty.communicationassistant.model;

import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.domain.DataContact;

import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public interface ICloudContactModel {
    void loadCloudContact(CloudContactListener listener ,String localNumber);
    public interface CloudContactListener{
        void onComplete(List<DataContact> contactList);
    }
}
