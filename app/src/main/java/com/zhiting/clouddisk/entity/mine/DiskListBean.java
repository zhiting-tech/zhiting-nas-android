package com.zhiting.clouddisk.entity.mine;

import java.util.List;

public class DiskListBean {

    private List<DiskBean> list;
    private PagerBean pager;;

    public List<DiskBean> getList() {
        return list;
    }

    public void setList(List<DiskBean> list) {
        this.list = list;
    }

    public PagerBean getPager() {
        return pager;
    }

    public void setPager(PagerBean pager) {
        this.pager = pager;
    }
}
