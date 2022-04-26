package com.zhiting.clouddisk.main.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.event.HomeRemoveEvent;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.networklib.entity.BaseUrlEvent;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.R;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;
import com.zhiting.networklib.widget.StatusBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseMVPDBActivity <DB extends ViewDataBinding, V extends IView, P extends IPresenter<V>> extends BaseActivity implements IView{

    public P mPresenter;
    public DB binding;

    protected ConfirmDialog mInvalidTokenDialog;

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

                SpUtil.put("loginInfo", "");
                List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(SpUtil.getString(Config.KEY_AUTH_INFO), new TypeToken<List<AuthBackBean>>() {
                }.getType());
                if (CollectionUtil.isNotEmpty(authBackList)) {
                    for (int i=0; i<authBackList.size(); i++){
                        AuthBackBean authBackBean = authBackList.get(i);
                        HomeCompanyBean homeCompanyBean = authBackBean.getHomeCompanyBean();
                        if (Constant.AREA_ID == homeCompanyBean.getId()) {
                            authBackList.remove(i);
                            break;
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(authBackList)) {  // 如果还有家庭
                    String authInfo = new Gson().toJson(authBackList);
                    SpUtil.put(Config.KEY_AUTH_INFO, authInfo);
                    EventBus.getDefault().post(new HomeRemoveEvent());
                    LibLoader.finishAllActivityExcludeCertain(MainActivity.class);
                } else {  // 没有家庭就到登录界面
                    SpUtil.put(Config.KEY_AUTH_INFO, "");
                    switchToActivity(Login2Activity.class);
                    LibLoader.finishAllActivity();
                }
                mInvalidTokenDialog.dismiss();
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
