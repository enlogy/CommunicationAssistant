package com.example.enlogty.communicationassistant.presenter;

import android.content.Context;

import com.example.enlogty.communicationassistant.base.BasePresenter;
import com.example.enlogty.communicationassistant.fragment.SynchroRecordFragment;
import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;
import com.example.enlogty.communicationassistant.model.ISynchroRecordModel;
import com.example.enlogty.communicationassistant.model.SynchroRecordModel;
import com.example.enlogty.communicationassistant.view.ISynchroRecord;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class SynchroRecordPresenter extends BasePresenter<ISynchroRecord>{
    private SynchroRecordModel  model = new SynchroRecordModel();
    public void fetch(Context context){
        model.loadSynchroRecordDB(context ,new ISynchroRecordModel.SynchroRecordListener() {
            @Override
            public void onComplete(List<SynchroRecord> synchroRecordList) {
                ((SynchroRecordFragment)getView()).getSynchroRecoreData(synchroRecordList);
            }
        });
    }
}
