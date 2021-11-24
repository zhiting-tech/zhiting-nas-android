package com.zhiting.clouddisk.main.contract;

import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface MainContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<Object>> renameFile(String scopeToken, String path, NameRequest name);

        public abstract Observable<BaseResponseEntity<Object>> removeFile(String scopeToken, ShareRequest shareRequest);

        public abstract Observable<BaseResponseEntity<ChannelEntity>> getTempChannel(String areaId, String cookie, Map<String, String> map);
    }

    interface Presenter extends IPresenter<View> {
        void renameFile(String scopeToken, String path, NameRequest name);

        void removeFile(String scopeToken, ShareRequest shareRequest);

        void getTempChannel(String areaId, String cookie, Map<String, String> map);
    }

    interface View extends IView {
        void renameSuccess();

        void renameFail(int errorCode, String msg);

        void removeFileSuccess();

        void removeFileFail(int errorCode, String msg);

        void onChannelSuccess(ChannelEntity response);

        void onChannelFail(int errorCode, String msg);
    }
}
