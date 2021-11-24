package com.zhiting.clouddisk.request;

import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;

import java.util.List;

public class AddFolderRequest {

    private String name; // 文件夹名称
    private String pool_name; // 储存池名称
    private String partition_name; // 储存池分区名称
    private int is_encrypt; // 是否加密1需要，0不需要
    private String pwd; // 密码
    private String confirm_pwd; // 确认密码
    private int mode; // 文件夹类型：1私人文件夹 2共享文件夹
    private List<AccessibleMemberBean> auth;

    public AddFolderRequest(String name, String pool_name, String partition_name, int is_encrypt, int mode) {
        this.name = name;
        this.pool_name = pool_name;
        this.partition_name = partition_name;
        this.is_encrypt = is_encrypt;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }

    public String getPartition_name() {
        return partition_name;
    }

    public void setPartition_name(String partition_name) {
        this.partition_name = partition_name;
    }

    public int getIs_encrypt() {
        return is_encrypt;
    }

    public void setIs_encrypt(int is_encrypt) {
        this.is_encrypt = is_encrypt;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getConfirm_pwd() {
        return confirm_pwd;
    }

    public void setConfirm_pwd(String confirm_pwd) {
        this.confirm_pwd = confirm_pwd;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<AccessibleMemberBean> getAuth() {
        return auth;
    }

    public void setAuth(List<AccessibleMemberBean> auth) {
        this.auth = auth;
    }
}
