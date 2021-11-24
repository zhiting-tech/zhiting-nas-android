package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.UploadContract;
import com.zhiting.clouddisk.home.model.UploadModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class UploadPresenter extends BasePresenter<UploadModel, UploadContract.View> implements UploadContract.Presenter {

    @Override
    public UploadModel createModel() {
        return new UploadModel();
    }
}
