package com.example.enlogty.communicationassistant.model;

import android.content.ContentResolver;

import com.example.enlogty.communicationassistant.utils.ContactsUtil;

/**
 * Created by enlogty on 2017/9/5.
 */

public class ContactModel implements IContactModel{

    @Override
    public void loadContact(final ContentResolver contentResolver,final ContactModelListener listener) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        listener.onComplete(new ContactsUtil().getContactsData(contentResolver));
//                    }
//                }).start();


        //访问数据,监听回调
        //listener.onComplete();
    }
}
