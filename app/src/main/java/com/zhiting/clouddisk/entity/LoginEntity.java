package com.zhiting.clouddisk.entity;

import java.io.Serializable;

public class LoginEntity implements Serializable {

   private UserInfoEntity user_info;

    public UserInfoEntity getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoEntity user_info) {
        this.user_info = user_info;
    }
}
