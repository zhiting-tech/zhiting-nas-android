package com.zhiting.clouddisk.entity.home;

/**
 * 上传/创建文件的 resource 类
 */
public class ResourceBean {

    private String name; // 目录/文件名称
    private int size; // 目录/文件大小
    private long mod_time; // 文件最后更新时间
    private int type; // 类型 0:目录;1:文件
    private String path; // 目录/文件路径

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getMod_time() {
        return mod_time;
    }

    public void setMod_time(long mod_time) {
        this.mod_time = mod_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
