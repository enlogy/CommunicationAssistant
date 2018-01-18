package com.example.enlogty.communicationassistant.domain;

/**
 * Created by enlogty on 2017/9/30.
 */

public class Sms {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *           Log.d("SMSActivity",cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)));
     Log.d("SMSActivity",""+cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
     Log.d("SMSActivity",""+cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)));
     Log.d("SMSActivity",cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY)));
     */
    private Integer id;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private long date;
    private int type;
    private String body;

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    private String localNumber;

    @Override
    public String toString() {
        return "Sms{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", body='" + body + '\'' +
                ", localNumber='" + localNumber + '\'' +
                '}';
    }
}
