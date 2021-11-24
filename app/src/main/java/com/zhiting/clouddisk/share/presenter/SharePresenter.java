package com.zhiting.clouddisk.share.presenter;

import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.home.contract.HomeContract;
import com.zhiting.clouddisk.home.model.HomeModel;
import com.zhiting.clouddisk.share.contract.ShareContract;
import com.zhiting.clouddisk.share.model.ShareModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.List;

public class SharePresenter extends BasePresenter<ShareModel, ShareContract.View> implements ShareContract.Presenter {
    private int failCount;//失败次数

    @Override
    public ShareModel createModel() {
        return new ShareModel();
    }

    /**
     * 共享文件夹列表
     *
     * @param scopeToken
     * @param showLoading
     */
    @Override
    public void getShareFolders(String scopeToken, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getShareFolders(scopeToken), new RequestDataCallback<FileListBean>(showLoading) {
                    @Override
                    public void onSuccess(FileListBean response) {
                        super.onSuccess(response);
                        mView.getFilesSuccess(response);
                        failCount = 0;
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        failCount++;
                        if (failCount <= 3) {
                            getShareFolders(scopeToken, false);
                        }
                    }
                });
            }
        });
    }
}
