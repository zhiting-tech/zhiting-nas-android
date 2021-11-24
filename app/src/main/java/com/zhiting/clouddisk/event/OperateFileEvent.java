package com.zhiting.clouddisk.event;

import com.zhiting.clouddisk.entity.home.FileBean;

import java.util.List;

/**
 * 首页操作主界面的文件操作栏视图显示
 */
public class OperateFileEvent {

    private int selectedSize;
    private boolean onlyFolder;
    private FileBean fileBean;
    private List<FileBean> folders;



    public OperateFileEvent(int selectedSize) {
        this.selectedSize = selectedSize;
    }

    public OperateFileEvent(int selectedSize, boolean onlyFolder) {
        this.selectedSize = selectedSize;
        this.onlyFolder = onlyFolder;
    }

    public OperateFileEvent(int selectedSize, boolean onlyFolder, FileBean fileBean) {
        this.selectedSize = selectedSize;
        this.onlyFolder = onlyFolder;
        this.fileBean = fileBean;
    }

    public OperateFileEvent(boolean onlyFolder, List<FileBean> folders) {
        this.onlyFolder = onlyFolder;
        this.folders = folders;
    }

    public OperateFileEvent(int selectedSize, boolean onlyFolder, List<FileBean> folders) {
        this.selectedSize = selectedSize;
        this.onlyFolder = onlyFolder;
        this.folders = folders;
    }

    public int getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(int selectedSize) {
        this.selectedSize = selectedSize;
    }

    public boolean isOnlyFolder() {
        return onlyFolder;
    }

    public void setOnlyFolder(boolean onlyFolder) {
        this.onlyFolder = onlyFolder;
    }

    public FileBean getFileBean() {
        return fileBean;
    }

    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public List<FileBean> getFolders() {
        return folders;
    }

    public void setFolders(List<FileBean> folders) {
        this.folders = folders;
    }
}
