package com.zhiting.clouddisk.mine.presenter;

import com.zhiting.clouddisk.mine.contract.BackupSettingContract;
import com.zhiting.clouddisk.mine.model.BackupSettingModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class BackupSettingPresenter extends BasePresenter<BackupSettingModel, BackupSettingContract.View> {
    @Override
    public BackupSettingModel createModel() {
        return new BackupSettingModel();
    }
}
