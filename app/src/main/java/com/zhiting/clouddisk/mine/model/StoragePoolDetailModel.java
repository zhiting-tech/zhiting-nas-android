package com.zhiting.clouddisk.mine.model;


import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.StoragePoolDetailContract;
import com.zhiting.clouddisk.request.ModifyNameRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class StoragePoolDetailModel extends StoragePoolDetailContract.Model {

    public StoragePoolDetailModel(){

    }

    /**
     * 存储池详情
     * @param scopeToken
     * @param name
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<StoragePoolDetailBean>> getStoragePoolDetail(String scopeToken, String name) {
        return  ApiServiceFactory.getApiService().getStoragePoolDetail(scopeToken, name);
    }

    /**
     * 编辑\重命名存储池
     * @param scopeToken
     * @param name
     * @param modifyNameRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> modifyPoolName(String scopeToken, String name, ModifyNameRequest modifyNameRequest) {
        return ApiServiceFactory.getApiService().modifyPoolName(scopeToken, name, modifyNameRequest);
    }

    /**
     * 删除存储池
     * @param scopeToken
     * @param name
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removePool(String scopeToken, String name) {
        return ApiServiceFactory.getApiService().removePool(scopeToken, name);
    }

    /**
     * 删除任务
     * @param scopeToken
     * @param id
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removeTask(String scopeToken, String id) {
        return ApiServiceFactory.getApiService().removeTask(scopeToken, id);
    }

    /**
     * 重新开始任务
     * @param scopeToken
     * @param id
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> restartTask(String scopeToken, String id) {
        return ApiServiceFactory.getApiService().restartTask(scopeToken, id);
    }

}
