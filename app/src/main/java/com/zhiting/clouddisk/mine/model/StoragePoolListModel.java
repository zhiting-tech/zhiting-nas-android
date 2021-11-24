package com.zhiting.clouddisk.mine.model;


import com.zhiting.clouddisk.entity.mine.DiskListBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.StoragePoolListContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class StoragePoolListModel extends StoragePoolListContract.Model {

    public StoragePoolListModel(){

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
     * 闲置硬盘列表（
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<DiskListBean>> getDisks(String scopeToken) {
        return ApiServiceFactory.getApiService().getDisks(scopeToken);
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
