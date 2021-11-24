package com.zhiting.clouddisk.entity.mine;

import java.io.Serializable;

public class FolderBean implements Serializable {

    private long id;
    private String name;  //文件夹名称
    private int is_encrypt; // 是否需要加密1需要0步需要
    private int mode; // 类型：1个人文件夹2分享文件夹
    private String persons; // 可访问成员，字符串输出
    private String pool_name; // 存储分区名称
    private String task_id; // 异步任务ID

    // 枚举: 为空则代表没有异步状态,TaskMovingFolder_1 修改中,TaskMovingFolder_0 修改失败,TaskDelFolder_1 删除中,TaskDelFolder_0 删除失败
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getPersons() {
        return persons;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}
