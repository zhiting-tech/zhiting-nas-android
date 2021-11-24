package com.zhiting.clouddisk.request;

public class UpdateFolderPwdRequest {

    private long id;
    private String old_pwd;
    private String new_pwd;
    private String confirm_pwd;

    public UpdateFolderPwdRequest(long id, String old_pwd, String new_pwd, String confirm_pwd) {
        this.id = id;  // 文件夹ID
        this.old_pwd = old_pwd;  // 旧密码
        this.new_pwd = new_pwd;  // 新密码;
        this.confirm_pwd = confirm_pwd;  // 确认密码
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOld_pwd() {
        return old_pwd;
    }

    public void setOld_pwd(String old_pwd) {
        this.old_pwd = old_pwd;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getConfirm_pwd() {
        return confirm_pwd;
    }

    public void setConfirm_pwd(String confirm_pwd) {
        this.confirm_pwd = confirm_pwd;
    }
}
