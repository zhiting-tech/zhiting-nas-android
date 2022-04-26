package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.home.contract.VideoContract;
import com.zhiting.clouddisk.home.model.VideoModel;
import com.zhiting.networklib.base.mvp.BasePresenter;

public class VideoPresenter extends BasePresenter<VideoModel, VideoContract.View> implements VideoContract.Presenter {

    @Override
    public VideoModel createModel() {
        return new VideoModel();
    }
}
