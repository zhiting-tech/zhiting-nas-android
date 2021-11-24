package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.CreateFolderContract;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.clouddisk.mine.model.CreateFolderModel;
import com.zhiting.clouddisk.mine.model.MineModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.Map;

public class CreateFolderPresenter extends BasePresenter<CreateFolderModel, CreateFolderContract.View> implements CreateFolderContract.Presenter {

    @Override
    public CreateFolderModel createModel() {
        return new CreateFolderModel();
    }

    /**
     * 文件夹详情
     *
     * @param scopeToken
     * @param id
     */
    @Override
    public void getFolderDetail(String scopeToken, long id) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getFolderDetail(scopeToken, id), new RequestDataCallback<FolderDetailBean>() {
                    @Override
                    public void onSuccess(FolderDetailBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getFolderDetailSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getFolderDetailFail(errorCode, errorMessage);
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
     * 添加文件夹
     *
     * @param scopeToken
     * @param folder
     */
    @Override
    public void addFolder(String scopeToken, FolderDetailBean folder) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.addFolder(scopeToken, folder), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.addFolderSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.addFolderFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除文件夹
     *
     * @param scopeToken
     * @param id
     */
    @Override
    public void removeFolder(String scopeToken, long id) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removeFolder(scopeToken, id), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removeFolderSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removeFolderFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 修改文件夹
     *
     * @param scopeToken
     * @param id
     * @param folder
     */
    @Override
    public void updateFolder(String scopeToken, long id, FolderDetailBean folder) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.updateFolder(scopeToken, id, folder), new RequestDataCallback<Object>(false) {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.updateFolderSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.updateFolderFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
