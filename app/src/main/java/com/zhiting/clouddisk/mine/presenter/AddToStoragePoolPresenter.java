package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.AddToStoragePoolContract;
import com.zhiting.clouddisk.mine.model.AddToStoragePoolModel;
import com.zhiting.clouddisk.request.AddStoragePoolRequest;
import com.zhiting.clouddisk.request.CreateStoragePoolRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.HashMap;

public class AddToStoragePoolPresenter extends BasePresenter<AddToStoragePoolModel, AddToStoragePoolContract.View> implements AddToStoragePoolContract.Presenter {

    @Override
    public AddToStoragePoolModel createModel() {
        return new AddToStoragePoolModel();
    }

    /**
     * 获取存储池列表
     *
     * @param scopeToken
     */
    @Override
    public void getStoragePools(String scopeToken) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getStoragePools(scopeToken, new HashMap<>()), new RequestDataCallback<StoragePoolListBean>() {
                    @Override
                    public void onSuccess(StoragePoolListBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getStoragePoolsSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getStoragePoolsFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 创建存储池
     *
     * @param scopeToken
     * @param createStoragePoolRequest
     */
    @Override
    public void createStoragePool(String scopeToken, CreateStoragePoolRequest createStoragePoolRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.createStoragePool(scopeToken, createStoragePoolRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.createStoragePoolSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.createStoragePoolFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 添加硬盘到存储池
     *
     * @param scopeToken
     * @param addStoragePoolRequest
     */
    @Override
    public void addToStoragePool(String scopeToken, AddStoragePoolRequest addStoragePoolRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.addToStoragePool(scopeToken, addStoragePoolRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.addToStoragePoolSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.addToStoragePoolFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
