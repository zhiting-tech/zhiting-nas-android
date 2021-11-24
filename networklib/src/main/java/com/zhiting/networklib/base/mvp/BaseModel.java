package com.zhiting.networklib.base.mvp;


import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseModel implements IModel {

    private CompositeDisposable mCompositeDisposable = null;

    @Override
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void removeDisposal() {
        if (mCompositeDisposable!=null){
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }
}
