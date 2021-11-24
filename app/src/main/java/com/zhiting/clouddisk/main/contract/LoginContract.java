package com.zhiting.clouddisk.main.contract;

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
    }

    interface Presenter extends IPresenter<View> {
//        void getTempChannel(String areaId,String cookie, Map<String, String> map);
    }

    interface View extends IView{
//        void onChannelSuccess(ChannelEntity response);
//        void onChannelFail(int errorCode, String msg);
    }
}
