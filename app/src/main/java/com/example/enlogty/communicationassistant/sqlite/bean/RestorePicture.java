package com.example.enlogty.communicationassistant.sqlite.bean;


/**
 * Created by enlogty on 2017/12/23.
 */


public class RestorePicture{

    private int id;
    private String filePath;
    private String fileName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
