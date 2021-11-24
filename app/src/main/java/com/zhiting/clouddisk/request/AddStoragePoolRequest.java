package com.zhiting.clouddisk.request;

public class AddStoragePoolRequest {

    private String pool_name;  // 储存池名称
    private String disk_name; // 硬盘名称

    public AddStoragePoolRequest(String pool_name, String disk_name) {
        this.pool_name = pool_name;
        this.disk_name = disk_name;
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }

    public String getDisk_name() {
        return disk_name;
    }

    public void setDisk_name(String disk_name) {
        this.disk_name = disk_name;
    }
}
