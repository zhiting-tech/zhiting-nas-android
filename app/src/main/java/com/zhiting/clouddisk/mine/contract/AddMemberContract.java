package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface AddMemberContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<MemberBean>> getMember(String scopeToken);
    }

    interface Presenter extends IPresenter<View> {
        void getMember(String scopeToken);
    }

    interface View extends IView {
        void getMemberSuccess(MemberBean memberBean);
        void getMemberFail(int errorCode, String msg);
    }

}
