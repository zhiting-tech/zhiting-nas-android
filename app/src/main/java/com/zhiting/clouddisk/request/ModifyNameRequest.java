package com.zhiting.clouddisk.request;

public class ModifyNameRequest {

    private String new_name;  // 存储池新名称

    public ModifyNameRequest(String new_name) {
        this.new_name = new_name;
    }

    public String getNew_name() {
        return new_name;
    }

    public void setNew_name(String new_name) {
        this.new_name = new_name;
    }
}
