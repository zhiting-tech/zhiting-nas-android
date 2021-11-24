package com.zhiting.networklib.base.mvp;


import io.reactivex.rxjava3.disposables.Disposable;

public interface IModel {
    void addDisposable(Disposable disposable);
    void removeDisposal();
}
