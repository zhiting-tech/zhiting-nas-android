package com.zhiting.networklib.base.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.lang.reflect.ParameterizedType;

public abstract class BaseMVPFragment <V extends IView, P extends IPresenter<V>> extends BaseFragment {


    public P mPresenter;

    @Override
    public View createView(LayoutInflater inflater,ViewGroup container) {
        mPresenter = getInstance(this,1);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        if (mContentView == null){
            mContentView = inflater.inflate(getLayoutId(), null);
        }else {
            // 不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewParent parent = mContentView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
    }

    /**
     * 设置资源id
     *
     * @return 资源id
     */
    protected abstract int getLayoutId();


    private <T> T getInstance(Object o, int i){
        try {
            return ((Class<T>)((ParameterizedType)(o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
