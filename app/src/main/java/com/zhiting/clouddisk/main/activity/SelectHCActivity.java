package com.zhiting.clouddisk.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.SelectHCAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivitySelectHcactivityBinding;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.ScopeBean;
import com.zhiting.clouddisk.entity.UserInfoEntity;
import com.zhiting.clouddisk.event.FinishLoginEvent;
import com.zhiting.clouddisk.main.contract.SelectHCContract;
import com.zhiting.clouddisk.main.presenter.SelectHCPresenter;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.cookie.PersistentCookieStore;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 请选择家庭/公司
 */
public class SelectHCActivity extends BaseMVPDBActivity<ActivitySelectHcactivityBinding, SelectHCContract.View, SelectHCPresenter> implements SelectHCContract.View {

    private SelectHCAdapter mSelectHCAdapter;
    private int userId;
    private UserInfoEntity mUserInfoEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_hcactivity;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRv();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mUserInfoEntity = (UserInfoEntity) intent.getSerializableExtra(IntentConstant.BEAN);
        userId = mUserInfoEntity.getUser_id();
        mPresenter.getExtensionTokenList(userId, 2, true);
    }

    private void initRv() {
        mSelectHCAdapter = new SelectHCAdapter();
        binding.rvHC.setAdapter(mSelectHCAdapter)
                .setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                        mPresenter.getExtensionTokenList(userId, 2, false);
                    }
                });
        mSelectHCAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ExtensionTokenListBean.ExtensionTokenBean extensionTokenBean = mSelectHCAdapter.getItem(position);
                for (ExtensionTokenListBean.ExtensionTokenBean etb : mSelectHCAdapter.getData()) {
                    etb.setSelected(false);
                }
                extensionTokenBean.setSelected(true);
                mSelectHCAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 通过sc获取所有家庭扩展应用的token成功
     *
     * @param extensionTokenListBean
     */
    @Override
    public void getExtensionTokenListSuccess(ExtensionTokenListBean extensionTokenListBean) {
        binding.rvHC.finishRefresh(true);
        List<ExtensionTokenListBean.ExtensionTokenBean> extension_token_list = extensionTokenListBean.getExtension_token_list();
        if (CollectionUtil.isNotEmpty(extension_token_list)){
            extension_token_list.get(0).setSelected(true);
        }
        mSelectHCAdapter.setNewData(extension_token_list);
        binding.rvHC.showEmptyView(CollectionUtil.isEmpty(extension_token_list));
        binding.tvConfirm.setVisibility(CollectionUtil.isNotEmpty(extension_token_list) ? View.VISIBLE : View.GONE);
    }

    /**
     * 通过sc获取所有家庭扩展应用的token失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getExtensionTokenListFail(int errorCode, String msg) {
        binding.rvHC.finishRefresh(true);
    }

    /**
     * 去到主界面
     * @param extensionTokenBean
     */
    private void toMain(ExtensionTokenListBean.ExtensionTokenBean extensionTokenBean){
        Constant.authBackBean = new AuthBackBean();
        Constant.authBackBean.setUserId(extensionTokenBean.getSa_user_id());
        Constant.authBackBean.setUserName(mUserInfoEntity.getNickname());
        Constant.authBackBean.setStBean(new ScopeBean(extensionTokenBean.getToken()));
        HomeCompanyBean homeCompanyBean = new HomeCompanyBean(extensionTokenBean.getArea_name());
        homeCompanyBean.setId(Long.parseLong(extensionTokenBean.getArea_id()));
        homeCompanyBean.setArea_id(Long.parseLong(extensionTokenBean.getArea_id()));
        homeCompanyBean.setSa_user_token(extensionTokenBean.getToken());
        homeCompanyBean.setUser_id(extensionTokenBean.getSa_user_id());
        homeCompanyBean.setCloud_user_id(userId);
        homeCompanyBean.setSc_lan_address(HttpUrlParams.SC_URL);
        homeCompanyBean.setSa_id(extensionTokenBean.getSaid());

        Constant.authBackBean.setHomeCompanyBean(homeCompanyBean);
        Constant.authBackBean.setCookies(PersistentCookieStore.getInstance().get(HttpUrl.parse(HttpUrlParams.LOGIN2)));
        String backInfo = new Gson().toJson(Constant.authBackBean);
        SpUtil.put(Config.KEY_LOGIN_INFO, backInfo);

        Constant.cookies = Constant.authBackBean.getCookies();
        Constant.scope_token = Constant.authBackBean.getStBean().getToken();//scopeToken
        HttpConfig.baseTestUrl = HttpUrlParams.SC_URL;
        Constant.USER_ID = extensionTokenBean.getSa_user_id();//用户 id
        Constant.userName = Constant.authBackBean.getUserName();//用户名称

        Constant.currentHome = Constant.authBackBean.getHomeCompanyBean();//家庭
        if (Constant.currentHome == null) return;

        Constant.AREA_ID = Constant.currentHome.getId();
        BaseConstant.AREA_ID = Constant.AREA_ID;
        BaseConstant.SCOPE_TOKEN = Constant.scope_token;
        Constant.HOME_NAME = Constant.currentHome.getName();
        SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
        SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());
        HttpConfig.baseTestUrl = HttpUrlParams.SC_URL;
        saveCookie();
        EventBus.getDefault().post(new FinishLoginEvent());
        switchToActivity(MainActivity.class);
        finish();
    }

    /**
     * sp 保存cookie
     */
    private void saveCookie() {
        if (CollectionUtil.isNotEmpty(Constant.cookies)) {
            for (Cookie cookie : Constant.cookies) {
                if (!TextUtils.isEmpty(cookie.value())) {
                    String cookieStr = "_session_=" + cookie.value();
                    SpUtil.put(SpConstant.COOKIE, cookieStr);
                    break;
                }
            }
        }
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) {  // 返回
                finish();
            } else if (viewId == R.id.tvConfirm) {  // 确定
                ExtensionTokenListBean.ExtensionTokenBean extensionTokenBean = mSelectHCAdapter.getSelectedItem();
                if (extensionTokenBean != null) {
                    toMain(extensionTokenBean);
                } else {
                    ToastUtil.show(UiUtil.getString(R.string.main_please_select_home_company));
                }
            }
        }
    }
}