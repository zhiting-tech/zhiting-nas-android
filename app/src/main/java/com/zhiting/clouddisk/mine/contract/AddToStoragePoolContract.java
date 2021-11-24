package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.request.AddStoragePoolRequest;
import com.zhiting.clouddisk.request.CreateStoragePoolRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface AddToStoragePoolContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePools(String scopeToken, Map<String, String> map);
        public abstract Observable<BaseResponseEntity<Object>> createStoragePool(String scopeToken, CreateStoragePoolRequest createStoragePoolRequest);
        public abstract Observable<BaseResponseEntity<Object>> addToStoragePool(String scopeToken, AddStoragePoolRequest addStoragePoolRequest);
    }

    interface Presenter extends IPresenter<View> {
        void getStoragePools(String scopeToken);
        void createStoragePool(String scopeToken, CreateStoragePoolRequest createStoragePoolRequest);
        void addToStoragePool(String scopeToken, AddStoragePoolRequest addStoragePoolRequest);
    }

    interface View extends IView {
        void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean);
        void getStoragePoolsFail(int errorCode, String msg);
        void createStoragePoolSuccess();
        void createStoragePoolFail(int errorCode, String msg);
        void addToStoragePoolSuccess();
        void addToStoragePoolFail(int errorCode, String msg);
    }
}
