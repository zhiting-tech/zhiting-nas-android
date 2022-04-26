package com.zhiting.clouddisk.entity;

public class UserInfoBean {

    private int user_id;  // 用户Id
    private String user_name;  // 用户名称
    private String scope_token; // 访问sa的scope-token

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getScope_token() {
        return scope_token;
    }

    public void setScope_token(String scope_token) {
        this.scope_token = scope_token;
    }
}
