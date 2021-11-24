package com.zhiting.clouddisk.entity.mine;

import androidx.annotation.DrawableRes;

public class SettingBean {

    private int id;
    private String name;
    @DrawableRes
    private int drawable;

    public SettingBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public SettingBean(int id, String name, int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
