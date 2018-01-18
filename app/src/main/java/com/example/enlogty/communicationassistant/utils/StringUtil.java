package com.example.enlogty.communicationassistant.utils;

/**
 * Created by enlogty on 2017/11/30.
 */

public class StringUtil {

    public static String getFolderAbsolutePath(String abFilePath){
        int index = abFilePath.lastIndexOf("/");
        String path = abFilePath.substring(0,index+1);
        return path;
    }
    public static String getFileName(String abFilePath){
        int index = abFilePath.lastIndexOf("/");
        String fileName = abFilePath.substring(index+2,abFilePath.length());
        return fileName;
    }
}
