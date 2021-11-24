package com.zhiting.clouddisk.entity.mine;

/**
 * 分页数据
 */
public class PagerBean {

    private int page; //当前页码
    private int page_size; //每页条数
    private int total_rows;  // 总记录数
    private boolean has_more; // 是否有更多

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_rows() {
        return total_rows;
    }

    public void setTotal_rows(int total_rows) {
        this.total_rows = total_rows;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }
}
