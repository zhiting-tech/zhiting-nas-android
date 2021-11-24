package com.zhiting.clouddisk.entity.mine;

public class FolderSettingBean {

    private String pool_name;  // 存储池
    private String partition_name; // 存储池分区
    private int is_auto_del;   // 成员退出，是否自动删除，1/0

    public FolderSettingBean() {
    }

    public FolderSettingBean(String pool_name, String partition_name, int is_auto_del) {
        this.pool_name = pool_name;
        this.partition_name = partition_name;
        this.is_auto_del = is_auto_del;
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

    public int getIs_auto_del() {
        return is_auto_del;
    }

    public void setIs_auto_del(int is_auto_del) {
        this.is_auto_del = is_auto_del;
    }
}
