package com.zhiting.clouddisk.request;

public class PoolNameRequest {

    private String pool_name;

    public PoolNameRequest(String pool_name) {
        this.pool_name = pool_name;
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }
}
