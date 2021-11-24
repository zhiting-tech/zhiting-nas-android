package com.zhiting.clouddisk.entity.mine;

import java.io.Serializable;

/**
 * 添加文件夹可访问成员
 */
public class AccessibleMemberBean implements Serializable {

    private int u_id; //用户ID
    private String face; // 用户头像：SA暂时不支持头像
    private String nickname; // 用户昵称：冗余昵称
    private int read; // 是否可读1/0
    private int write; // 是否可写1/0
    private int deleted; // 是否可删除1/0

    public AccessibleMemberBean() {
    }

    public AccessibleMemberBean(int u_id, String nickname) {
        this.u_id = u_id;
        this.nickname = nickname;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getWrite() {
        return write;
    }

    public void setWrite(int write) {
        this.write = write;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
