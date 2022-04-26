package com.zhiting.clouddisk.entity.home;

public class BackupBean {

    private int id;
    private String url;  // 上传地址
    private String thumbnail_url;  // 缩略图
    private String preview_url;  // 预览地址
    private int is_backup;  //
    private long size;  // 文件大小
    private String name; // 文件名称
    private long upload; // 已上传大小
    private long speeds; // 速度 B/s
    private int status;  // 上传状态 |0未开始|1上传中|2已暂停｜3已完成|4上传失败｜5生成临时文件中
    private long create_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public int getIs_backup() {
        return is_backup;
    }

    public void setIs_backup(int is_backup) {
        this.is_backup = is_backup;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpload() {
        return upload;
    }

    public void setUpload(long upload) {
        this.upload = upload;
    }

    public long getSpeeds() {
        return speeds;
    }

    public void setSpeeds(long speeds) {
        this.speeds = speeds;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
}
