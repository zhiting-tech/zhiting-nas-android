package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.entity.mine.FolderSettingBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.FolderSettingContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;

public class FolderSettingModel extends FolderSettingContract.Model {

    public FolderSettingModel(){

    }

    /**
     * 设置数据
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FolderSettingBean>> getSettingData(String scopeToken) {
        return ApiServiceFactory.getApiService().getFolderSettingData(scopeToken);
    }

    /**
     * 获取存储池列表
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken) {
        return ApiServiceFactory.getApiService().getStoragePoolList(scopeToken, new HashMap<>());
    }

    /**
     * 保存文件夹设置
     * @param scopeToken
     * @param folderSettingBean
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> saveFolderSettingData(String scopeToken, FolderSettingBean folderSettingBean) {
        return ApiServiceFactory.getApiService().saveFolderSetting(scopeToken, folderSettingBean);
    }
}
