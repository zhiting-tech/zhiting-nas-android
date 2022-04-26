package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.BackupContract;
import com.zhiting.clouddisk.home.model.BackupModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class BackupPresenter extends BasePresenter<BackupModel, BackupContract.View> implements BackupContract.Presenter {

    @Override
    public BackupModel createModel() {
        return new BackupModel();
    }
}
