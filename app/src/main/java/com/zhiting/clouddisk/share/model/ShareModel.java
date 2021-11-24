package com.zhiting.clouddisk.share.model;

import com.zhiting.clouddisk.api.ApiService;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.share.contract.ShareContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.http.RetrofitManager;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class ShareModel extends ShareContract.Model {

    public ShareModel(){
    }

    /**
     * 共享文件夹
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FileListBean>> getShareFolders(String scopeToken) {
        return ApiServiceFactory.getApiService().getShareFolders(scopeToken);
    }
}
