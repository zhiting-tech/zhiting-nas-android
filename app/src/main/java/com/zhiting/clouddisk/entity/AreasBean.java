package com.zhiting.clouddisk.entity;

public class AreasBean {

    private String id;  // 家庭Id
    private String name;  // 家庭名称
    private boolean is_bind_sa;  // 是否绑定sa
    private int sa_user_id;
    private String sa_lan_address;
    private boolean is_bind_app;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_bind_sa() {
        return is_bind_sa;
    }

    public void setIs_bind_sa(boolean is_bind_sa) {
        this.is_bind_sa = is_bind_sa;
    }

    public int getSa_user_id() {
        return sa_user_id;
    }

    public void setSa_user_id(int sa_user_id) {
        this.sa_user_id = sa_user_id;
    }

    public String getSa_lan_address() {
        return sa_lan_address;
    }

    public void setSa_lan_address(String sa_lan_address) {
        this.sa_lan_address = sa_lan_address;
    }

    public boolean isIs_bind_app() {
        return is_bind_app;
    }

    public void setIs_bind_app(boolean is_bind_app) {
        this.is_bind_app = is_bind_app;
    }
}
