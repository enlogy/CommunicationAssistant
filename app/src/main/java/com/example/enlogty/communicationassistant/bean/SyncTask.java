package com.example.enlogty.communicationassistant.bean;

/**
 * Created by enlogty on 2017/12/15.
 */

public class SyncTask {
    private static SyncTask task;
    private SyncTask() {
    }
    public static SyncTask getInstance(){
        if (task==null){
            synchronized (SyncTask.class){
                if (task==null){
                    task = new SyncTask();
                }
            }
        }
        return task;
    }
    public boolean Downloading;
    public int ContactUploadCount;
    public int ContactUploadedCount;
    public int ContactDownloadCount;
    public int ContactDownloadedCount;
    public int SmsUploadCount;
    public int SmsUploadedCount;
    public int SmsDownloadCount;
    public int SmsDownloadedCount;
    public int PictureUploadCount;
    public int PictureUploadedCount;
    public int PictureDownloadCount;
    public int PictureDownloadedCount;

    private int TaskTotal;
    private int TaskedTotal;

    public int getContactUploadCount() {
        return ContactUploadCount;
    }

    public void setContactUploadCount(int contactUploadCount) {
        ContactUploadCount = contactUploadCount;
    }

    public int getContactUploadedCount() {
        return ContactUploadedCount;
    }

    public void setContactUploadedCount(int contactUploadedCount) {
        ContactUploadedCount = contactUploadedCount;
    }

    public int getContactDownloadCount() {
        return ContactDownloadCount;
    }

    public void setContactDownloadCount(int contactDownloadCount) {
        ContactDownloadCount = contactDownloadCount;
    }

    public int getContactDownloadedCount() {
        return ContactDownloadedCount;
    }

    public void setContactDownloadedCount(int contactDownloadedCount) {
        ContactDownloadedCount = contactDownloadedCount;
    }

    public int getSmsUploadCount() {
        return SmsUploadCount;
    }

    public void setSmsUploadCount(int smsUploadCount) {
        SmsUploadCount = smsUploadCount;
    }

    public int getSmsUploadedCount() {
        return SmsUploadedCount;
    }

    public void setSmsUploadedCount(int smsUploadedCount) {
        SmsUploadedCount = smsUploadedCount;
    }

    public int getSmsDownloadCount() {
        return SmsDownloadCount;
    }

    public void setSmsDownloadCount(int smsDownloadCount) {
        SmsDownloadCount = smsDownloadCount;
    }

    public int getSmsDownloadedCount() {
        return SmsDownloadedCount;
    }

    public void setSmsDownloadedCount(int smsDownloadedCount) {
        SmsDownloadedCount = smsDownloadedCount;
    }

    public int getPictureUploadCount() {
        return PictureUploadCount;
    }

    public void setPictureUploadCount(int pictureUploadCount) {
        PictureUploadCount = pictureUploadCount;
    }

    public int getPictureUploadedCount() {
        return PictureUploadedCount;
    }

    public void setPictureUploadedCount(int pictureUploadedCount) {
        PictureUploadedCount = pictureUploadedCount;
    }

    public int getPictureDownloadCount() {
        return PictureDownloadCount;
    }

    public void setPictureDownloadCount(int pictureDownloadCount) {
        PictureDownloadCount = pictureDownloadCount;
    }

    public int getPictureDownloadedCount() {
        return PictureDownloadedCount;
    }

    public void setPictureDownloadedCount(int pictureDownloadedCount) {
        PictureDownloadedCount = pictureDownloadedCount;
    }

    public int getTaskTotal() {
        return TaskTotal=ContactDownloadCount+ContactUploadCount+SmsDownloadCount+SmsUploadCount+PictureDownloadCount+PictureUploadCount;
    }

    public int getTaskedTotal() {
        return TaskedTotal=ContactDownloadedCount+ContactUploadedCount+SmsDownloadedCount+SmsUploadedCount+PictureDownloadedCount+PictureUploadedCount;
    }

    @Override
    public String toString() {
        return "SyncTask{" +
                "ContactUploadCount=" + ContactUploadCount +
                ", ContactUploadedCount=" + ContactUploadedCount +
                ", ContactDownloadCount=" + ContactDownloadCount +
                ", ContactDownloadedCount=" + ContactDownloadedCount +
                ", SmsUploadCount=" + SmsUploadCount +
                ", SmsUploadedCount=" + SmsUploadedCount +
                ", SmsDownloadCount=" + SmsDownloadCount +
                ", SmsDownloadedCount=" + SmsDownloadedCount +
                ", PictureUploadCount=" + PictureUploadCount +
                ", PictureUploadedCount=" + PictureUploadedCount +
                ", PictureDownloadCount=" + PictureDownloadCount +
                ", PictureDownloadedCount=" + PictureDownloadedCount +
                ", TaskTotal=" + TaskTotal +
                ", TaskedTotal=" + TaskedTotal +
                '}';
    }
    public void reset(){
        ContactUploadCount = 0;
        ContactUploadedCount = 0;
        ContactDownloadCount = 0;
        ContactDownloadedCount = 0;
        SmsUploadCount = 0;
        SmsUploadedCount = 0;
        SmsDownloadCount = 0;
        SmsDownloadedCount = 0;
        PictureUploadCount = 0;
        PictureUploadedCount = 0;
        PictureDownloadCount = 0;
        PictureDownloadedCount = 0;
    }
}
