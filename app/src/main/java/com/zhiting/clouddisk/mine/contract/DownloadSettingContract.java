package com.zhiting.clouddisk.mine.contract;

import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface DownloadSettingContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<Object>> restartTask(String scopeToken, String id);
    }

    interface Presenter extends IPresenter<View> {
        void restartTask(String scopeToken, String id);
    }

    interface View extends IView {
        void restartTaskSuccess();

        void restartTaskFail(int errorCode, String msg);
    }
}
