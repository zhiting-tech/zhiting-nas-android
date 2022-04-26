package com.zhiting.clouddisk.main.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhiting.clouddisk.CDApplication;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityLogin2Binding;
import com.zhiting.clouddisk.dialog.AgreementDialog;
import com.zhiting.clouddisk.dialog.NoHCTipsDialog;
import com.zhiting.clouddisk.entity.AreaCodeBean;
import com.zhiting.clouddisk.entity.AreasBean;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.entity.ScopeBean;
import com.zhiting.clouddisk.entity.UserInfoBean;
import com.zhiting.clouddisk.entity.UserInfoEntity;
import com.zhiting.clouddisk.event.FinishLoginEvent;
import com.zhiting.clouddisk.home.activity.WebActivity;
import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.clouddisk.main.presenter.LoginPresenter;
import com.zhiting.clouddisk.popup_window.AreaCodePopupWindow;
import com.zhiting.clouddisk.request.LoginRequest;
import com.zhiting.clouddisk.util.AreaCodeConstant;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.entity.ChannelEntity;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.RetrofitManager;
import com.zhiting.networklib.http.cookie.PersistentCookieStore;
import com.zhiting.networklib.utils.AgreementPolicyListener;
import com.zhiting.networklib.utils.AppUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 登录界面
 */
public class Login2Activity extends BaseMVPDBActivity<ActivityLogin2Binding, LoginContract.View, LoginPresenter> implements LoginContract.View, AgreementPolicyListener {

    private String mUri = "zt://com.yctc.zhiting/sign?type=1&user_package_name=com.yctc.zhiting";  // 启动智汀App地址
    private MyBroadcastReceiver mReceiver;  // 授权登录广播

    private AreaCodePopupWindow mAreaCodePopupWindow; // 区号选择弹窗
    private UserInfoEntity mUserInfoEntity;
    private NoHCTipsDialog mNoHCTipsDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login2;
    }

    @Override
    public void onHead() {
        binding.ivSel.setSelected(!binding.ivSel.isSelected());
    }

    @Override
    public void onAgreement() {
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
        bundle.putString(IntentConstant.WEB_URL, Constant.AGREEMENT_URL);
        switchToActivity(WebActivity.class, bundle);
    }

    @Override
    public void onPolicy() {
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
        bundle.putString(IntentConstant.WEB_URL, Constant.POLICY_URL);
        switchToActivity(WebActivity.class, bundle);
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        binding.llPhone.post(new Runnable() {
            @Override
            public void run() {
                int width = binding.llPhone.getWidth();
                initAreaCodePopupWindow(width);
            }
        });

        binding.tvAgreementPolicy.setHighlightColor(Color.TRANSPARENT);
        binding.tvAgreementPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvAgreementPolicy.setText(StringUtil.setAgreementAndPolicyStyle(UiUtil.getString(R.string.agree_user_agreement_and_private_policy), UiUtil.getColor(R.color.white), true, true, this));
        boolean agreed = SpUtil.getBoolean(Constant.AGREED); // 是否同意过用户协议
        if (agreed) {
            basePermissionTask();
        } else {
            showAgreementDialog();
        }
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        copyAssetFileToSD();
    }

    /**
     * 拷assets中数据库到手机sd
     */
    private void copyAssetFileToSD() {
        String fileName = GonetUtil.dbName;
        String fileRoot = GonetUtil.dbPath;
        String filePath = fileRoot + File.separator + fileName;
        File file = new File(filePath);
        LogUtil.e("fileRoot=path1=" + filePath);
        if ((file.exists() && file.length() == 0) || !file.exists()) {
            LogUtil.e("fileRoot=path2=" + filePath);
            BaseFileUtil.copyAssetData(fileName, fileRoot, () -> {
                initGonet();
            });
        } else {
            initGonet();
        }
    }

    private void initGonet() {
//        GonetUtil.initUploadManager();
//        GonetUtil.initDownloadManager();
        UiUtil.postDelayed(() -> {
            GonetUtil.initUploadManager();
            GonetUtil.initDownloadManager();
        }, 200);
    }

    /**
     * 区号选择弹窗
     */
    private void initAreaCodePopupWindow(int width) {
        List<AreaCodeBean> areaCodeData = new Gson().fromJson(AreaCodeConstant.AREA_CODE, new TypeToken<List<AreaCodeBean>>() {
        }.getType());
        mAreaCodePopupWindow = new AreaCodePopupWindow(this, width, areaCodeData);
        mAreaCodePopupWindow.setSelectedAreaCodeListener(new AreaCodePopupWindow.OnSelectedAreaCodeListener() {
            @Override
            public void selectedAreaCode(AreaCodeBean areaCodeBean) {
                binding.tvArea.setText("+" + areaCodeBean.getCode());
                mAreaCodePopupWindow.dismiss();
            }
        });
        mAreaCodePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.tvArea.setSelected(false);
            }
        });
    }

    /**
     * 初始化用户协议弹窗
     */
    private void showAgreementDialog() {
        AgreementDialog mAgreementDialog = new AgreementDialog(this, this);
        mAgreementDialog.setOnOperateListener(new AgreementDialog.OnOperateListener() {

            @Override
            public void onDisagree() {
                mAgreementDialog.dismiss();
                finish();
            }

            @Override
            public void onAgree() {
                SpUtil.put(Constant.AGREED, true);
                basePermissionTask();
                mAgreementDialog.dismiss();
            }
        });
        mAgreementDialog.show();
    }

    @Override
    protected void initData() {
        super.initData();
        //注册广播接受者,接收授权成功返回广播信息
        mReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //zt.com.yctc.zhiting.sign 自行定义action 即可
        intentFilter.addAction("zt.com.yctc.zhiting.sign");
        registerReceiver(mReceiver, intentFilter);

//        String loginInfo = SpUtil.getString(Config.KEY_LOGIN_INFO);
//        if (!TextUtils.isEmpty(loginInfo)) {
//            toMain(loginInfo, false);
//        }
        List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(SpUtil.getString(Config.KEY_AUTH_INFO), new TypeToken<List<AuthBackBean>>() {
        }.getType());
        if (CollectionUtil.isNotEmpty(authBackList))
            goMainAct(authBackList.get(0), false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinishLoginEvent event) {
        finish();
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.show(UiUtil.getString(R.string.common_exit_tip));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 登录成功
     *
     * @param loginBean
     */
    @Override
    public void loginSuccess(LoginBean loginBean) {
        HomeCompanyBean homeCompanyBean = new HomeCompanyBean();
        AuthBackBean authBackBean = new AuthBackBean();
        ScopeBean scopeBean = new ScopeBean();
        if (loginBean != null) {
            AreasBean areasBean = loginBean.getAreas();
            if (areasBean != null) {
                homeCompanyBean.setId(Long.parseLong(areasBean.getId()));
                homeCompanyBean.setName(areasBean.getName());
                homeCompanyBean.setIs_bind_sa(areasBean.isIs_bind_sa());
                homeCompanyBean.setUser_id(areasBean.getSa_user_id());
                homeCompanyBean.setSa_lan_address(areasBean.getSa_lan_address());
            }
            UserInfoBean userInfoBean = loginBean.getUser_info();
            if (userInfoBean != null) {
                Constant.scope_token = userInfoBean.getScope_token();//scopeToken
                Constant.USER_ID = userInfoBean.getUser_id();//用户 id
                Constant.userName = userInfoBean.getUser_name();//用户名称
                authBackBean.setUserId(userInfoBean.getUser_id());
                authBackBean.setUserName(userInfoBean.getUser_name());
                scopeBean.setToken(userInfoBean.getScope_token());
            }
            authBackBean.setHomeCompanyBean(homeCompanyBean);
            authBackBean.setStBean(scopeBean);
            String backInfo = new Gson().toJson(authBackBean);
            saveInfo(backInfo);
        }
    }

    /**
     * 登录成功
     *
     * @param loginEntity
     */
    @Override
    public void login2Success(LoginEntity loginEntity) {
        if (loginEntity != null) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(IntentConstant.BEAN, loginEntity.getUser_info());
//            switchToActivity(SelectHCActivity.class, bundle);
            mUserInfoEntity = loginEntity.getUser_info();
            if (mUserInfoEntity != null) {
                mPresenter.getExtensionTokenList(mUserInfoEntity.getUser_id(), 2, true);
            }
        }
    }

    /**
     * 登录失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void loginFail(int errorCode, String msg) {

    }

    /**
     * 通过sc获取所有家庭扩展应用的token成功
     *
     * @param extensionTokenListBean
     */
    @Override
    public void getExtensionTokenListSuccess(ExtensionTokenListBean extensionTokenListBean) {
        List<ExtensionTokenListBean.ExtensionTokenBean> extension_token_list = extensionTokenListBean.getExtension_token_list();
        if (CollectionUtil.isNotEmpty(extension_token_list)) {
            saveExtensionTokenInfo(extension_token_list);
        } else {
            showNoHcDialog();
        }
    }

    /**
     * 保存所有家庭扩展应用的token成功
     */
    private void saveExtensionTokenInfo(List<ExtensionTokenListBean.ExtensionTokenBean> extensionTokenList) {
        List<AuthBackBean> authBackList = new ArrayList<>();
        for (ExtensionTokenListBean.ExtensionTokenBean extensionToken : extensionTokenList) {
            AuthBackBean authBackBean = new AuthBackBean();
            authBackBean.setUserId(extensionToken.getSa_user_id());
            authBackBean.setUserName(mUserInfoEntity.getNickname());
            authBackBean.setStBean(new ScopeBean(extensionToken.getToken()));
            HomeCompanyBean homeCompanyBean = new HomeCompanyBean(extensionToken.getArea_name());
            homeCompanyBean.setId(Long.parseLong(extensionToken.getArea_id()));
            homeCompanyBean.setArea_id(Long.parseLong(extensionToken.getArea_id()));
            homeCompanyBean.setSa_user_token(extensionToken.getToken());
            homeCompanyBean.setUser_id(extensionToken.getSa_user_id());
            homeCompanyBean.setCloud_user_id(mUserInfoEntity.getUser_id());
            homeCompanyBean.setSc_lan_address(HttpUrlParams.SC_URL);
            homeCompanyBean.setSa_id(extensionToken.getSaid());

            authBackBean.setHomeCompanyBean(homeCompanyBean);
            authBackBean.setCookies(PersistentCookieStore.getInstance().get(HttpUrl.parse(HttpUrlParams.LOGIN2)));
            authBackList.add(authBackBean);
        }
        String authInfo = new Gson().toJson(authBackList);
        SpUtil.put(Config.KEY_AUTH_INFO, authInfo);

        goMainAct(authBackList.get(0), false);
    }

    /**
     * 不能通过账号密码登录弹窗
     */
    private void showNoHcDialog() {
        if (mNoHCTipsDialog == null) {
            mNoHCTipsDialog = new NoHCTipsDialog();
        }
        mNoHCTipsDialog.show(this);
    }

    /**
     * 去主界面
     */
    private void goMainAct(AuthBackBean authBackBean, boolean delay) {
        Constant.authBackBean = authBackBean;
        Constant.cookies = Constant.authBackBean.getCookies();
        Constant.scope_token = Constant.authBackBean.getStBean().getToken();//scopeToken
        Constant.USER_ID = Constant.authBackBean.getUserId();//用户 id
        Constant.userName = Constant.authBackBean.getUserName();//用户名称

        Constant.currentHome = Constant.authBackBean.getHomeCompanyBean();//家庭
        if (Constant.currentHome == null) return;

        Constant.AREA_ID = Constant.currentHome.getId();
        BaseConstant.AREA_ID = Constant.AREA_ID;
        BaseConstant.SCOPE_TOKEN = Constant.scope_token;
        Constant.HOME_NAME = Constant.currentHome.getName();
        SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
        SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());

        saveCookie();
        String urlSA = Constant.currentHome.getSa_lan_address();
        HttpConfig.baseSAHost = urlSA;
        HttpConfig.baseTestUrl = TextUtils.isEmpty(urlSA) ? HttpUrlParams.SC_URL : urlSA;
        String urlSC = Constant.currentHome.getSc_lan_address();

        boolean isSAEnvironment = Constant.currentHome.isSAEnvironment();
        SpUtil.put(SpConstant.IS_SA, isSAEnvironment);
        String remoteUrl = isSAEnvironment ? urlSA : urlSC;

        LogUtil.e("toMain1=", "12=" + Constant.scope_token);
        LogUtil.e("toMain2=", "12=" + Constant.USER_ID);
        LogUtil.e("toMain3=", "12=" + isSAEnvironment);
        LogUtil.e("toMain4=", "urlSA=" + urlSA);
        LogUtil.e("toMain5=", "urlSC=" + urlSC);

        saveCookie();
        setTempChannelUrl(isSAEnvironment, remoteUrl);
        finishCurrentActivity(delay);
    }

    /**
     * 通过sc获取所有家庭扩展应用的token失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getExtensionTokenListFail(int errorCode, String msg) {

    }


    /**
     * 接收授权登录广播
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 授权成功之后返回的信息
            String backInfo = intent.getStringExtra("backInfo");
            saveInfo(backInfo);
        }
    }

    /**
     * 保存授权信息
     *
     * @param backInfo
     */
    private void saveInfo(String backInfo) {
//        AuthBackBean authBackBean = GsonConverter.getGson().fromJson(backInfo, AuthBackBean.class);//授权信息
//        if (authBackBean == null) return;
        List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(backInfo, new TypeToken<List<AuthBackBean>>() {
        }.getType());//授权信息
//        authBackList.add(authBackBean);
        String authInfo = new Gson().toJson(authBackList);
        SpUtil.put(Config.KEY_AUTH_INFO, authInfo);
        if (CollectionUtil.isNotEmpty(authBackList))
            goMainAct(authBackList.get(0), true);
//        toMain(backInfo, true);
    }

    /**
     * 去主界面
     *
     * @param json
     */
    private void toMain(String json, boolean delay) {
        Constant.authBackBean = GsonConverter.getGson().fromJson(json, AuthBackBean.class);//授权信息
        if (Constant.authBackBean == null) return;

        AuthBackBean authBackBean = Constant.authBackBean;
        if (authBackBean == null) return;

        Constant.cookies = authBackBean.getCookies();
        Constant.scope_token = authBackBean.getStBean().getToken();//scopeToken

        Constant.USER_ID = authBackBean.getUserId();//用户 id
        Constant.userName = authBackBean.getUserName();//用户名称

        Constant.currentHome = authBackBean.getHomeCompanyBean();//家庭
        if (Constant.currentHome == null) return;

        Constant.AREA_ID = Constant.currentHome.getId();
        BaseConstant.AREA_ID = Constant.AREA_ID;
        BaseConstant.SCOPE_TOKEN = Constant.scope_token;
        Constant.HOME_NAME = Constant.currentHome.getName();
        SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
        SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());

        String urlSA = Constant.currentHome.getSa_lan_address();
        HttpConfig.baseSAHost = urlSA;
        String urlSC = Constant.currentHome.getSc_lan_address();

        boolean isSAEnvironment = Constant.currentHome.isSAEnvironment();
        SpUtil.put(SpConstant.IS_SA, isSAEnvironment);
        String remoteUrl = isSAEnvironment ? urlSA : urlSC;

        LogUtil.e("toMain1=", "12=" + Constant.scope_token);
        LogUtil.e("toMain2=", "12=" + Constant.USER_ID);
        LogUtil.e("toMain3=", "12=" + isSAEnvironment);
        LogUtil.e("toMain4=", "urlSA=" + urlSA);
        LogUtil.e("toMain5=", "urlSC=" + urlSC);

        saveCookie();
        setTempChannelUrl(isSAEnvironment, remoteUrl);
        finishCurrentActivity(delay);
    }

    private void setTempChannelUrl(boolean isSAEnvironment, String remoteUrl) {
        Log.e("setTempChannelUrl=", "url=" + remoteUrl);
        String tokenKey = SpUtil.getString(SpConstant.SA_TOKEN);
        String json = SpUtil.getString(tokenKey);
        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null && !TextUtils.isEmpty(channel.getHost())) {
                String tempChannel = RetrofitManager.HTTPS_HEAD + channel.getHost() + "/";
                HttpConfig.baseTestUrl = isSAEnvironment ? remoteUrl : tempChannel;
//                HttpConfig.baseTestUrl = HttpUtils.getHttpUrl(HttpConfig.baseTestUrl);
                ChannelUtil.resetApiServiceFactory(HttpConfig.baseTestUrl);
                LogUtil.e("setTempChannelUrl=" + HttpConfig.baseTestUrl);
            }
        }
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
     * 结束当前的Activity
     *
     * @param delay
     */
    public void finishCurrentActivity(boolean delay) {
        if (delay) {//是否需要延时, 主要解决oppo没有跳转到主界面，直接到桌面的问题
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        switchToActivity(MainActivity.class);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    /**
     * 错误提示
     *
     * @param tips
     */
    private void setErrorTip(boolean visible, String tips) {
        binding.tvTips.setText(tips);
        binding.tvTips.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvLogin) { // 登录
                String phone = binding.etPhone.getText().toString().trim();
                String pwd = binding.etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    binding.tvPhoneTips.setVisibility(View.VISIBLE);
                    return;
                }
                binding.tvPhoneTips.setVisibility(View.GONE);
                if (TextUtils.isEmpty(pwd)) {
                    binding.tvTips.setVisibility(View.VISIBLE);
                    return;
                }
                binding.tvTips.setVisibility(View.INVISIBLE);
                if (!binding.ivSel.isSelected()) {
                    ToastUtil.show(getResources().getString(R.string.please_check_agreement_and_policy));
                    return;
                }
                LoginRequest loginRequest = new LoginRequest(phone, pwd);
                mPresenter.login2(loginRequest);
            } else if (viewId == R.id.tvLoginFast) { // 智汀家庭云快速登录
                boolean zhiting = AppUtil.isMobile_spExist(CDApplication.getContext(), "com.zhiting");
                boolean zhiting_test = AppUtil.isMobile_spExist(CDApplication.getContext(), "com.yctc.zhiting");
                if (binding.ivSel.isSelected()) {
                    if (zhiting || zhiting_test) {
                        LogUtil.d("=================登录============");
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(mUri));//参数拼接在URI后面 type=1是授权页面,user_package_name使用者包名,后续参数可自行添加
                        intent.putExtra("needPermissions", "user,area");//这里Intent也可传递参数,但是一般情况下都会放到上面的URL中进行传递
                        intent.putExtra("appName", UiUtil.getString(R.string.to_third_party_name));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {//未安装智汀，提示用户安装
                        ToastUtil.show(getResources().getString(R.string.main_please_install_zhiting));
                    }
                } else {
                    ToastUtil.show(getResources().getString(R.string.please_check_agreement_and_policy));
                }
            } else if (viewId == R.id.ivVisible) { // 密码可不可见
                binding.ivVisible.setSelected(!binding.ivVisible.isSelected());
                if (binding.ivVisible.isSelected()) {
                    binding.etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    binding.etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.etPwd.setSelection(binding.etPwd.getText().length());
            } else if (viewId == R.id.ivSel) { // 同意协议
                binding.ivSel.setSelected(!binding.ivSel.isSelected());
            } else if (viewId == R.id.tvArea) {  // 区号
                if (mAreaCodePopupWindow != null && !mAreaCodePopupWindow.isShowing()) {
                    binding.tvArea.setSelected(true);
                    mAreaCodePopupWindow.showAsDropDown(binding.llPhone, -15, 10);
                }
            }
        }
    }
}