package com.example.enlogty.communicationassistant.view;

import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.domain.DataContact;

import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public interface ICloudContact {
    void showCloudContact(List<DataContact> contacts);
}
