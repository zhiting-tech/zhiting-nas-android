package com.zhiting.networklib.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.zhiting.networklib.R;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.base.mvp.IView;
import com.zhiting.networklib.dialog.LoadingDialog;
import com.zhiting.networklib.entity.AppEventEntity;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public abstract class BaseFragment extends Fragment implements IView {

    public String TAG = "当前组件=========" + getClass().getSimpleName() + "=======";
    public String TAG_NAME = getClass().getSimpleName() + "=======";

    // 视图是否加载完毕
    private boolean isViewPrepare = false;

    // 是否加载过数据
    private boolean hasLoadData = false;

    public View mContentView;
    public Intent intent = new Intent();
    public Bundle bundle = new Bundle();
    public Fragment currentFragment = new Fragment();

    private boolean showLoading = false;

    // 加载框
    private LoadingDialog loadingDialog;
    public static final String SHARE_ANIM_KEY = "SHARE_ANIM_KEY";

    protected Context mContext;
    protected Activity mActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (bindEventBus()) {//注册EventBus
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
                EventBus.getDefault().register(this);
                LogUtil.e("Base=注册===EventBus2===" + TAG);
            }
        }
        return createView(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
        isViewPrepare = true;
        initUI(view);
        lazyLoadDataPrepared();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && hasLoadData){
            lazyLoad();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            AndroidUtil.hideSoftInput(mActivity);
        }else if (hasLoadData){
            lazyLoad();
        }
    }

    /**
     * 视图可见且没加载数据
     */
    private void lazyLoadDataPrepared(){
        if (getUserVisibleHint() && isViewPrepare && !hasLoadData){
            lazyLoad();
            hasLoadData = true;
        }
    }

    /**
     * 懒加载
     */
    protected abstract void lazyLoad();

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
        if (bindEventBus()) {//注册EventBus
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void showLoading() {
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getContext());
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


    public abstract View createView(LayoutInflater inflater, ViewGroup container);



    /**
     * 初始化控件
     */
    protected void initUI(View view) {

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

    //-------------------------------------------------------EventBus---------------------------------------------------


    //    界面之间的跳转
    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
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
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
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
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), shareView, shareElement);
        startActivity(intent, options.toBundle());
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

    public void switchToActivityForResultShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement,int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), shareView, shareElement);
        startActivityForResult(intent, requestCode,options.toBundle());
    }

    public void switchToActivityResultTextShareAnim(Class<?> clazz, Bundle bundle, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_txt),requestCode);
    }

    public void switchToActivityResultTextShareAnim(Class<?> clazz, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_txt),requestCode);
    }

    public void switchToActivityResultImageShareAnim(Class<?> clazz, Bundle bundle, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_img),requestCode);
    }

    public void switchToActivityResultImageShareAnim(Class<?> clazz, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_img),requestCode);
    }
}
