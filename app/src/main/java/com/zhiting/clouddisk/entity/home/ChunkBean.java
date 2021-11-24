package com.zhiting.clouddisk.entity.home;

/**
 * 上传/创建文件的 chunk 类
 */
public class ChunkBean {

    private long id;
    private int size;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
