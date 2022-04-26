package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.DiskListBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.clouddisk.mine.contract.StoragePoolListContract;
import com.zhiting.clouddisk.mine.model.MineModel;
import com.zhiting.clouddisk.mine.model.StoragePoolListModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.Map;

public class StoragePoolListPresenter extends BasePresenter<StoragePoolListModel, StoragePoolListContract.View> implements StoragePoolListContract.Presenter {

    @Override
    public StoragePoolListModel createModel() {
        return new StoragePoolListModel();
    }

    /**
     * 获取存储池列表
     *
     * @param scopeToken
     * @param map
     * @param showLoading
     */
    @Override
    public void getStoragePools(String scopeToken, Map<String, String> map, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getStoragePools(scopeToken, map), new RequestDataCallback<StoragePoolListBean>(showLoading) {
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
     * 闲置硬盘列表
     *
     * @param scopeToken
     */
    @Override
    public void getDisks(String scopeToken) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getDisks(scopeToken), new RequestDataCallback<DiskListBean>(false) {
                    @Override
                    public void onSuccess(DiskListBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getDisksSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getDisksFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 重新开始任务
     *
     * @param scopeToken
     * @param id
     */
    @Override
    public void restartTask(String scopeToken, String id) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.restartTask(scopeToken, id), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.restartTaskSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.restartTaskFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
