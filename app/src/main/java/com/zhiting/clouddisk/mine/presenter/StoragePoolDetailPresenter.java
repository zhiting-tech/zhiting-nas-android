package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.mine.contract.StoragePoolDetailContract;
import com.zhiting.clouddisk.mine.model.StoragePoolDetailModel;
import com.zhiting.clouddisk.request.ModifyNameRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class StoragePoolDetailPresenter extends BasePresenter<StoragePoolDetailModel, StoragePoolDetailContract.View> implements StoragePoolDetailContract.Presenter {

    @Override
    public StoragePoolDetailModel createModel() {
        return new StoragePoolDetailModel();
    }


    /**
     * 存储池详情
     *
     * @param scopeToken
     * @param name
     */
    @Override
    public void getStoragePoolDetail(String scopeToken, String name, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getStoragePoolDetail(scopeToken, name), new RequestDataCallback<StoragePoolDetailBean>(showLoading) {
                    @Override
                    public void onSuccess(StoragePoolDetailBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getStoragePoolDetailSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getStoragePoolDetailFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 编辑\重命名存储池
     *
     * @param scopeToken
     * @param name
     * @param modifyNameRequest
     */
    @Override
    public void modifyPoolName(String scopeToken, String name, ModifyNameRequest modifyNameRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.modifyPoolName(scopeToken, name, modifyNameRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.modifyPoolNameSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.modifyPoolNameFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除存储存储
     *
     * @param scopeToken
     * @param name
     */
    @Override
    public void removePool(String scopeToken, String name) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removePool(scopeToken, name), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removePoolSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removePoolFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除任务
     *
     * @param scopeToken
     * @param id
     */
    @Override
    public void removeTask(String scopeToken, String id) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removeTask(scopeToken, id), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removeTaskSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removeTaskFail(errorCode, errorMessage);
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
