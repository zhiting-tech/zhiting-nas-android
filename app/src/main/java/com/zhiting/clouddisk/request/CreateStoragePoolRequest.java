package com.zhiting.clouddisk.request;

/**
 * 添加到存储池
 */
public class CreateStoragePoolRequest {

    private String name;  // 添加到新的存储池 传的字段
    private String disk_name;

    public CreateStoragePoolRequest(String name, String disk_name) {
        this.name = name;
        this.disk_name = disk_name;
    }

    public CreateStoragePoolRequest(String disk_name) {
        this.disk_name = disk_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getDisk_name() {
        return disk_name;
    }

    public void setDisk_name(String disk_name) {
        this.disk_name = disk_name;
    }
}
