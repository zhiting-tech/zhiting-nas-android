package com.zhiting.networklib.base.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;

import com.gyf.barlibrary.ImmersionBar;
import com.zhiting.networklib.R;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.dialog.LoadingDialog;
import com.zhiting.networklib.entity.AppEventEntity;
import com.zhiting.networklib.utils.AndroidBugsSolution;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity extends BasePermissionsActivity implements IView {

    public static final String SHARE_ANIM_KEY = "SHARE_ANIM_KEY";
    public String TAG = getClass().getSimpleName();

    // 加载框
    private LoadingDialog loadingDialog;
    protected ImmersionBar mImmersionBar;

    protected Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        LibLoader.addActivity(this);
        mImmersionBar = ImmersionBar.with(this).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        createView();
        AndroidBugsSolution.assistActivity(this, null); //adjustResize无效解决
        initUI();
        Intent intent = getIntent();
        if (intent != null) {
            initIntent(getIntent());
        }
        initData();
        if (bindEventBus()) {//注册EventBus
            if (!EventBus.getDefault().isRegistered(this)) {
                Log.e(TAG, "Base=注册===EventBus1===" + TAG);
                EventBus.getDefault().unregister(this);
                EventBus.getDefault().register(this);
            }
        }
        LogUtil.e("当前组件=="+TAG);
    }

    @Override
    public void showLoading() {
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    loadingDialog.reset();
                }
            });

            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    loadingDialog.stop();
                }
            });
        }
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void showError(int errorCode, String msg) {

    }


    @Override
    protected void onDestroy() {
        LibLoader.removeActivity(this);
        super.onDestroy();

        ImmersionBar.with(this).destroy();
        hideLoading();
        if (bindEventBus()) {//注册EventBus
            EventBus.getDefault().unregister(this);
        }
        AndroidUtil.hideSoftInput(this);
    }


    /**
     *  设置布局
     */
    public abstract void createView();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化各种监听事件
     */
    protected void initIntent(Intent intent) {
    }

    /**
     * 初始化界面控件
     */
    protected void initUI() {
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    //-------------------------------------------------------EventBus--------------------------------------------------

    /**
     * 是否需要绑定EventBus，默认是不需要的
     * 如果有需要绑定的界面，重写返回true即可
     *
     * @return
     */
    public boolean bindEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(String text) {
        onReceiveEvent(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(AppEventEntity entity) {
        onReceiveEvent(entity);
    }

    public void onReceiveEvent(String key) {

    }

    public void onReceiveEvent(AppEventEntity entity) {

    }

    //-------------------------------------------------------EventBus--------------------------------------------------


    //    界面之间的跳转
    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivityClearTask(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz,
                                          Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);

    }

    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void switchToWelcomeActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //分开写会覆盖了
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 共享控件动画启动Activity
     *
     * @param clazz        跳转的activity
     * @param bundle       参数
     * @param shareView    需要共享的view
     * @param shareElement 共享配置标签
     */
    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, shareView, shareElement);
        startActivity(intent);
    }

    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView) {
        switchToActivityWithTextShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_txt));
    }

    public void switchToActivityWithTextShareAnim(Class<?> clazz, View shareView) {
        switchToActivityWithTextShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_txt));
    }

    public void switchToActivityWithImageShareAnim(Class<?> clazz, Bundle bundle, View shareView) {
        switchToActivityWithTextShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_img));
    }

    public void switchToActivityWithImageShareAnim(Class<?> clazz, View shareView) {
        switchToActivityWithTextShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_img));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
}
