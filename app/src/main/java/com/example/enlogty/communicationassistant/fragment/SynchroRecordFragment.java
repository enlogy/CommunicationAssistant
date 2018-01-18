package com.example.enlogty.communicationassistant.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.adapter.SynchroRecordRVAdapter;
import com.example.enlogty.communicationassistant.adapter.UnderlineDecoration;
import com.example.enlogty.communicationassistant.base.BaseFragment;
import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;
import com.example.enlogty.communicationassistant.presenter.SynchroRecordPresenter;
import com.example.enlogty.communicationassistant.view.ISynchroRecord;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class SynchroRecordFragment extends BaseFragment<ISynchroRecord,SynchroRecordPresenter> implements ISynchroRecord{
    private RecyclerView synchroRecordRV;
    private View view;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mContext = container.getContext();
        return view = inflater.inflate(R.layout.fragment_synchrorecord,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.fetch(mContext);
    }

    @Override
    public void getSynchroRecoreData(List<SynchroRecord> synchroRecordList) {
        Log.d("SR-isMainThread",isMainThread()+"");
        synchroRecordRV = view.findViewById(R.id.synchroRecordRV);
        SynchroRecordRVAdapter adapter = new SynchroRecordRVAdapter(synchroRecordList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        synchroRecordRV.setLayoutManager(linearLayoutManager);
        synchroRecordRV.addItemDecoration(new UnderlineDecoration());
        synchroRecordRV.setAdapter(adapter);
    }
    @Override
    public SynchroRecordPresenter createPresenter() {
        return new SynchroRecordPresenter();
    }
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
