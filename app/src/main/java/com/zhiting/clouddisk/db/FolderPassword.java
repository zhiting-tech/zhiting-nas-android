package com.zhiting.clouddisk.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class FolderPassword {

    @Id(autoincrement = true)
    private Long id;  // 主键id
    private int userId;  // 用户id
    private String path;  // 文件夹路径
    private String password; // 文件夹密码
    private String scopeToken;  // scopeToken
    private Long modifyTime;  // 创建/修改时间

    @Generated(hash = 1014957722)
    public FolderPassword(Long id, int userId, String path, String password,
                          String scopeToken, Long modifyTime) {
        this.id = id;
        this.userId = userId;
        this.path = path;
        this.password = password;
        this.scopeToken = scopeToken;
        this.modifyTime = modifyTime;
    }

    @Generated(hash = 1726191658)
    public FolderPassword() {
    }

    public FolderPassword(int userId, String path, String password, String scopeToken, Long modifyTime) {
        this.userId = userId;
        this.path = path;
        this.password = password;
        this.scopeToken = scopeToken;
        this.modifyTime = modifyTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScopeToken() {
        return this.scopeToken;
    }

    public void setScopeToken(String scopeToken) {
        this.scopeToken = scopeToken;
    }

    public Long getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

}
