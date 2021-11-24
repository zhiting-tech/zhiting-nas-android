package com.zhiting.clouddisk.share.contract;

import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface ShareContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FileListBean>> getShareFolders(String scopeToken);
    }

    interface Presenter extends IPresenter<View> {
        void getShareFolders(String scopeToken, boolean showLoading);
    }

    interface View extends IView {
        void getFilesSuccess(FileListBean files);
        void getFilesFail(int errorCode, String msg);
    }
}
