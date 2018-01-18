package com.example.enlogty.communicationassistant.model;

import android.content.Context;

import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public interface ISynchroRecordModel {
    void loadSynchroRecordDB(Context context ,SynchroRecordListener synchroRecordListener);
    public interface SynchroRecordListener{
        void onComplete(List<SynchroRecord> synchroRecordList);
    }
}
