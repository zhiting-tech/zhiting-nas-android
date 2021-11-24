package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.entity.mine.FolderListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.FolderContract;
import com.zhiting.clouddisk.request.UpdateFolderPwdRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class FolderModel extends FolderContract.Model {

    /**
     * 文件夹列表
     * @param scopeToken
     * @param map
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FolderListBean>> getFolderList(String scopeToken, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getFolderList(scopeToken, map);
    }

    /**
     * 修改文件密码
     * @param scopeToken
     * @param updateFolderPwdRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> updateFolderPwd(String scopeToken, UpdateFolderPwdRequest updateFolderPwdRequest) {
        return ApiServiceFactory.getApiService().updateFolderPwd(scopeToken, updateFolderPwdRequest);
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
