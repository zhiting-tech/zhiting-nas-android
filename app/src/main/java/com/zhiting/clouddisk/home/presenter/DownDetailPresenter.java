package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.DownDetailContract;
import com.zhiting.clouddisk.home.model.DownDetailModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

/**
 * 下载详情
 */
public class DownDetailPresenter extends BasePresenter<DownDetailModel, DownDetailContract.View> implements DownDetailContract.Presenter {

    @Override
    public DownDetailModel createModel() {
        return new DownDetailModel();
    }


}
