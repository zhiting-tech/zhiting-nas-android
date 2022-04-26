package com.zhiting.clouddisk.main.model;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.clouddisk.request.LoginRequest;
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

    /**
     * 登录(废弃）
     * @param loginRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<LoginBean>> login(LoginRequest loginRequest) {
        return ApiServiceFactory.getApiService().login(loginRequest);
    }

    @Override
    public Observable<BaseResponseEntity<LoginEntity>> login2(LoginRequest loginRequest) {
        return ApiServiceFactory.getApiService().login2(loginRequest);
    }

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
