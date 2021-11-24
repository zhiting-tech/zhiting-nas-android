package com.zhiting.clouddisk.factory;

import com.zhiting.clouddisk.api.ApiService;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.RetrofitManager;

public class ApiServiceFactory {

    private static final Object LOCK = new Object();

    private static volatile ApiService API_SERVICE;

    public static ApiService getApiService() {
        if (API_SERVICE == null) {
            synchronized (LOCK) {
                if (API_SERVICE == null) {
                    API_SERVICE = RetrofitManager.getInstance(HttpConfig.baseTestUrl).create(ApiService.class);
                }
            }
        }
        return API_SERVICE;
    }

    public static void releaseApiService() {
        API_SERVICE = null;
    }
}
