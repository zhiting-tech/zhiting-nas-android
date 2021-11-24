package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.mine.MemberDetailBean;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.clouddisk.mine.model.MineModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class MinePresenter extends BasePresenter<MineModel, MineContract.View> implements MineContract.Presenter {
    private int failCount;//失败次数

    @Override
    public MineModel createModel() {
        return new MineModel();
    }

    /**
     * 用户详情
     *
     * @param scopeToken
     * @param id
     */
    @Override
    public void getUserDetail(String scopeToken, int id) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getUserDetail(scopeToken, id), new RequestDataCallback<MemberDetailBean>() {
                    @Override
                    public void onSuccess(MemberDetailBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getUserDetailSuccess(response);
                        }
                        failCount = 0;
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.getUserDetailFail(errorCode, errorMessage);
                        }
                        failCount++;
                        if (failCount <= 3) {
                            getUserDetail(scopeToken, id);
                        }
                    }
                });
            }
        });
    }
}
