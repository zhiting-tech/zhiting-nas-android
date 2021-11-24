package com.zhiting.networklib.utils;

import android.text.TextUtils;

public class HttpUtils {
    public static String getHttpUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("https")) {
            url = url.replace("https", "http");
            return url;
        }
        return url;
    }
}
