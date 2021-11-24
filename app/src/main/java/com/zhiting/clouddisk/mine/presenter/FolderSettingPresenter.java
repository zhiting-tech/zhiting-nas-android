package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.FolderSettingBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.AddMemberContract;
import com.zhiting.clouddisk.mine.contract.FolderSettingContract;
import com.zhiting.clouddisk.mine.model.AddMemberModel;
import com.zhiting.clouddisk.mine.model.FolderSettingModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class FolderSettingPresenter extends BasePresenter<FolderSettingModel, FolderSettingContract.View> implements FolderSettingContract.Presenter {

    @Override
    public FolderSettingModel createModel() {
        return new FolderSettingModel();
    }

    /**
     * 获取设置
     *
     * @param scopeToken
     */
    @Override
    public void getSettingData(String scopeToken) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getSettingData(scopeToken), new RequestDataCallback<FolderSettingBean>() {
                    @Override
                    public void onSuccess(FolderSettingBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getSettingDataSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getSettingDataFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
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
                executeObservable(mModel.getStoragePools(scopeToken), new RequestDataCallback<StoragePoolListBean>() {
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
     * 保存文件夹设置
     *
     * @param scopeToken
     * @param folderSettingBean
     */
    @Override
    public void saveFolderSettingData(String scopeToken, FolderSettingBean folderSettingBean) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.saveFolderSettingData(scopeToken, folderSettingBean), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.saveFolderSettingDataSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.saveFolderSettingDataFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
