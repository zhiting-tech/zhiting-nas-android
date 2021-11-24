package com.zhiting.clouddisk.entity;

import java.io.Serializable;

public class PermissionUserBean implements Serializable {

    private String name;
    private String avatar;

    public PermissionUserBean(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
