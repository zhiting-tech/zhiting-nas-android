package com.zhiting.networklib.base.mvp;

public interface IView {
    void showLoading();
    void hideLoading();
    void showError(String msg);
    void showError(int errorCode, String msg);
}
