package com.example.enlogty.communicationassistant.presenter;

import com.example.enlogty.communicationassistant.activity.ContactsActivity;
import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.model.ContactModel;
import com.example.enlogty.communicationassistant.model.IContactModel;
import com.example.enlogty.communicationassistant.view.IContacts;

import java.util.List;

/**
 * Created by enlogty on 2017/9/5.
 */

public class ContactsPresenter extends BasePresenter<IContacts>{
    private ContactModel model = new ContactModel();
    public void fetch(){
        model.loadContact(((ContactsActivity)getView()).getContentResolver(),new IContactModel.ContactModelListener() {
            @Override
            public void onComplete(List<Contact> contactList) {
                getView().showContactData(contactList);
            }
        });
    }
}
