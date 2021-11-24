package com.zhiting.clouddisk.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileOperateAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityMainBinding;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.RenameFileDialog;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.event.CancelFileOperateEvent;
import com.zhiting.clouddisk.event.OperateFileEvent;
import com.zhiting.clouddisk.home.activity.MoveCopyFileActivity;
import com.zhiting.clouddisk.home.activity.ShareFolderActivity;
import com.zhiting.clouddisk.home.fragment.HomeFragment;
import com.zhiting.clouddisk.main.contract.MainContract;
import com.zhiting.clouddisk.main.presenter.MainPresenter;
import com.zhiting.clouddisk.mine.fragment.MineFragment;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.clouddisk.share.fragment.ShareFragment;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.clouddisk.util.FastUtil;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.entity.ChannelEntity;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.HttpUtils;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

/**
 * dev1.4.0
 */
public class MainActivity extends BaseMVPDBActivity<ActivityMainBinding, MainContract.View, MainPresenter> implements MainContract.View, BottomNavigationView.OnNavigationItemSelectedListener {

    private HomeFragment homeFragment;
    private ShareFragment shareFragment;
    private MineFragment mineFragment;

    private int mIndex = 0;
    private final int FRAGMENT_HOME = 0;
    private final int FRAGMENT_SHARE = 1;
    private final int FRAGMENT_MINE = 2;
    private final String HOME_TAG = "home";
    private final String SHARE_TAG = "share";
    private final String MINE_TAG = "mine";

    private FileOperateAdapter fileOperateAdapter;  // 文件操作列表适配器
    private List<FileOperateBean> mOperateData = new ArrayList<>(); // 文件操作列表数据
    private List<FileBean> folders; // 选中的文件

    private RenameFileDialog renameFileDialog;
    private WifiReceiver mWifiReceiver;

    /******************************** 重写方法 ******************************/
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.navigation.setItemIconTintList(null);
        binding.navigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        initFragment();
        initRvOperateFile();
        basePermissionTask();
        initGonet();
    }

    private void initGonet() {
        UiUtil.postDelayed(() -> {
            GonetUtil.initUploadManager();
            GonetUtil.initDownloadManager();
        }, 200);
    }

    @Override
    protected void initData() {
        super.initData();
        mWifiReceiver = new WifiReceiver();
        registerWifiReceiver();
    }

    /**
     * Wifi 状态监听注册
     */
    private void registerWifiReceiver() {
        if (mWifiReceiver == null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {//断开wifi
                    switchNetwork(false);
                    GonetUtil.netWorkNotice();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {//连接wifi
                        connectWifiHandle(context);
                    }
                }
            }
        }
    }

    /**
     * 连接wifi成功后的操作
     *
     * @param context
     */
    private void connectWifiHandle(Context context) {
        UiUtil.postDelayed(() -> {
            WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //LogUtil.e(TAG, "网络=连接到网络11 " + wifiInfo.getSSID());
            //LogUtil.e(TAG, "网络=连接到网络22 " + GsonConverter.getGson().toJson(wifiInfo));
            //LogUtil.e(TAG, "网络=连接到网络33 " + wifiInfo.getBSSID() + "-" + Constant.currentHome.getMac_address());
            //LogUtil.e(TAG, "网络=连接到网络44 " + wifiInfo.getMacAddress());
            boolean isSASAEnvironment = checkSA(wifiInfo);
            switchNetwork(isSASAEnvironment);
        }, 100);
    }

    /**
     * 切换网络
     *
     * @param isSA
     */
    private void switchNetwork(boolean isSA) {
        if (Constant.currentHome == null) return;
        SpUtil.put(SpConstant.IS_SA, isSA);
        Constant.currentHome.setSAEnvironment(isSA);
        if (isSA) {
            String url = Constant.currentHome.getSa_lan_address();
            ChannelUtil.resetApiServiceFactory(url);
        } else {
            checkTempChannelWithinTime();
        }
    }

    /**
     * 处理临时通道
     */
    private void checkTempChannelWithinTime() {
        HomeCompanyBean home = Constant.currentHome;
        String channelEntityJson = SpUtil.getString(home.getSa_user_token());
        if (!TextUtils.isEmpty(channelEntityJson)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(channelEntityJson, ChannelEntity.class);
            if (channel != null) {
                long currentTime = TimeFormatUtil.getCurrentTime();
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    String newUrl = getUrl(home.getSc_lan_address(), channel.getHost());
                    ChannelUtil.resetApiServiceFactory(newUrl);
                    LogUtil.e(TAG, "baseUrl2=" + newUrl);
                    return;
                }
            }
        }
        UiUtil.postDelayed(() -> requestTempChannelUrl(home), 200);
    }

    /**
     * 通过域名获网络请求地址,临时通道是走http
     *
     * @param host
     * @return
     */
    private String getUrl(String oldUrl, String host) {
        String url = "";
        if (!TextUtils.isEmpty(host)) {
            url = oldUrl.replace(HttpConfig.baseSCHost, host);
        }
        LogUtil.e("getUrl=" + url);
        return HttpUtils.getHttpUrl(url);
    }

    /**
     * 获取临时通道
     */
    private void requestTempChannelUrl(HomeCompanyBean home) {
        Map<String, String> map = new HashMap<>();
        map.put("scheme", "http");
        String cookieStr = "";
        if (CollectionUtil.isNotEmpty(Constant.cookies)) {
            for (Cookie cookie : Constant.cookies) {
                if (!TextUtils.isEmpty(cookie.value())) {
                    cookieStr = "_session_=" + cookie.value();
                    break;
                }
            }
        }
        mPresenter.getTempChannel(String.valueOf(home.getId()), cookieStr, map);
    }

    /**
     * 检测是否SA环境
     *
     * @param wifiInfo
     * @return
     */
    private boolean checkSA(WifiInfo wifiInfo) {
        if (wifiInfo != null && wifiInfo.getBSSID() != null &&
                Constant.currentHome != null && !TextUtils.isEmpty(Constant.currentHome.getMac_address())) {
            if (wifiInfo.getBSSID().equalsIgnoreCase(Constant.currentHome.getMac_address())) {
                return true;
            }
        }
        return false;
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
            BaseFileUtil.copyAssetData(fileName, fileRoot);
        }
    }

    /**
     * 文件的操作
     */
    private void initRvOperateFile() {
        binding.rvOperate.setLayoutManager(new GridLayoutManager(this, 6));
        fileOperateAdapter = new FileOperateAdapter();
        binding.rvOperate.setAdapter(fileOperateAdapter);
        mOperateData.add(new FileOperateBean(R.drawable.icon_share, UiUtil.getString(R.string.home_share), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_download, UiUtil.getString(R.string.home_download), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_move, UiUtil.getString(R.string.home_move), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_copy, UiUtil.getString(R.string.home_copy), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_rename, UiUtil.getString(R.string.home_rename), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_remove, UiUtil.getString(R.string.home_remove), true));
        fileOperateAdapter.setNewData(mOperateData);
        fileOperateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileOperateBean fileOperateBean = fileOperateAdapter.getItem(position);
                if (fileOperateBean.isEnabled()) {
                    switch (position) {
                        case 0:  // 共享
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("originalWrite", FileUtil.hasWritePermission(folders));
                            bundle.putBoolean("originalDel", FileUtil.hasDelPermission(folders));
                            bundle.putSerializable("folder", (Serializable) folders);

                            switchToActivity(ShareFolderActivity.class, bundle);
                            break;

                        case 1:  // 下载
//                            downloadFiles(fileDetailAdapter.getSelectedData());
                            break;

                        case 2:  // 移动

                            break;

                        case 3:  // 复制到
                            toMoveCopyActivity(getFolderPath());
                            break;

                        case 4:  // 重命名
                            FileBean fileBean = folders.get(0);
                            int drawableRes = R.drawable.icon_file_big;
                            if (fileBean.getType() == 0) {
                                drawableRes = TextUtils.isEmpty(fileBean.getFrom_user()) ? R.drawable.icon_file_big : R.drawable.icon_share_folder_big;
                            } else {
                                /**
                                 * 1. word
                                 * 2. excel
                                 * 3. ppt
                                 * 4. 压缩包
                                 * 5. 图片
                                 * 6. 音频
                                 * 7. 视频
                                 * 8. 文本
                                 *
                                 */
                                int fileType = FileTypeUtil.fileType(fileBean.getName());
                                drawableRes = FileTypeUtil.getFileBigLogo(fileType);
                            }
                            showCreateFileDialog(drawableRes, fileBean);
                            break;

                        case 5:  // 删除
                            showRemoveFileTips(getFolderPath());
                            break;
                    }
                }
            }
        });
    }

    /**
     * 获取文件夹的路径
     *
     * @return
     */
    private List<String> getFolderPath() {
        List<String> paths = new ArrayList<>();
        for (FileBean folder : folders) {
            paths.add(folder.getPath());
        }
        return paths;
    }


    /**
     * 创建/重新命名 文件夹
     */
    private void showCreateFileDialog(int drawable, FileBean fileBean) {
        renameFileDialog = RenameFileDialog.getInstance(1, drawable, fileBean.getName());
        renameFileDialog.setCompleteListener(new RenameFileDialog.OnCompleteListener() {
            @Override
            public void onComplete(int type, String fileName) {
                // 重新命名
                String[] fn = fileName.split("\\."); // 修改之后的名称
                String[] ofn = fileBean.getName().split("\\."); // 修改之前的名称
                int fnLength = fn.length; // 修改之后的名称长度
                int ofnLength = ofn.length; // 修改之前的名称场地
                if (fnLength != ofnLength) {
                    showFileTypeChangeDialog(fileName, fileBean);
                } else {
                    if (fnLength > 1 && ofnLength > 1) {  // 文件类型改变
                        if (!fn[fnLength - 1].equalsIgnoreCase(ofn[ofnLength - 1])) {
                            showFileTypeChangeDialog(fileName, fileBean);
                        } else {
                            mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
                        }
                    } else {
                        mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
                    }
                }
            }

            @Override
            public void onDismissListener(DialogInterface dialog) {

            }
        });
//        renameFileDialog.setCompleteListener((kind, fileName) -> {
//                 // 重新命名
//                String[] fn = fileName.split("\\."); // 修改之后的名称
//                String[] ofn = fileBean.getName().split("\\."); // 修改之前的名称
//                int fnLength = fn.length; // 修改之后的名称长度
//                int ofnLength = ofn.length; // 修改之前的名称场地
//                if (fnLength != ofnLength) {
//                    showFileTypeChangeDialog(fileName, fileBean);
//                } else {
//                    if (fnLength > 1 && ofnLength > 1) {  // 文件类型改变
//                        if (!fn[fnLength - 1].equalsIgnoreCase(ofn[ofnLength - 1])) {
//                            showFileTypeChangeDialog(fileName, fileBean);
//                        } else {
//                            mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
//                        }
//                    } else {
//                        mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
//                    }
//                }
//        });
        renameFileDialog.show(this);
    }


    /**
     * 文件类型改变
     */
    private void showFileTypeChangeDialog(String fileName, FileBean fileBean) {
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance("", UiUtil.getString(R.string.home_modify_file_type_tips));
        centerAlertDialog.setConfirmListener(() -> {
            mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
            centerAlertDialog.dismiss();
        });
        centerAlertDialog.show(this);
    }

    /**
     * 删除文件弹窗
     *
     * @param paths
     */
    private void showRemoveFileTips(List<String> paths) {
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(String.format(UiUtil.getString(R.string.home_remove_file_title), paths.size()), UiUtil.getString(R.string.home_remove_file_tips));
        centerAlertDialog.setConfirmListener(() -> {
            mPresenter.removeFile(Constant.scope_token, new ShareRequest(paths));
            centerAlertDialog.dismiss();
        });
        centerAlertDialog.show(this);
    }

    /**
     * 移动复制
     *
     * @param paths
     */
    private void toMoveCopyActivity(List<String> paths) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putSerializable("paths", (Serializable) paths);
        bundle.putInt(Constant.FROM, 1);
        switchToActivity(MoveCopyFileActivity.class, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OperateFileEvent event) {
        folders = event.getFolders();
        if (CollectionUtil.isNotEmpty(folders)) {
            mOperateData.get(0).setEnabled(event.isOnlyFolder() && FileUtil.hasWritePermission(folders));  // 是文件夹且有写权限才能共享
            mOperateData.get(1).setEnabled(FileUtil.hasWritePermission(folders));  // 有写权限才能下载
            mOperateData.get(2).setEnabled(false);
            mOperateData.get(3).setEnabled(true);
            mOperateData.get(4).setEnabled(folders.size() == 1 && FileUtil.hasWritePermission(folders));  // 有写权限才能重命名
            mOperateData.get(5).setEnabled(FileUtil.hasDelPermission(folders));  // 有删权限才能删除
            fileOperateAdapter.notifyDataSetChanged();
        } else {
            setAllEnabled();
        }
        binding.rvOperate.setVisibility(CollectionUtil.isNotEmpty(folders) && folders.size() > 0 ? View.VISIBLE : View.GONE);
        binding.navigation.setVisibility(CollectionUtil.isNotEmpty(folders) && folders.size() > 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置所有操作都可以点击
     */
    private void setAllEnabled() {
        for (FileOperateBean fileOperateBean : mOperateData) {
            fileOperateBean.setEnabled(true);
        }
        fileOperateAdapter.notifyDataSetChanged();
    }

    // 底部导航栏点击
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                showFragment(FRAGMENT_HOME);
                break;

            case R.id.navigation_share:
                showFragment(FRAGMENT_SHARE);
                break;

            case R.id.navigation_mine:
                showFragment(FRAGMENT_MINE);
                break;
        }
        return true;
    }

    /******************************** 初始化fragment ******************************/
    private void initFragment() {
        mIndex = FRAGMENT_HOME;
        showFragment(mIndex);
    }

    /**
     * 显示fragment
     *
     * @param index
     */
    private void showFragment(int index) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragments(fragmentTransaction);
        mIndex = index;
        switch (index) {
            // 我的文件
            case FRAGMENT_HOME:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.fl_content, homeFragment, HOME_TAG);
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;

            // 共享文件
            case FRAGMENT_SHARE:
                if (shareFragment == null) {
                    shareFragment = new ShareFragment();
                    fragmentTransaction.add(R.id.fl_content, shareFragment, SHARE_TAG);
                } else {
                    fragmentTransaction.show(shareFragment);
                }
                break;

            // 我的
            case FRAGMENT_MINE:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.fl_content, mineFragment, MINE_TAG);
                } else {
                    fragmentTransaction.show(mineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (shareFragment != null) {
            transaction.hide(shareFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    /**
     * 隐藏操作选项
     */
    private void hideOperate() {
        binding.rvOperate.setVisibility(View.GONE);
        binding.navigation.setVisibility(View.VISIBLE);
    }

    /**
     * 重新命名成功
     */
    @Override
    public void renameSuccess() {
        hideOperate();
        if (renameFileDialog != null && renameFileDialog.isShowing()) {
            renameFileDialog.dismiss();
        }
        ToastUtil.show(UiUtil.getString(R.string.home_rename_success));
        shareFragment.getData(false);
    }

    /**
     * 重新命名失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void renameFail(int errorCode, String msg) {

    }

    /**
     * 删除文件成功
     */
    @Override
    public void removeFileSuccess() {
        hideOperate();
        ToastUtil.show(UiUtil.getString(R.string.home_remove_success));
        shareFragment.getData(false);
    }

    /**
     * 删除文件失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void removeFileFail(int errorCode, String msg) {

    }

    @Override
    public void onChannelSuccess(ChannelEntity bean) {
        String newUrl = getUrl(Constant.currentHome.getSc_lan_address(), bean.getHost());
        ChannelUtil.resetApiServiceFactory(newUrl);
        LogUtil.e(TAG, "baseUrl3=" + newUrl);
        saveTempChannelUrl(bean);
    }

    boolean isFirstError = true;

    @Override
    public void onChannelFail(int errorCode, String msg) {
        if (isFirstError) {
            isFirstError = false;
            checkTempChannelWithinTime();
        } else {
            isFirstError = true;
            String url = Constant.currentHome.getSa_lan_address();
            ChannelUtil.resetApiServiceFactory(url);
            LogUtil.e(TAG, "baseUrl4=" + url);
        }
    }

    /**
     * 保存临时通道
     *
     * @param bean
     */
    private void saveTempChannelUrl(ChannelEntity bean) {
        bean.setGenerate_channel_time(TimeFormatUtil.getCurrentTime());
        String value = GsonConverter.getGson().toJson(bean);
        SpUtil.put(Constant.currentHome.getSa_user_token(), value);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (binding.rvOperate.getVisibility() == View.VISIBLE) {  // 如果是在操作文件，则取消操作
            binding.rvOperate.setVisibility(View.GONE);
            binding.navigation.setVisibility(View.VISIBLE);
            setAllEnabled();
            EventBus.getDefault().post(new CancelFileOperateEvent());
        } else {
            if (FastUtil.isDoubleClick()) {
                super.onBackPressed();
            } else {
                //GonetUtil.exitApp();
                ToastUtil.show(UiUtil.getString(R.string.common_exit_tip));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWifiReceiver != null) {
            unregisterReceiver(mWifiReceiver);
        }
    }
}