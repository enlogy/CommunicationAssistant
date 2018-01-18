package com.example.enlogty.communicationassistant.domain;

/**
 * Created by enlogty on 2017/11/30.
 */
public class CloudImage {

    private Integer id;
    private String path;
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CloudImage{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", mynumber='" + mynumber + '\'' +
                '}';
    }

    private String type;
    public String getMynumber() {
        return mynumber;
    }

    public void setMynumber(String mynumber) {
        this.mynumber = mynumber;
    }

    private String mynumber;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
