package com.example.enlogty.communicationassistant.base;

import java.lang.ref.WeakReference;

/**
 * Created by enlogty on 2017/8/22.
 */

public abstract class BasePresenter<T> {
    protected WeakReference<T> weakReference;
    public  void attachView(T view){
        weakReference = new WeakReference<T>(view);
    }
    public void detachView(){
        if (weakReference != null)
        weakReference.clear();
    }
    public T getView(){
        return weakReference.get();
    }
}
