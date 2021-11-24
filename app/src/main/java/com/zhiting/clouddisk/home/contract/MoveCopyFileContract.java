package com.zhiting.clouddisk.home.contract;

import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface MoveCopyFileContract {
    abstract class Model extends BaseModel {

    }

    interface Presenter extends IPresenter<View> {

    }

    interface View extends IView {

    }
}
