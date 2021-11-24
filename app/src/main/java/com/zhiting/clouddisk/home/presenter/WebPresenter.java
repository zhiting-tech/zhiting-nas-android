package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.DownDetailContract;
import com.zhiting.clouddisk.home.contract.WebContract;
import com.zhiting.clouddisk.home.model.DownDetailModel;
import com.zhiting.clouddisk.home.model.WebModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

/**
 * 下载详情
 */
public class WebPresenter extends BasePresenter<WebModel, WebContract.View> implements WebContract.Presenter {

    @Override
    public WebModel createModel() {
        return new WebModel();
    }


}
