package com.zhiting.clouddisk.main.presenter;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.clouddisk.main.model.LoginModel;
import com.zhiting.clouddisk.request.LoginRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;


public class LoginPresenter extends BasePresenter<LoginModel, LoginContract.View> implements LoginContract.Presenter {
    @Override
    public LoginModel createModel() {
        return new LoginModel();
    }

    /**
     * 登录（废弃）
     *
     * @param loginRequest
     */
    @Override
    public void login(LoginRequest loginRequest) {
        executeObservable(mModel.login(loginRequest), new RequestDataCallback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean response) {
                super.onSuccess(response);
                if (mView != null) {
                    mView.loginSuccess(response);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
            }
        });
    }

    /**
     * 登录
     *
     * @param loginRequest
     */
    @Override
    public void login2(LoginRequest loginRequest) {
        executeObservable(mModel.login2(loginRequest), new RequestDataCallback<LoginEntity>() {
            @Override
            public void onSuccess(LoginEntity response) {
                super.onSuccess(response);
                if (mView != null) {
                    mView.login2Success(response);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.loginFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 通过sc获取所有家庭扩展应用的token
     *
     * @param userId
     * @param type
     */
    @Override
    public void getExtensionTokenList(int userId, int type, boolean showLoading) {
        executeObservable(mModel.getExtensionTokenList(userId, type), new RequestDataCallback<ExtensionTokenListBean>(showLoading) {
            @Override
            public void onSuccess(ExtensionTokenListBean response) {
                super.onSuccess(response);
                if (mView != null) {
                    mView.getExtensionTokenListSuccess(response);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getExtensionTokenListFail(errorCode, errorMessage);
                }
            }
        });
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
