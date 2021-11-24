package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.api.ApiService;
import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.mine.MemberDetailBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.http.RetrofitManager;

import io.reactivex.rxjava3.core.Observable;

public class MineModel extends MineContract.Model {

    public MineModel(){

    }

    /**
     * 用户详情
     * @param scopeToken
     * @param id
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<MemberDetailBean>> getUserDetail(String scopeToken, int id) {
        return ApiServiceFactory.getApiService().getUserDetail(scopeToken, id);
    }
}
