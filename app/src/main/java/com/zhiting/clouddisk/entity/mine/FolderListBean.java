package com.zhiting.clouddisk.entity.mine;

import java.util.List;

public class FolderListBean {

    private List<FolderBean> list;
    private PagerBean pager;

    public List<FolderBean> getList() {
        return list;
    }

    public void setList(List<FolderBean> list) {
        this.list = list;
    }

    public PagerBean getPager() {
        return pager;
    }

    public void setPager(PagerBean pager) {
        this.pager = pager;
    }
}
