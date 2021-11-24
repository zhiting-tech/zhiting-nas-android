package com.zhiting.networklib.base.mvp;

public interface IPresenter<V extends IView> {

    /**
     * 绑定view
     * @param View
     */
    void attachView(V View);

    /**
     * 解绑view
     */
    void detachView();
}
