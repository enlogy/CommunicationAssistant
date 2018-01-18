package com.example.enlogty.communicationassistant.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.enlogty.communicationassistant.bean.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by enlogty on 2017/9/19.
 */

public class ImageUtil {
    private int imagePathGETSize = 0;
    private   ArrayList<Image> imageList = new ArrayList<>();
    private String rootDir = "/storage/emulated/0";
    private final String[] dirSrc = {
            rootDir+"/dcim",
            rootDir+"/download",
            rootDir+"/netease",
            rootDir+"/pictures",
            rootDir+"/tencent",
            rootDir+"/joke_essay",
            rootDir+"/sina"};
    /**
     * 需要调用两次，第一次
     * @param cr
     * @return
     */
    public ArrayList<Image> getImagePath(ContentResolver cr){
        imageList.clear();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Image img;
        Cursor cursor = cr.query(uri, null, null, null, null);
        int count = cursor.getCount();
        Log.d("保存图片前的count",count+"");
        while (cursor.moveToNext()){
            String mPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            for (int j=0;j<dirSrc.length;j++){
                if (mPath.toLowerCase().contains(dirSrc[j])){

                    Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    img = new Image();
                    img.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    img.setSimplename(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                    img.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    img.setType(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    String spn = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                    if (spn.contains("22-46-10-image"))
                        Log.d("是否存在文件",""+spn.contains("22-46-10-image"));
                    //数据库保存的文件名被篡改，将其名字改回去
                    if (!new File(filePath).exists()){
                        //在数据库中删除 不存在文件的一行数据
//                int res = cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        MediaStore.Audio.Media.DATA + "= ?",new String[]{cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))});
                        String simpleName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                        String checkName = StringUtil.getFolderAbsolutePath(filePath)+simpleName;
                        File searchFile = new File(StringUtil.getFolderAbsolutePath(filePath));
                        File[] files = searchFile.listFiles();
                        //if (files != null){
                        for (int i=0;i<files.length;i++){
                            if (files[i].isFile()){
                                //已存在的文件路径
                                String fp = files[i].toString();
                                //Log.d(" files[i].toString();",files[i].toString());
                                if (fp.contains(checkName)){
                                    Log.d("包含-已存在文件",fp);
                                    Log.d("被改名的文件",filePath);
                                    if (files[i].canWrite()){
                                        Log.d("文件可写","canWrite() = true");
                                        boolean b = files[i].renameTo(new File(filePath));
                                        Log.d("改名是否成功",b+"");
                                    }
                                    //new MediaScanner();
                                }
                            }

                        }
                        // }

                        Log.d("文件不存在","---");


                        continue;
                    }
                    imageList.add(img);

                }
            }


        }
//
        if (cursor != null){
            cursor.close();
        }
        imagePathGETSize ++;
        if (imagePathGETSize == 1){
            Log.d("if (imagePathGETSize == 1){","");
            getImagePath(cr);
        }
        Log.d("if (imagePathGETSize == 1){","false");
        return imageList;
    }
}
