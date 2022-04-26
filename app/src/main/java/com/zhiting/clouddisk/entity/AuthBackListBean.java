package com.zhiting.clouddisk.entity;

import java.util.List;

public class AuthBackListBean {

    private List<AuthBackBean> authBackList;

    public AuthBackListBean() {
    }

    public AuthBackListBean(List<AuthBackBean> authBackList) {
        this.authBackList = authBackList;
    }

    public List<AuthBackBean> getAuthBackList() {
        return authBackList;
    }

    public void setAuthBackList(List<AuthBackBean> authBackList) {
        this.authBackList = authBackList;
    }
}
