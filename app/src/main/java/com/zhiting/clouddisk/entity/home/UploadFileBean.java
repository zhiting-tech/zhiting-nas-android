package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;

/**
 * 上传文件实体类
 */
public class UploadFileBean implements Serializable {
    private Integer id;
    private String url;//上传地址
    private long size;//文件大小
    private String name;//文件名字
    private long upload;//已上传大小
    private long speeds;//速度b/s
    private Integer status;//上传状态 0未开始 1上传中 2暂停 3已完成 4上传失败 5生成临时文件中
    private String hash;//哈希值
    private long create_time;//创建时间
    private String tmp_name;
    private String ThreadInfo;//记录每个线程对应块的上传状态 1已上传 0未上传

    public Integer getId() {
        return id;
    }

    public UploadFileBean setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UploadFileBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getSize() {
        return size;
    }

    public UploadFileBean setSize(long size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public UploadFileBean setName(String name) {
        this.name = name;
        return this;
    }

    public long getUpload() {
        return upload;
    }

    public UploadFileBean setUpload(long upload) {
        this.upload = upload;
        return this;
    }

    public long getSpeeds() {
        return speeds;
    }

    public UploadFileBean setSpeeds(long speeds) {
        this.speeds = speeds;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public UploadFileBean setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public UploadFileBean setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public long getCreate_time() {
        return create_time;
    }

    public UploadFileBean setCreate_time(long create_time) {
        this.create_time = create_time;
        return this;
    }

    public String getTmp_name() {
        return tmp_name;
    }

    public UploadFileBean setTmp_name(String tmp_name) {
        this.tmp_name = tmp_name;
        return this;
    }

    public String getThreadInfo() {
        return ThreadInfo;
    }

    public UploadFileBean setThreadInfo(String threadInfo) {
        ThreadInfo = threadInfo;
        return this;
    }
}
