package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.mine.FolderBean;
import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.security.PublicKey;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface CreateFolderContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FolderDetailBean>> getFolderDetail(String scopeToken, long id);
        public abstract Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken);
        public abstract Observable<BaseResponseEntity<Object>> addFolder(String scopeToken, FolderDetailBean folder);
        public abstract Observable<BaseResponseEntity<Object>> removeFolder(String scopeToken, long id);
        public abstract Observable<BaseResponseEntity<Object>> updateFolder(String scopeToken, long id, FolderDetailBean folder);
    }

    interface Presenter extends IPresenter<View> {
        void getFolderDetail(String scopeToken, long id);
        void getStoragePools(String scopeToken);
        void addFolder(String scopeToken, FolderDetailBean folder);
        void removeFolder(String scopeToken, long id);
        void updateFolder(String scopeToken, long id, FolderDetailBean folder);
    }

    interface View extends IView {
        void getFolderDetailSuccess(FolderDetailBean folderDetailBean);
        void getFolderDetailFail(int errorCode, String msg);
        void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean);
        void getStoragePoolsFail(int errorCode, String msg);
        void addFolderSuccess();
        void addFolderFail(int errorCode, String msg);
        void removeFolderSuccess();
        void removeFolderFail(int errorCode, String msg);
        void updateFolderSuccess();
        void updateFolderFail(int errorCode, String msg);
    }
}
