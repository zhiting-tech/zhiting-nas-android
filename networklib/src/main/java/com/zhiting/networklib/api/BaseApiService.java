package com.zhiting.networklib.api;

import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

public interface BaseApiService {

    @GET("api/datatunnel")
    Observable<BaseResponseEntity<ChannelEntity>> getChannel(@Header("Area-ID") String areaId, @Header("Cookie") String cookie, @QueryMap Map<String, String> map);
}
