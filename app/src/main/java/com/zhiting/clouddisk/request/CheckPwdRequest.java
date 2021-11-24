package com.zhiting.clouddisk.request;

public class CheckPwdRequest {

    private String password;

    public CheckPwdRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
