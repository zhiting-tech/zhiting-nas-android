package com.zhiting.clouddisk.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.zhiting.clouddisk.CDApplication;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityLoginBinding;
import com.zhiting.clouddisk.dialog.AgreementDialog;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.home.activity.WebActivity;
import com.zhiting.clouddisk.main.contract.LoginContract;
import com.zhiting.clouddisk.main.presenter.LoginPresenter;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.clouddisk.util.FastUtil;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.entity.ChannelEntity;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.AgreementPolicyListener;
import com.zhiting.networklib.utils.AppUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.HttpUtils;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;
import com.zhiting.networklib.utils.toast.ToastUtil;

import okhttp3.Cookie;

/**
 * 登录界面
 */
public class LoginActivity extends BaseMVPDBActivity<ActivityLoginBinding, LoginContract.View, LoginPresenter> implements LoginContract.View, AgreementPolicyListener {

    private String mUri = "zt://com.yctc.zhiting/sign?type=1&user_package_name=com.yctc.zhiting";  // 启动智汀App地址
    private MyBroadcastReceiver mReceiver;  // 授权登录广播
    private boolean isDelay;//是否延时
    private long currentTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        binding.tvAgreementPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvAgreementPolicy.setText(StringUtil.setAgreementAndPolicyStyle(UiUtil.getString(R.string.agree_user_agreement_and_private_policy), UiUtil.getColor(R.color.color_2da3f6), true,true, this));
        boolean agreed = SpUtil.getBoolean(Constant.AGREED); // 是否同意过用户协议
        if (agreed) {
            basePermissionTask();
        } else {
            showAgreementDialog();
        }

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

        String loginInfo = SpUtil.getString(Config.KEY_LOGIN_INFO);
        if (!TextUtils.isEmpty(loginInfo)) {
            toMain(loginInfo, false);
        }
    }


    @Override
    public void onHead() {

    }

    /**
     * 查看用户协议
     */
    @Override
    public void onAgreement() {
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
        bundle.putString(IntentConstant.WEB_URL, Constant.AGREEMENT_URL);
        switchToActivity(WebActivity.class, bundle);
    }

    /**
     * 查看隐私政策
     */
    @Override
    public void onPolicy() {
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
        bundle.putString(IntentConstant.WEB_URL, Constant.POLICY_URL);
        switchToActivity(WebActivity.class, bundle);
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

    @Override
    public void loginSuccess(LoginBean loginBean) {

    }

    @Override
    public void login2Success(LoginEntity loginEntity) {

    }

    @Override
    public void loginFail(int errorCode, String msg) {

    }

    @Override
    public void getExtensionTokenListSuccess(ExtensionTokenListBean extensionTokenListBean) {

    }

    @Override
    public void getExtensionTokenListFail(int errorCode, String msg) {

    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvLogin) { // 登录
                if (binding.ivSel.isSelected()) {
                    if (AppUtil.isMobile_spExist(CDApplication.getContext(), "com.zhiting")) {
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
            } else if (viewId == R.id.ivSel) { // 同意用户协议
                binding.ivSel.setSelected(!binding.ivSel.isSelected());
            }
        }
    }

    /**
     * 接收授权登录广播
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 授权成功之后返回的信息
            String backInfo = intent.getStringExtra("backInfo");
            SpUtil.put("loginInfo", backInfo);
            toMain(backInfo, true);
        }
    }

    /**
     * 去主界面
     *
     * @param json
     */
    private void toMain(String json, boolean delay) {
        isDelay = delay;
        Constant.authBackBean = GsonConverter.getGson().fromJson(json, AuthBackBean.class);//授权信息
        if (Constant.authBackBean == null) return;

        AuthBackBean authBackBean = Constant.authBackBean;
        if (authBackBean == null) return;

        Constant.cookies = authBackBean.getCookies();
        Constant.scope_token = authBackBean.getStBean().getToken();//scopeToken
        BaseConstant.SCOPE_TOKEN = authBackBean.getStBean().getToken();//scopeToken
        Constant.USER_ID = authBackBean.getUserId();//用户 id
        Constant.userName = authBackBean.getUserName();//用户名称

        Constant.currentHome = authBackBean.getHomeCompanyBean();//家庭
        if (Constant.currentHome == null) return;

        Constant.AREA_ID = Constant.currentHome.getId();
        BaseConstant.AREA_ID = Constant.currentHome.getId();
        Constant.HOME_NAME = Constant.currentHome.getName();
        SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
        SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());

        String urlSA = Constant.currentHome.getSa_lan_address();
        String urlSC = Constant.currentHome.getSc_lan_address();

        boolean isSAEnvironment = Constant.currentHome.isSAEnvironment();
        SpUtil.put(SpConstant.IS_SA,isSAEnvironment);
        String remoteUrl = isSAEnvironment ? urlSA : urlSC;

        LogUtil.e("toMain1=", "12=" + Constant.scope_token);
        LogUtil.e("toMain2=", "12=" + Constant.USER_ID);
        LogUtil.e("toMain3=", "12=" + isSAEnvironment);
        LogUtil.e("toMain4=", "urlSA=" + urlSA);
        LogUtil.e("toMain5=", "urlSC=" + urlSC);

        saveCookie();
        setTempChannelUrl(delay, remoteUrl);
        finishCurrentActivity(delay);
    }

    private void setTempChannelUrl(boolean delay, String remoteUrl) {
        Log.e("setTempChannelUrl=", "url=" + remoteUrl);
        String tokenKey = SpUtil.getString(SpConstant.SA_TOKEN);
        String json = SpUtil.getString(tokenKey);
        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null && !TextUtils.isEmpty(channel.getHost())) {
                String tempChannel = "http://" + channel.getHost() + "/";
                HttpConfig.baseTestUrl = delay ? remoteUrl : tempChannel;
                HttpConfig.baseTestUrl = HttpUtils.getHttpUrl(HttpConfig.baseTestUrl);
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
}