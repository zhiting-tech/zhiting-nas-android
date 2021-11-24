package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.home.contract.ShareFolderContract;
import com.zhiting.clouddisk.home.model.ShareFolderModel;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class ShareFolderPresenter extends BasePresenter<ShareFolderModel, ShareFolderContract.View> implements ShareFolderContract.Presenter {

    @Override
    public ShareFolderModel createModel() {
        return new ShareFolderModel();
    }

    /**
     * 获取成员列表
     *
     * @param scopeToken
     */
    @Override
    public void getMember(String scopeToken) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getMember(scopeToken), new RequestDataCallback<MemberBean>() {
                    @Override
                    public void onSuccess(MemberBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getMemberSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getMemberFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 共享
     *
     * @param scopeToken
     * @param shareRequest
     */
    @Override
    public void share(String scopeToken, ShareRequest shareRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.share(scopeToken, shareRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.shareSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.shareFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }
}
