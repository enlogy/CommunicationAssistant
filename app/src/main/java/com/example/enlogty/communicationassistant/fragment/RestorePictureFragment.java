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
import com.example.enlogty.communicationassistant.adapter.RestorePictureRVAdapter;
import com.example.enlogty.communicationassistant.adapter.UnderlineDecoration;
import com.example.enlogty.communicationassistant.base.BaseFragment;
import com.example.enlogty.communicationassistant.sqlite.bean.RestorePicture;
import com.example.enlogty.communicationassistant.presenter.RestorePicturePresenter;
import com.example.enlogty.communicationassistant.view.IRestorePicture;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class RestorePictureFragment extends BaseFragment<IRestorePicture,RestorePicturePresenter> implements IRestorePicture{
    private  View view;
    private RecyclerView restorePictureRV;
    private Context mContext;
    @Override
    public RestorePicturePresenter createPresenter() {
        return new RestorePicturePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mContext = container.getContext();
        return view = inflater.inflate(R.layout.fragment_restorepicture,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.fetch(mContext);
    }

    @Override
    public void getRestorePictureData(List<RestorePicture> restorePictureList) {
        Log.d("RP-isMainThread",isMainThread()+"");
        restorePictureRV = view.findViewById(R.id.restorePictureRV);
        RestorePictureRVAdapter adapter = new RestorePictureRVAdapter(restorePictureList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        restorePictureRV.setLayoutManager(linearLayoutManager);
        restorePictureRV.addItemDecoration(new UnderlineDecoration());
        restorePictureRV.setAdapter(adapter);
    }
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
