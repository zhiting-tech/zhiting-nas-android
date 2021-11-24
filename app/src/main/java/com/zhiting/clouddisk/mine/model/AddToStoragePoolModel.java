package com.zhiting.clouddisk.mine.model;


import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.AddToStoragePoolContract;
import com.zhiting.clouddisk.request.AddStoragePoolRequest;
import com.zhiting.clouddisk.request.CreateStoragePoolRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class AddToStoragePoolModel extends AddToStoragePoolContract.Model {

    public AddToStoragePoolModel(){

    }

    /**
     * 获取存储池列表
     * @param scopeToken
     * @param map
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getStoragePoolList(scopeToken, map);
    }

    /**
     * 创建存储池
     * @param scopeToken
     * @param createStoragePoolRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> createStoragePool(String scopeToken, CreateStoragePoolRequest createStoragePoolRequest) {
        return ApiServiceFactory.getApiService().createStoragePool(scopeToken, createStoragePoolRequest);
    }


    /**
     * 添加硬盘到存储池
     * @param scopeToken
     * @param addStoragePoolRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> addToStoragePool(String scopeToken, AddStoragePoolRequest addStoragePoolRequest) {
        return  ApiServiceFactory.getApiService().addStoragePool(scopeToken, addStoragePoolRequest);
    }

}
