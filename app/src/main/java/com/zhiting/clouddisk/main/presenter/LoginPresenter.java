package com.zhiting.clouddisk.main.presenter;

import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.clouddisk.main.model.LoginModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

public class LoginPresenter extends BasePresenter<LoginModel, LoginContract.View> implements LoginContract.Presenter {
    @Override
    public LoginModel createModel() {
        return new LoginModel();
    }

//    @Override
//    public void getTempChannel(String areaId, String cookie, Map<String, String> map) {
//        checkTempChannel(new OnTempChannelListener() {
//            @Override
//            public void onSuccess() {
//                super.onSuccess();
//                executeObservable(mModel.getTempChannel(areaId, cookie, map), new RequestDataCallback<ChannelEntity>() {
//                    @Override
//                    public void onSuccess(ChannelEntity response) {
//                        super.onSuccess(response);
//                        if (mView != null) {
//                            mView.onChannelSuccess(response);
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(int errorCode, String errorMessage) {
//                        super.onFailed(errorCode, errorMessage);
//                        if (mView != null) {
//                            mView.onChannelFail(errorCode, errorMessage);
//                        }
//                    }
//                });
//            }
//        });
//    }
}
