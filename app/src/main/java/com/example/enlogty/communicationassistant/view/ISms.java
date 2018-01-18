package com.example.enlogty.communicationassistant.view;

import com.example.enlogty.communicationassistant.domain.Sms;

import java.util.List;

/**
 * Created by enlogty on 2017/12/28.
 */

public interface ISms {
    void loadSms(List<Sms> smsList);
}
