package com.example.enlogty.communicationassistant.utils;

import android.os.Looper;

/**
 * Created by enlogty on 2017/12/28.
 */

public class ThreadUtil {
    public static boolean isMainThead(){
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
