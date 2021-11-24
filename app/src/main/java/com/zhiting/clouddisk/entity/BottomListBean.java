package com.zhiting.clouddisk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BottomListBean implements Parcelable {

    private String name;
    private boolean selected;

    public BottomListBean() {
    }

    public BottomListBean(String name) {
        this.name = name;
    }

    public BottomListBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.selected = source.readByte() != 0;
    }

    protected BottomListBean(Parcel in) {
        this.name = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<BottomListBean> CREATOR = new Creator<BottomListBean>() {
        @Override
        public BottomListBean createFromParcel(Parcel source) {
            return new BottomListBean(source);
        }

        @Override
        public BottomListBean[] newArray(int size) {
            return new BottomListBean[size];
        }
    };
}
