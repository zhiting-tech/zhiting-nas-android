package com.zhiting.clouddisk.main.model;

import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


public class LoginModel extends LoginContract.Model {

    public LoginModel(){ }

    @Override
    public Observable<BaseResponseEntity<ChannelEntity>> getTempChannel(String areaId,String cookie, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getTempChannel(areaId, cookie,map);
    }
}
