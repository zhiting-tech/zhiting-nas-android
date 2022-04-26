package com.zhiting.clouddisk.main.contract;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.request.LoginRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface SelectHCContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<ExtensionTokenListBean>> getExtensionTokenList(int userId, int type);
    }

    interface Presenter extends IPresenter<View> {
        void getExtensionTokenList(int userId, int type, boolean showLoading);
    }

    interface View extends IView {
        void getExtensionTokenListSuccess(ExtensionTokenListBean extensionTokenListBean);
        void getExtensionTokenListFail(int errorCode, String msg);
    }
}
