package com.example.enlogty.communicationassistant.rx;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by enlogty on 2017/12/15.
 */

public class RxBus {
    private static volatile RxBus rxBus;
    private final Subject<Object,Object> subject;
    private RxBus(){
        subject = new SerializedSubject<>(PublishSubject.create());
    }
    public  static RxBus getInstance(){
        if (rxBus==null){
            synchronized (RxBus.class){
                if (rxBus==null){
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }
    public void post(Object obj){
        subject.onNext(obj);
    }
    public <T> Observable<T> tObservable(Class<T> tClass){
        return subject.ofType(tClass);
    }
}
