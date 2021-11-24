package com.zhiting.clouddisk.entity.home;

import androidx.annotation.DrawableRes;

public class FileOperateBean {

    @DrawableRes
    private int drawable;
    private String name;
    private boolean enabled;

    public FileOperateBean(int drawable, String name) {
        this.drawable = drawable;
        this.name = name;
    }

    public FileOperateBean(int drawable, String name, boolean enabled) {
        this.drawable = drawable;
        this.name = name;
        this.enabled = enabled;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
