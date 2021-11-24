package com.zhiting.clouddisk.main.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.networklib.entity.AppEventEntity;
import com.zhiting.networklib.entity.BaseUrlEvent;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.R;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;
import com.zhiting.networklib.widget.StatusBarView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.ParameterizedType;

public abstract class BaseMVPDBActivity <DB extends ViewDataBinding, V extends IView, P extends IPresenter<V>> extends BaseActivity implements IView{

    public P mPresenter;
    public DB binding;

    private ConfirmDialog mInvalidTokenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = getInstance(this,2);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(BaseUrlEvent entity) {
        ChannelUtil.resetApiServiceFactory(entity.getBaseUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        AndroidUtil.hideSoftInput(this);
    }

    @Override
    public void createView() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    protected void initUI() {
        super.initUI();
        StatusBarView statusBarView = binding.getRoot().findViewById(R.id.sbView);
        mImmersionBar.statusBarView(statusBarView).statusBarDarkFont(true).init();
        initConfirmDialog();
    }

    @Override
    public void showError(int errorCode, String msg) {
        super.showError(errorCode, msg);
        if (errorCode == ErrorConstant.INVALID_AUTH){
            showInvalidTokenDialog();
        }
    }


    /**
     * 初始化确认弹窗
     */
    private void initConfirmDialog(){
        mInvalidTokenDialog  = ConfirmDialog.getInstance();
        mInvalidTokenDialog.setCancelable(false);
        mInvalidTokenDialog.setConfirmListener(new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                switchToActivity(LoginActivity.class);
                SpUtil.put("loginInfo", "");
                LibLoader.finishAllActivity();
            }
        });
    }

    /**
     * 显示token失效弹窗
     */
    private void showInvalidTokenDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("title", UiUtil.getString(com.zhiting.clouddisk.R.string.common_tips));
        bundle.putString("content", UiUtil.getString(com.zhiting.clouddisk.R.string.common_invalid_auth));
        bundle.putBoolean("cancelable", false);
        mInvalidTokenDialog.setArguments(bundle);
        mInvalidTokenDialog.show(this);
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
