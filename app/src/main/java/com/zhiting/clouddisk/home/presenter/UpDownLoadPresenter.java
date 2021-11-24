package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.UpDownLoadContract;
import com.zhiting.clouddisk.home.model.UpDownLoadModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class UpDownLoadPresenter extends BasePresenter<UpDownLoadModel, UpDownLoadContract.View> implements UpDownLoadContract.Presenter {

    @Override
    public UpDownLoadModel createModel() {
        return new UpDownLoadModel();
    }
}
