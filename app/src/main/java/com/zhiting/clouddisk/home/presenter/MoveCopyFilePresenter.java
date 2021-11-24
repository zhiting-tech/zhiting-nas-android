package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.MoveCopyFileContract;
import com.zhiting.clouddisk.home.model.MoveCopyFileModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class MoveCopyFilePresenter extends BasePresenter<MoveCopyFileModel, MoveCopyFileContract.View> implements MoveCopyFileContract.Presenter {

    @Override
    public MoveCopyFileModel createModel() {
        return new MoveCopyFileModel();
    }
}
