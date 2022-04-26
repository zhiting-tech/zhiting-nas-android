package com.zhiting.clouddisk.entity;


import java.util.List;

import okhttp3.Cookie;

public class AuthBackBean {

    private int userId; // 用户id
    private String userName;  // 用户昵称
    private HomeCompanyBean homeCompanyBean; // 家庭信息
    private ScopeBean stBean;  // 授权token和过期时间
    private boolean selected;
    private List<Cookie> cookies;  // 登录SC时的cookie

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public HomeCompanyBean getHomeCompanyBean() {
        return homeCompanyBean;
    }

    public void setHomeCompanyBean(HomeCompanyBean homeCompanyBean) {
        this.homeCompanyBean = homeCompanyBean;
    }

    public ScopeBean getStBean() {
        return stBean;
    }

    public void setStBean(ScopeBean stBean) {
        this.stBean = stBean;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
