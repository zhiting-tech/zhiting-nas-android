package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;

/**
 * 下载文件实体类
 */
public class DownLoadFileBean implements Serializable {

    private int id;
    private int pid;//所属文件夹下载id
    private String type;//下载类型file dir
    private String url;//下载地址
    private long size;//文件的大小
    private String name;//文件大小
    private long downloaded;//下载大小
    private int speeds;//速度
    private int status;//下载状态 0未开始 1下载中 2已暂停 3 成功 4下载失败
    private long create_time;//开始下载时间

    public int getId() {
        return id;
    }

    public DownLoadFileBean setId(int id) {
        this.id = id;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public DownLoadFileBean setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public String getType() {
        return type;
    }

    public DownLoadFileBean setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DownLoadFileBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getSize() {
        return size;
    }

    public DownLoadFileBean setSize(long size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public DownLoadFileBean setName(String name) {
        this.name = name;
        return this;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public DownLoadFileBean setDownloaded(long downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public int getSpeeds() {
        return speeds;
    }

    public DownLoadFileBean setSpeeds(int speeds) {
        this.speeds = speeds;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public DownLoadFileBean setStatus(int status) {
        this.status = status;
        return this;
    }

    public long getCreate_time() {
        return create_time;
    }

    public DownLoadFileBean setCreate_time(long create_time) {
        this.create_time = create_time;
        return this;
    }
}
