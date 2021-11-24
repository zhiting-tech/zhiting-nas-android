package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.FolderListBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.FolderContract;
import com.zhiting.clouddisk.mine.model.FolderModel;
import com.zhiting.clouddisk.request.UpdateFolderPwdRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.Map;

/**
 * 文件夹列表presenter层
 */
public class FolderPresenter extends BasePresenter<FolderModel, FolderContract.View> implements FolderContract.Presenter {
    @Override
    public FolderModel createModel() {
        return new FolderModel();
    }

    /**
     * 文件夹列表
     *
     * @param scopeToken
     * @param map
     */
    @Override
    public void getFolderList(String scopeToken, Map<String, String> map, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();

                executeObservable(mModel.getFolderList(scopeToken, map), new RequestDataCallback<FolderListBean>(showLoading) {
                    @Override
                    public void onSuccess(FolderListBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getFolderListSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getFolderFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 修改文件夹密码
     *
     * @param scopeToken
     * @param updateFolderPwdRequest
     */
    @Override
    public void updateFolderPwd(String scopeToken, UpdateFolderPwdRequest updateFolderPwdRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.updateFolderPwd(scopeToken, updateFolderPwdRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.updateFolderPwdSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.updateFolderPwdFail(errorCode, errorMessage);
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
