package com.zhiting.clouddisk.entity.mine;

import java.util.List;

public class FolderDetailBean {

    private String name; // 文件夹名称
    private int is_encrypt; // 是否加密 1加密0不加密
    private int mode; //文件夹类型：1私人文件夹 2共享文件夹
    private String pool_name;  // 存储池名称
    private String partition_name;  // 存储池分区名称
    private List<AccessibleMemberBean> auth;  // 可访问成员

    private String pwd; // 密码 （添加文件夹额外的字段）
    private String confirm_pwd; // 确认密码（添加文件夹额外的字段）

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_encrypt() {
        return is_encrypt;
    }

    public void setIs_encrypt(int is_encrypt) {
        this.is_encrypt = is_encrypt;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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

    public List<AccessibleMemberBean> getAuth() {
        return auth;
    }

    public void setAuth(List<AccessibleMemberBean> auth) {
        this.auth = auth;
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
}
