package com.zhiting.clouddisk.util;

import android.text.TextUtils;

import com.zhiting.networklib.http.HttpConfig;

public class UrlUtil {
    public static String getUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.equals("") && !url.startsWith("http")) {
            return HttpConfig.baseTestUrl + "/api/plugin/wangpan/" + url;
        }
        return "";
    }
}
