package com.zhiting.networklib.event;

/**
 * 暂停上传下载
 */
public class FileStatusEvent {
    public int type;//0暂停 1开始

    public FileStatusEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
