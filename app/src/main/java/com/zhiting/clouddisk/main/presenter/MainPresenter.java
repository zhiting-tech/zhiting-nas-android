package com.zhiting.clouddisk.main.presenter;

import com.zhiting.clouddisk.main.contract.MainContract;
import com.zhiting.clouddisk.main.model.MainModel;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

public class MainPresenter extends BasePresenter<MainModel, MainContract.View> implements MainContract.Presenter {
    @Override
    public MainModel createModel() {
        return new MainModel();
    }

    /**
     * 重命名
     *
     * @param scopeToken
     * @param path
     * @param name
     */
    @Override
    public void renameFile(String scopeToken, String path, NameRequest name) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.renameFile(scopeToken, path, name), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.renameSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.renameFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除文件/文件夹
     *
     * @param scopeToken
     */
    @Override
    public void removeFile(String scopeToken, ShareRequest shareRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removeFile(scopeToken, shareRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removeFileSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removeFileFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void getTempChannel(String areaId, String cookie, Map<String, String> map) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getTempChannel(areaId, cookie, map), new RequestDataCallback<ChannelEntity>() {
                    @Override
                    public void onSuccess(ChannelEntity response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.onChannelSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.onChannelFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
