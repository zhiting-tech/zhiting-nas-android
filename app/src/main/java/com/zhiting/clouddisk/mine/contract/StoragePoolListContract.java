package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.mine.DiskListBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface StoragePoolListContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken, Map<String, String> map);
        public abstract Observable<BaseResponseEntity<DiskListBean>> getDisks(String scopeToken);
        public abstract Observable<BaseResponseEntity<Object>> restartTask(String scopeToken, String id);
    }

    interface Presenter extends IPresenter<View> {
        void getStoragePools(String scopeToken, Map<String, String> map, boolean showLoading);
        void  getDisks(String scopeToken);
        void restartTask(String scopeToken, String id);
    }

    interface View extends IView {
        void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean);
        void getStoragePoolsFail(int errorCode, String msg);
        void getDisksSuccess(DiskListBean diskListBean);
        void getDisksFail(int errorCode, String msg);
        void restartTaskSuccess();
        void restartTaskFail(int errorCode, String msg);
    }
}
