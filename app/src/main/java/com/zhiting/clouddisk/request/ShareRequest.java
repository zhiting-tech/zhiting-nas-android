package com.zhiting.clouddisk.request;

import java.util.List;

/**
 * 共享 body
 */
public class ShareRequest {

    private List<Integer> to_users; // 目标用户的user_id，由成员列表接口返回
    private List<String> paths; // 共享的目录
    private int read; // 可读1/0
    private int write; // 可写1/0
    private int deleted; // 可删除1/0
    private String from_user; // 共享者昵称

    public ShareRequest(List<Integer> to_users, List<String> paths) {
        this.to_users = to_users;
        this.paths = paths;
    }

    public ShareRequest(List<Integer> to_users, List<String> paths, int read, int write, int deleted, String from_user) {
        this.to_users = to_users;
        this.paths = paths;
        this.read = read;
        this.write = write;
        this.deleted = deleted;
        this.from_user = from_user;
    }

    public ShareRequest(List<String> paths) {
        this.paths = paths;
    }

    public List<Integer> getTo_users() {
        return to_users;
    }

    public void setTo_users(List<Integer> to_users) {
        this.to_users = to_users;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
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

    public String getFrom_user() {
        return from_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }
}
