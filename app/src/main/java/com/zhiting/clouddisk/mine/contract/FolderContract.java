package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.mine.FolderListBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.UpdateFolderPwdRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface FolderContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FolderListBean>> getFolderList(String scopeToken, Map<String, String> map);
        public abstract Observable<BaseResponseEntity<Object>> updateFolderPwd(String scopeToken, UpdateFolderPwdRequest updateFolderPwdRequest);
        public abstract Observable<BaseResponseEntity<Object>> removeTask(String scopeToken, String id);
        public abstract Observable<BaseResponseEntity<Object>> restartTask(String scopeToken, String id);
    }

    interface Presenter extends IPresenter<View> {
        void getFolderList(String scopeToken, Map<String, String> map, boolean showLoading);
        void updateFolderPwd(String scopeToken, UpdateFolderPwdRequest updateFolderPwdRequest);
        void removeTask(String scopeToken, String id);
        void restartTask(String scopeToken, String id);
    }

    interface View extends IView {
        void getFolderListSuccess(FolderListBean folderListBean);
        void getFolderFail(int errorCode, String msg);
        void updateFolderPwdSuccess();
        void updateFolderPwdFail(int errorCode, String msg);
        void removeTaskSuccess();
        void removeTaskFail(int errorCode, String msg);
        void restartTaskSuccess();
        void restartTaskFail(int errorCode, String msg);
    }
}
