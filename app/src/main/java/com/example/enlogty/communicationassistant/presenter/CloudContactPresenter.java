package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;

import com.example.enlogty.communicationassistant.activity.CloudContactActivity;
import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.model.CloudContactModel;
import com.example.enlogty.communicationassistant.model.ICloudContactModel;
import com.example.enlogty.communicationassistant.view.ICloudContact;

import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudContactPresenter extends BasePresenter<ICloudContact>{

    public void fetchCloudContact(Context context,String localNumber){
        CloudContactModel model = new CloudContactModel(context);
        model.loadCloudContact(new ICloudContactModel.CloudContactListener() {
            @Override
            public void onComplete(List<DataContact> contactList) {
                ((CloudContactActivity)getView()).showCloudContact(contactList);
            }
        },localNumber);
    }
}
