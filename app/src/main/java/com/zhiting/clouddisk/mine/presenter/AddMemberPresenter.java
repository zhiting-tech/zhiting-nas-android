package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.mine.contract.AddMemberContract;
import com.zhiting.clouddisk.mine.contract.CreateFolderContract;
import com.zhiting.clouddisk.mine.model.AddMemberModel;
import com.zhiting.clouddisk.mine.model.CreateFolderModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class AddMemberPresenter extends BasePresenter<AddMemberModel, AddMemberContract.View> implements AddMemberContract.Presenter {

    @Override
    public AddMemberModel createModel() {
        return new AddMemberModel();
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
}
