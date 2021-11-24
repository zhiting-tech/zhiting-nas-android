package com.zhiting.clouddisk.entity.mine;

import java.util.List;

/**
 * 存储池列表
 */
public class StoragePoolListBean {

    private List<StoragePoolDetailBean> list;  // tem为object,存储池
    private PagerBean pager;  // 分页数据

    public List<StoragePoolDetailBean> getList() {
        return list;
    }

    public void setList(List<StoragePoolDetailBean> list) {
        this.list = list;
    }

    public PagerBean getPager() {
        return pager;
    }

    public void setPager(PagerBean pager) {
        this.pager = pager;
    }
}
