package com.example.enlogty.communicationassistant.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.enlogty.communicationassistant.sqlite.bean.RestorePicture;
import com.example.enlogty.communicationassistant.sqlite.MySQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class RestorePictureModel implements IRestorePictureModel{
    @Override
    public void loadRestorePictureDB(Context context,RestorePictureListener restorePictureListener) {

        //RestorePictureDao restorePictureDao = MyApplication.getInstances().getDaoSession().getRestorePictureDao();
//        List<RestorePicture> restorePictures = DataSupport.findAll(RestorePicture.class);
        List<RestorePicture> restorePictures = new ArrayList<>();
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context,"history.db",null,1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("restorepicture", null, null, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    RestorePicture rp = new RestorePicture();
                    String filePath = cursor.getString(cursor.getColumnIndex("filepath"));
                    String fileName = cursor.getString(cursor.getColumnIndex("filename"));
                    rp.setFileName(fileName);
                    rp.setFilePath(filePath);
                    boolean pass = true;
                    for (int i=0;i<restorePictures.size();i++){
                        if (restorePictures.get(i).getFilePath().equals(filePath)){
                            pass = false;
                        }
                    }
                    if (new File(filePath).exists()&&pass){
                        restorePictures.add(rp);
                    }

                }while (cursor.moveToNext());
            }
        }

        cursor.close();
        restorePictureListener.onComplete(restorePictures);
    }
}
