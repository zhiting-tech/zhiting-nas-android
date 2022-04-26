package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.mine.MemberDetailBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface MineContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<MemberDetailBean>> getUserDetail(String scopeToken, int id);
        public abstract Observable<BaseResponseEntity<Object>> logout();
    }

    interface Presenter extends IPresenter<View> {
        void getUserDetail(String scopeToken, int id);;
        void logout();
    }

    interface View extends IView {
        void getUserDetailSuccess(MemberDetailBean memberDetailBean);
        void getUserDetailFail(int errorCode, String msg);
        void logoutSuccess();
        void logoutFail(int errorCode, String msg);
    }
}
