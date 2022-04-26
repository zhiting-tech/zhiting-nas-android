package com.zhiting.networklib.http;

import com.zhiting.networklib.utils.HttpUtils;
import com.zhiting.networklib.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        HttpUrl oldHttpUrl = request.url();
        String oldRequestUrl = oldHttpUrl.toString();

        String newBaseUrl = HttpUtils.getHttpUrl(oldRequestUrl);

        if (oldRequestUrl.contains("datatunnel")) {
            newBaseUrl = "http://gz.sc.zhitingtech.com/api/datatunnel";
        }
        LogUtil.e("BaseUrlInterceptor="+newBaseUrl);
        HttpUrl newHttpUrl = HttpUrl.parse(newBaseUrl);
        HttpUrl newFullUrl = oldHttpUrl
                .newBuilder()
                .scheme(newHttpUrl.scheme())
                .host(newHttpUrl.host())
                .port(newHttpUrl.port())
                .build();
        return chain.proceed(builder.url(newFullUrl).build());
    }
}
