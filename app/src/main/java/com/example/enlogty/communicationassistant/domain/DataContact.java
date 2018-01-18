package com.example.enlogty.communicationassistant.domain;

/**
 * Created by enlogty on 2017/11/22.
 */

public class DataContact {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String localnumber;
    private String name;
    private String phonenumber;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck;


    public String getLocalnumber() {
        return localnumber;
    }

    public void setLocalnumber(String localnumber) {
        this.localnumber = localnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "DataContact{" +
                "id=" + id +
                ", localnumber='" + localnumber + '\'' +
                ", name='" + name + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }
}
