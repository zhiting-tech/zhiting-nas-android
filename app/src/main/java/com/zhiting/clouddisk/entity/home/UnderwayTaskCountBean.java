package com.zhiting.clouddisk.entity.home;

import java.io.Serializable;

public class UnderwayTaskCountBean implements Serializable {

    private int FileUploadNum;
    private int FileDownloadNum;
    private int AllNum;

    public int getFileUploadNum() {
        return FileUploadNum;
    }

    public UnderwayTaskCountBean setFileUploadNum(int fileUploadNum) {
        FileUploadNum = fileUploadNum;
        return this;
    }

    public int getFileDownloadNum() {
        return FileDownloadNum;
    }

    public UnderwayTaskCountBean setFileDownloadNum(int fileDownloadNum) {
        FileDownloadNum = fileDownloadNum;
        return this;
    }

    public int getAllNum() {
        return AllNum;
    }

    public UnderwayTaskCountBean setAllNum(int allNum) {
        AllNum = allNum;
        return this;
    }
}
