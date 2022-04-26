package com.zhiting.clouddisk.util;

import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.RetrofitManager;
import com.zhiting.networklib.utils.HttpUtils;
import com.zhiting.networklib.utils.LogUtil;

/**
 * date : 2021/9/3 17:16
 * desc :
 */
public class ChannelUtil {

    public static void resetApiServiceFactory(String baseUrl) {
        LogUtil.e("resetApiServiceFactory=" + baseUrl);
        ApiServiceFactory.releaseApiService();
        RetrofitManager.releaseRetrofitManager();
        HttpConfig.baseUrl = baseUrl;
        HttpConfig.baseTestUrl = baseUrl;
//        HttpConfig.baseTestUrl = HttpUtils.getHttpUrl(baseUrl) ;
        GonetUtil.changeHost();
    }
}
