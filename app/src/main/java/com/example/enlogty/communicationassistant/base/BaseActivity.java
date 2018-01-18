package com.example.enlogty.communicationassistant.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by enlogty on 2017/8/22.
 */

public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity{
    protected T mPresenter;
    public abstract T createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
