package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;
import java.util.List;

/**
 * 上传文件实体类
 */
public class UploadFilesBean implements Serializable {
    private List<UploadFileBean> UploadList;

    public List<UploadFileBean> getUploadList() {
        return UploadList;
    }

    public UploadFilesBean setUploadList(List<UploadFileBean> uploadList) {
        UploadList = uploadList;
        return this;
    }
}
