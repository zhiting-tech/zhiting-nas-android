package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;

/**
 * 文件实体类
 */
public class FileBean implements Serializable {


    /**
     * name : config.yaml
     * size : 1474
     * mod_time : 1622010079
     * type : 1
     * path : /1/demo-plugin/config.yaml
     * from_user :
     */

    private String name;  // 名称
    private long size;  // 大小（b)
    private long mod_time; // 修改时间
    private int type;//0是文件夹 1文件
    private String path; // 路径
    private String from_user;  // 共享者名称
    private boolean selected; // 是否选中该条数据，自己定义的字段

    private int is_encrypt; // 是否加密文件夹；文件夹有效：1加密，0不需要加密
    private int read; // 是否可读：1/0
    private int write; //是否可写：1/0
    private int deleted; // 是否可删：1/0

    private boolean enabled = true;  // 是否可操作，自己定义的字段
    private String thumbnail_url;//缩略、图播放图封面

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public FileBean() {
    }

    public FileBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public FileBean(String name, long size, long mod_time) {
        this.name = name;
        this.size = size;
        this.mod_time = mod_time;
    }

    public FileBean(String name, int type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
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

    public String getFrom_user() {
        return from_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIs_encrypt() {
        return is_encrypt;
    }

    public void setIs_encrypt(int is_encrypt) {
        this.is_encrypt = is_encrypt;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
