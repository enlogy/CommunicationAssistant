package com.example.enlogty.communicationassistant.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Created by enlogty on 2017/12/26.
 */

public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient{
    private MediaScannerConnection mediaScanConn;
    private MediaScannerConnection.OnScanCompletedListener listener;
    private String[] filePaths;
    private String[] typeMines;
    private Context mContext;
    private boolean singleFile;
    private int scanneredSize = 0;
    public MediaScanner(Context context) {
        this.mContext = context;
        mediaScanConn = new MediaScannerConnection(mContext,this);
    }

    public void scanFiles(String[] filePaths, String[] typeMines , MediaScannerConnection.OnScanCompletedListener listener){
        this.listener = listener;
        this.filePaths =  filePaths;
        this.typeMines = typeMines;
        mediaScanConn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
            mediaScanConn.scanFile(mContext,filePaths,typeMines,listener);
            filePaths = null;
            typeMines = null;
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        Log.d("内部onScanCompleted","调用到");
        scanneredSize ++;
        if (scanneredSize == filePaths.length){
            if (mediaScanConn.isConnected())
            mediaScanConn.disconnect();
            scanneredSize = 0;
        }
    }
    public void disConnection(){
        if (mediaScanConn.isConnected())
        mediaScanConn.disconnect();
    }
}
