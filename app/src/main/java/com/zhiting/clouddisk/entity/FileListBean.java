package com.zhiting.clouddisk.entity;

import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;

import java.util.List;

public class FileListBean {

    private PagerBean pager;
    private List<FileBean> list;

    public PagerBean getPager() {
        return pager;
    }

    public void setPager(PagerBean pager) {
        this.pager = pager;
    }

    public List<FileBean> getList() {
        return list;
    }

    public void setList(List<FileBean> list) {
        this.list = list;
    }
}
