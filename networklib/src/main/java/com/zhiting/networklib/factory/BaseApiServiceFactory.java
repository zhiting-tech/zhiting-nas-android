package com.zhiting.networklib.factory;

import com.zhiting.networklib.api.BaseApiService;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.RetrofitManager;

public class BaseApiServiceFactory {

    private static final Object LOCK = new Object();

    private static volatile BaseApiService API_SERVICE;

    public static BaseApiService getApiService() {
        if (API_SERVICE == null) {
            synchronized (LOCK) {
                if (API_SERVICE == null) {
                    API_SERVICE = RetrofitManager.getInstance(HttpConfig.baseSCUrl).create(BaseApiService.class);
                }
            }
        }
        return API_SERVICE;
    }
}
