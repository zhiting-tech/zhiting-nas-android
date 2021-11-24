package com.zhiting.networklib.base.mvp;

/**
 * date : 2021/7/614:50
 * desc :
 */
public abstract class RequestDataCallback<T> {
    private boolean isLoading = true;

    public boolean isLoading() {
        return isLoading;
    }

    public RequestDataCallback(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public RequestDataCallback() {

    }

    public void onSuccess(T response) {
    }

    public void onFailed(int errorCode, String errorMessage) {
    }

    public void onFailed(){

    }
}
