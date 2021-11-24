package com.zhiting.networklib.entity;

public class BaseUrlEvent {
    private String baseUrl;

    public BaseUrlEvent(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
