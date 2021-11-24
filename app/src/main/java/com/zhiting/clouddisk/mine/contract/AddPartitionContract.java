package com.zhiting.clouddisk.mine.contract;

import com.zhiting.clouddisk.request.AddPartitionRequest;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import io.reactivex.rxjava3.core.Observable;

public interface AddPartitionContract {

    abstract class Model extends BaseModel {
        public abstract  Observable<BaseResponseEntity<Object>> addPartition(String scopeToken, AddPartitionRequest addPartitionRequest);
        public abstract Observable<BaseResponseEntity<Object>> modifyPartition(String scopeToken,String name, ModifyPartitionRequest modifyPartitionRequest);
        public abstract Observable<BaseResponseEntity<Object>> removePartition(String scopeToken,String name, PoolNameRequest poolNameRequest);
    }

    interface Presenter extends IPresenter<View> {
        void addPartition(String scopeToken, AddPartitionRequest addPartitionRequest);
        void modifyPartition(String scopeToken, String name, ModifyPartitionRequest modifyPartitionRequest);
        void removePartition(String scopeToken,String name, PoolNameRequest poolNameRequest);
    }

    interface View extends IView {
        void addPartitionSuccess();
        void addPartitionFail(int errorCode, String msg);
        void modifyPartitionSuccess();
        void modifyPartitionFail(int errorCode, String msg);
        void removePartitionSuccess();
        void removePartitionFail(int errorCode, String msg);
    }
}
