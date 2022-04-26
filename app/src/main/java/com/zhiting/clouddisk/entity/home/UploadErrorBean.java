package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;
import java.util.List;

/**
 * 上传文件实体类
 */
public class UploadErrorBean implements Serializable {
    private int status;
    private String reason;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
