package com.example.enlogty.communicationassistant.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by enlogty on 2017/9/19.
 */

public class Image implements Parcelable {

    private String name;
    private String path;
    private String type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getSimplename() {
        return simplename;
    }

    public void setSimplename(String simplename) {
        this.simplename = simplename;
    }

    private String simplename;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(type);
        dest.writeString(simplename);
    }
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>(){
        @Override
        public Image createFromParcel(Parcel source) {
            Image image = new Image();
            image.name = source.readString();
            image.path = source.readString();
            image.type = source.readString();
            image.simplename = source.readString();
            return image;
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
