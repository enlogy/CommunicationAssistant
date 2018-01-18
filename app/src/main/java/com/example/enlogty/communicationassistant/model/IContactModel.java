package com.example.enlogty.communicationassistant.model;

import android.content.ContentResolver;

import com.example.enlogty.communicationassistant.bean.Contact;

import java.util.List;

/**
 * Created by enlogty on 2017/9/5.
 */

public interface IContactModel {
    void loadContact(ContentResolver contentResolver, ContactModelListener listener);
    public interface ContactModelListener{
        void onComplete(List<Contact> contactList);
    }
}
