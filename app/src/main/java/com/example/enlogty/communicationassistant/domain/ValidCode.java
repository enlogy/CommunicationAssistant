package com.example.enlogty.communicationassistant.domain;

/**
 * Created by enlogty on 2017/11/30.
 */

public class ValidCode {
    private int id;

    @Override
    public String toString() {
        return "ValidCode{" +
                "id=" + id +
                ", validcode=" + validCode +
                ", phonenumber='" + phonenumber + '\'' +
                '}';
    }

    private int validCode;
    private String phonenumber;


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


    public void setValidcode(int validcode) {
        this.validCode = validcode;
    }
    public int getValidcode() {
        return validCode;
    }


    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getPhonenumber() {
        return phonenumber;
    }

}
