package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.DownloadSettingContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class DownloadSettingModel extends DownloadSettingContract.Model {

    public DownloadSettingModel(){
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
