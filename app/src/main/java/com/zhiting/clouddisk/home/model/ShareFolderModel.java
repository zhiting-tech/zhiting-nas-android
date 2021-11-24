package com.zhiting.clouddisk.home.model;


import com.zhiting.clouddisk.api.ApiService;
import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.home.contract.ShareFolderContract;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class ShareFolderModel extends ShareFolderContract.Model {

    /**
     * 获取成员
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<MemberBean>> getMember(String scopeToken) {
        return ApiServiceFactory.getApiService().getMembers(scopeToken);
    }

    /**
     * 共享
     * @param scopeToken
     * @param shareRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> share(String scopeToken, ShareRequest shareRequest) {
        return ApiServiceFactory.getApiService().share(scopeToken, shareRequest);
    }
}
