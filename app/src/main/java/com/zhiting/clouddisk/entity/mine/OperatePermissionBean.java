package com.zhiting.clouddisk.entity.mine;

import androidx.annotation.DrawableRes;

public class OperatePermissionBean {

    private int id;
    private String name;
    private @DrawableRes int drawable;
    private boolean selected;  // 是否选中
    private boolean enabled = true; // 是否可操作

    public OperatePermissionBean(String name, int drawable) {
        this.name = name;
        this.drawable = drawable;
    }

    public OperatePermissionBean(String name, int drawable, boolean selected) {
        this.name = name;
        this.drawable = drawable;
        this.selected = selected;
    }

    public OperatePermissionBean(String name, int drawable, boolean selected, boolean enabled) {
        this.name = name;
        this.drawable = drawable;
        this.selected = selected;
        this.enabled = enabled;
    }

    public OperatePermissionBean(int id, String name, int drawable) {
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
