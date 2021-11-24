package com.zhiting.clouddisk.main.model;

import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.main.contract.MainContract;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class MainModel extends MainContract.Model {

    public MainModel(){ }

    /**
     * 重命名
     * @param scopeToken
     * @param path
     * @param name
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> renameFile(String scopeToken, String path, NameRequest name) {
        return ApiServiceFactory.getApiService().renameFile(scopeToken, path, name);
    }

    /**
     * 删除文件/目录
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removeFile(String scopeToken, ShareRequest shareRequest) {
        return ApiServiceFactory.getApiService().removeFile(scopeToken, shareRequest);
    }

    /**
     * 获取临时通道
     * @param areaId
     * @param cookie
     * @param map
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<ChannelEntity>> getTempChannel(String areaId, String cookie, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getTempChannel(areaId, cookie,map);
    }
}
