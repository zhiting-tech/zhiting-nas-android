package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.FolderSettingBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface FolderSettingContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FolderSettingBean>> getSettingData(String scopeToken);
        public abstract Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken);
        public abstract Observable<BaseResponseEntity<Object>> saveFolderSettingData(String scopeToken, FolderSettingBean folderSettingBean);
    }

    interface Presenter extends IPresenter<View> {
        void getSettingData(String scopeToken);
        void getStoragePools(String scopeToken);
        void saveFolderSettingData(String scopeToken, FolderSettingBean folderSettingBean);
    }

    interface View extends IView {
        void getSettingDataSuccess(FolderSettingBean folderSettingBean);
        void getSettingDataFail(int errorCode, String msg);
        void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean);
        void getStoragePoolsFail(int errorCode, String msg);
        void saveFolderSettingDataSuccess();
        void saveFolderSettingDataFail(int errorCode, String msg);
    }

}
