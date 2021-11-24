package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.mine.contract.AddPartitionContract;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.clouddisk.mine.model.AddPartitionModel;
import com.zhiting.clouddisk.mine.model.MineModel;
import com.zhiting.clouddisk.request.AddPartitionRequest;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

/**
 * 添加分区
 */
public class AddPartitionPresenter extends BasePresenter<AddPartitionModel, AddPartitionContract.View> implements AddPartitionContract.Presenter {

    @Override
    public AddPartitionModel createModel() {
        return new AddPartitionModel();
    }


    /**
     * 添加分区
     *
     * @param scopeToken
     * @param addPartitionRequest
     */
    @Override
    public void addPartition(String scopeToken, AddPartitionRequest addPartitionRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.addPartition(scopeToken, addPartitionRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.addPartitionSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.addPartitionFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 编辑 存储池分区
     *
     * @param scopeToken
     * @param name
     * @param modifyPartitionRequest
     */
    @Override
    public void modifyPartition(String scopeToken, String name, ModifyPartitionRequest modifyPartitionRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.modifyPartition(scopeToken, name, modifyPartitionRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.modifyPartitionSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.modifyPartitionFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除 存储池分区
     *
     * @param scopeToken
     * @param name
     */
    @Override
    public void removePartition(String scopeToken, String name, PoolNameRequest poolNameRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removePartition(scopeToken, name, poolNameRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removePartitionSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removePartitionFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
