package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;
import java.util.List;

/**
 * 下载文件实体类
 */
public class DownLoadFilesBean implements Serializable {
    private List<DownLoadFileBean> DownloadList;

    public List<DownLoadFileBean> getUploadList() {
        return DownloadList;
    }

    public DownLoadFilesBean setUploadList(List<DownLoadFileBean> uploadList) {
        DownloadList = uploadList;
        return this;
    }
}
