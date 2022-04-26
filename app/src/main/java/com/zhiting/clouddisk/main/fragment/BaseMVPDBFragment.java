package com.zhiting.clouddisk.main.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.event.HomeRemoveEvent;
import com.zhiting.clouddisk.main.activity.Login2Activity;
import com.zhiting.clouddisk.main.activity.LoginActivity;
import com.zhiting.clouddisk.main.activity.MainActivity;
import com.zhiting.clouddisk.popup_window.FamilyPopupWindow;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.base.fragment.BaseFragment;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseMVPDBFragment <DB extends ViewDataBinding, V extends IView, P extends IPresenter<V>> extends BaseFragment {
    public P mPresenter;
    public DB binding;

    protected FamilyPopupWindow familyPopupWindow;

    protected Map<String, String> map = new HashMap<>();

    private ConfirmDialog mInvalidTokenDialog;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mPresenter = getInstance(this,2);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        if (binding == null){
            binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initInvalidTokenDialogDialog();
        familyPopupWindow = new FamilyPopupWindow((BaseActivity) getActivity());

        familyPopupWindow.setSelectFamilyListener(new FamilyPopupWindow.OnSelectFamilyListener() {
            @Override
            public void selectedFamily(AuthBackBean authBackBean) {
                selectedHome(authBackBean);
                familyPopupWindow.dismiss();
            }
        });

        familyPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                familyPopupWindowDismiss();
            }
        });
        refreshAuth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
    }

    @Override
    public void showError(int errorCode, String msg) {
        super.showError(errorCode, msg);
        if (errorCode == ErrorConstant.INVALID_AUTH){
            showInvalidTokenDialog();
        }
    }

    /**
     * 初始化token失效弹窗
     */
    private void initInvalidTokenDialogDialog(){
        mInvalidTokenDialog  = ConfirmDialog.getInstance();

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
        if(!mInvalidTokenDialog.isShowing()){
            Bundle bundle = new Bundle();
            bundle.putString("title", UiUtil.getString(R.string.common_tips));
            bundle.putString("content", UiUtil.getString(R.string.common_invalid_auth));
            bundle.putBoolean("cancelable", false);
            mInvalidTokenDialog.setArguments(bundle);
            mInvalidTokenDialog.show(this);
        }
    }

    /**
     * 选中家庭之后的操作
     * @param authBackBean
     */
    protected void selectedHome(AuthBackBean authBackBean){
        Constant.authBackBean = authBackBean;
        Constant.cookies = Constant.authBackBean.getCookies();
        Constant.scope_token = Constant.authBackBean.getStBean().getToken();//scopeToken
        Constant.USER_ID = Constant.authBackBean.getUserId();//用户 id
        Constant.userName = Constant.authBackBean.getUserName();//用户名称
        Constant.currentHome = Constant.authBackBean.getHomeCompanyBean();//家庭
        Constant.AREA_ID = Constant.currentHome.getId();
        BaseConstant.AREA_ID = Constant.AREA_ID;
        BaseConstant.SCOPE_TOKEN = Constant.scope_token;
        Constant.HOME_NAME = Constant.currentHome.getName();
        SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
        SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());
        String saLanAddress = Constant.currentHome.getSa_lan_address();
        HttpConfig.baseTestUrl = TextUtils.isEmpty(saLanAddress) ? HttpUrlParams.SC_URL : saLanAddress;
    }

    /**
     * 家庭弹窗消失之后操作
     */
    protected void familyPopupWindowDismiss(){

    }

    /**
     * 设置资源id
     *
     * @return 资源id
     */
    protected abstract int getLayoutId();


    /**
     * 更新授权信息
     */
    protected void refreshAuth(){
//        List<HomeCompanyBean> homeCompanyBeans = new ArrayList<>();
//        if (Constant.authBackBean!=null) {
//            HomeCompanyBean homeCompanyBean = Constant.authBackBean.getHomeCompanyBean();
//            if (homeCompanyBean != null) {
//                homeCompanyBean.setSelected(true);
//                homeCompanyBeans.add(homeCompanyBean);
//                familyPopupWindow.setFamilyData(homeCompanyBeans);
//            }
//        }
//        List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(SpUtil.getString(Config.KEY_AUTH_INFO), new TypeToken<List<AuthBackBean>>() {
//        }.getType());
//        authBackList.get(0).setSelected(true);
//        familyPopupWindow.setFamilyData(authBackList);
    }


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
