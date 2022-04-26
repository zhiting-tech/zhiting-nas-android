package com.zhiting.clouddisk.main.model;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.main.contract.SelectHCContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class SelectHCModel extends SelectHCContract.Model {
    /**
     * 通过sc获取所有家庭扩展应用的token
     *
     * @param userId
     * @param type
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<ExtensionTokenListBean>> getExtensionTokenList(int userId, int type) {
        return ApiServiceFactory.getApiService().getExtensionTokenList(userId, type);
    }
}
