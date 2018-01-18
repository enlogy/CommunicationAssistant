package com.example.enlogty.communicationassistant.sqlite.bean;

/**
 * Created by enlogty on 2017/12/23.
 */


public class SynchroRecord{

    private int id;
    private String contactSize;
    private String smsSize;
    private String pictureSize;
    private String total;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactSize() {
        return contactSize;
    }

    public void setContactSize(String contactSize) {
        this.contactSize = contactSize;
    }

    public String getSmsSize() {
        return smsSize;
    }

    public void setSmsSize(String smsSize) {
        this.smsSize = smsSize;
    }

    public String getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(String pictureSize) {
        this.pictureSize = pictureSize;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
