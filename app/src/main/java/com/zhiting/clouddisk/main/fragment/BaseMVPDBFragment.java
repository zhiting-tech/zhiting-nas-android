package com.zhiting.clouddisk.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.main.activity.LoginActivity;
import com.zhiting.clouddisk.popup_window.FamilyPopupWindow;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.base.fragment.BaseFragment;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
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
            public void selectedFamily(HomeCompanyBean homeCompanyBean) {
                selectedHome(homeCompanyBean);
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
                switchToActivity(LoginActivity.class);
                SpUtil.put("loginInfo", "");
                mActivity.finish();
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
     * @param homeCompanyBean
     */
    protected void selectedHome(HomeCompanyBean homeCompanyBean){

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
        List<HomeCompanyBean> homeCompanyBeans = new ArrayList<>();
        if (Constant.authBackBean!=null) {
            HomeCompanyBean homeCompanyBean = Constant.authBackBean.getHomeCompanyBean();
            if (homeCompanyBean != null) {
                homeCompanyBean.setSelected(true);
                homeCompanyBeans.add(homeCompanyBean);
                familyPopupWindow.setFamilyData(homeCompanyBeans);
            }
        }
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
