package com.zhiting.clouddisk.main.presenter;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.main.contract.SelectHCContract;
import com.zhiting.clouddisk.main.model.SelectHCModel;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

public class SelectHCPresenter extends BasePresenter<SelectHCModel, SelectHCContract.View> implements SelectHCContract.Presenter {
    @Override
    public SelectHCModel createModel() {
        return new SelectHCModel();
    }

    /**
     * 通过sc获取所有家庭扩展应用的token
     *
     * @param userId
     * @param type
     */
    @Override
    public void getExtensionTokenList(int userId, int type, boolean showLoading) {
        executeObservable(mModel.getExtensionTokenList(userId, type), new RequestDataCallback<ExtensionTokenListBean>(showLoading) {
            @Override
            public void onSuccess(ExtensionTokenListBean response) {
                super.onSuccess(response);
                if (mView != null) {
                    mView.getExtensionTokenListSuccess(response);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getExtensionTokenListFail(errorCode, errorMessage);
                }
            }
        });
    }
}
