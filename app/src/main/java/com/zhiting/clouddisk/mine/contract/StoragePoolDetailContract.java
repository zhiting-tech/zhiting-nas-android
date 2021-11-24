package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.request.ModifyNameRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

/**
 * 存储池详情
 */
public interface StoragePoolDetailContract {

    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<StoragePoolDetailBean>> getStoragePoolDetail(String scopeToken, String name);
        public abstract Observable<BaseResponseEntity<Object>> modifyPoolName(String scopeToken, String name, ModifyNameRequest modifyNameRequest);
        public abstract Observable<BaseResponseEntity<Object>> removePool(String scopeToken, String id);
        public abstract Observable<BaseResponseEntity<Object>> removeTask(String scopeToken, String id);
        public abstract Observable<BaseResponseEntity<Object>> restartTask(String scopeToken, String id);
    }

    interface Presenter extends IPresenter<View> {
        void getStoragePoolDetail(String scopeToken, String name, boolean showLoading);
        void modifyPoolName(String scopeToken, String name, ModifyNameRequest modifyNameRequest);
        void removePool(String scopeToken, String id);
        void removeTask(String scopeToken, String id);
        void restartTask(String scopeToken, String id);
    }

    interface View extends IView {
        void getStoragePoolDetailSuccess(StoragePoolDetailBean storagePoolDetailBean);
        void getStoragePoolDetailFail(int errorCode, String msg);
        void modifyPoolNameSuccess();
        void modifyPoolNameFail(int errorCode, String msg);
        void removePoolSuccess();
        void removePoolFail(int errorCode, String msg);
        void removeTaskSuccess();
        void removeTaskFail(int errorCode, String msg);
        void restartTaskSuccess();
        void restartTaskFail(int errorCode, String msg);
    }
}
