package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.mine.contract.DownloadSettingContract;
import com.zhiting.clouddisk.mine.model.DownloadSettingModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class DownloadSettingPresenter extends BasePresenter<DownloadSettingModel, DownloadSettingContract.View> implements DownloadSettingContract.Presenter {

    @Override
    public DownloadSettingModel createModel() {
        return new DownloadSettingModel();
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
