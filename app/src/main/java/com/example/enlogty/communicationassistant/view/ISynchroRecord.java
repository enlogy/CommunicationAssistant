package com.example.enlogty.communicationassistant.view;

import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public interface ISynchroRecord {
    void getSynchroRecoreData(List<SynchroRecord> synchroRecordList);
}
