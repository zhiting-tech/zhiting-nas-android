package com.zhiting.clouddisk.home.contract;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface ShareFolderContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<MemberBean>> getMember(String scopeToken);
        public abstract Observable<BaseResponseEntity<Object>> share(String scopeToken, ShareRequest shareRequest);
    }

    interface Presenter extends IPresenter<ShareFolderContract.View> {
        void getMember(String scopeToken);
        void share(String scopeToken, ShareRequest shareRequest);
    }

    interface View extends IView {
        void getMemberSuccess(MemberBean memberBean);
        void getMemberFail(int errorCode, String msg);
        void shareSuccess();
        void shareFail(int errorCode, String msg);
    }
}
