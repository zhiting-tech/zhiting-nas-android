package com.zhiting.networklib.base.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;

import com.zhiting.networklib.R;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.widget.StatusBarView;

import java.lang.reflect.ParameterizedType;

public abstract class BaseMVPActivity<V extends IView, P extends IPresenter<V>> extends BaseActivity{

    public P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = getInstance(this,1);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
    }

    @Override
    public void createView() {
        setContentView(getLayoutId());
    }

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    protected void initUI() {
        super.initUI();
        StatusBarView statusBarView = findViewById(R.id.sbView);
        mImmersionBar.statusBarView(statusBarView).keyboardEnable(true).statusBarDarkFont(true).init();
    }

    private <P> P getInstance(Object o, int i){
        try {
            return ((Class<P>)((ParameterizedType)(o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
