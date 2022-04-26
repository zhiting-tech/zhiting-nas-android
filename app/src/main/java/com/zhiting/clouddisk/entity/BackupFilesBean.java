package com.zhiting.clouddisk.entity;

import com.zhiting.clouddisk.entity.home.UploadFileBean;

import java.util.List;

public class BackupFilesBean {

    private List<UploadFileBean> UploadOnGoingList; // 进行中
    private List<UploadFileBean> UploadOnSuccessList ;  //上传成功
    private List<UploadFileBean> UploadOnFailList;  // 上传失败
    private long OnGoingNum; // 进行中数量
    private long StopNum;  // 停止数量
    private long FinishNum;  // 完成数量
    private long FailNum;  // 失败数量

    public List<UploadFileBean> getUploadOnGoingList() {
        return UploadOnGoingList;
    }

    public void setUploadOnGoingList(List<UploadFileBean> uploadOnGoingList) {
        UploadOnGoingList = uploadOnGoingList;
    }

    public List<UploadFileBean> getUploadOnSuccessList() {
        return UploadOnSuccessList;
    }

    public void setUploadOnSuccessList(List<UploadFileBean> uploadOnSuccessList) {
        UploadOnSuccessList = uploadOnSuccessList;
    }

    public List<UploadFileBean> getUploadOnFailList() {
        return UploadOnFailList;
    }

    public void setUploadOnFailList(List<UploadFileBean> uploadOnFailList) {
        UploadOnFailList = uploadOnFailList;
    }

    public long getOnGoingNum() {
        return OnGoingNum;
    }

    public void setOnGoingNum(long onGoingNum) {
        OnGoingNum = onGoingNum;
    }

    public long getStopNum() {
        return StopNum;
    }

    public void setStopNum(long stopNum) {
        StopNum = stopNum;
    }

    public long getFinishNum() {
        return FinishNum;
    }

    public void setFinishNum(long finishNum) {
        FinishNum = finishNum;
    }

    public long getFailNum() {
        return FailNum;
    }

    public void setFailNum(long failNum) {
        FailNum = failNum;
    }
}
