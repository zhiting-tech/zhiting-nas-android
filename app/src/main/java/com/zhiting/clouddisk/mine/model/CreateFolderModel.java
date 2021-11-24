package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.CreateFolderContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class CreateFolderModel extends CreateFolderContract.Model {

    public CreateFolderModel(){

    }

    /**
     * 文件夹详情
     * @param scopeToken
     * @param id
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FolderDetailBean>> getFolderDetail(String scopeToken, long id) {
        return ApiServiceFactory.getApiService().getFolderDetail(scopeToken, id);
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
     * 添加文件夹
     * @param scopeToken
     * @param folder
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> addFolder(String scopeToken, FolderDetailBean folder) {
        return ApiServiceFactory.getApiService().addFolder(scopeToken, folder);
    }

    /**
     * 删除文件夹
     * @param scopeToken
     * @param id
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removeFolder(String scopeToken, long id) {
        return ApiServiceFactory.getApiService().removeFolder(scopeToken, id);
    }

    /**
     * 修改文件夹
     * @param scopeToken
     * @param id
     * @param folder
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> updateFolder(String scopeToken, long id, FolderDetailBean folder) {
        return ApiServiceFactory.getApiService().updateFolder(scopeToken, id, folder);
    }
}
