package com.zhiting.clouddisk.main.contract;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.request.LoginRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


public interface LoginContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<ChannelEntity>> getTempChannel(String areaId, String cookie, Map<String, String> map);
        public abstract Observable<BaseResponseEntity<LoginBean>> login(LoginRequest loginRequest);
        public abstract Observable<BaseResponseEntity<LoginEntity>> login2(LoginRequest loginRequest);
        public abstract Observable<BaseResponseEntity<ExtensionTokenListBean>> getExtensionTokenList(int userId, int type);
    }

    interface Presenter extends IPresenter<View> {
//        void getTempChannel(String areaId,String cookie, Map<String, String> map);
        void login(LoginRequest loginRequest);
        void login2(LoginRequest loginRequest);
        void getExtensionTokenList(int userId, int type, boolean showLoading);
    }

    interface View extends IView{
//        void onChannelSuccess(ChannelEntity response);
//        void onChannelFail(int errorCode, String msg);
        void loginSuccess(LoginBean loginBean);
        void login2Success(LoginEntity loginEntity);
        void loginFail(int errorCode, String msg);
        void getExtensionTokenListSuccess(ExtensionTokenListBean extensionTokenListBean);
        void getExtensionTokenListFail(int errorCode, String msg);
    }
}
