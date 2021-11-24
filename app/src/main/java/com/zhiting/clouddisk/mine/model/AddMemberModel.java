package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.AddMemberContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class AddMemberModel extends AddMemberContract.Model {

    public AddMemberModel(){

    }

    /**
     * 获取成员
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<MemberBean>> getMember(String scopeToken) {
        return ApiServiceFactory.getApiService().getMembers(scopeToken);
    }
}
