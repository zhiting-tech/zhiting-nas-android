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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.tools.SPUtils;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileOperateAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityMainBinding;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.RenameFileDialog;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.GetDeviceInfoBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.ScanDeviceByUDPBean;
import com.zhiting.clouddisk.entity.UpdateHomeNameEvent;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.entity.mine.SettingBean;
import com.zhiting.clouddisk.event.CancelFileOperateEvent;
import com.zhiting.clouddisk.event.ChangeHomeEvent;
import com.zhiting.clouddisk.event.HomeRemoveEvent;
import com.zhiting.clouddisk.event.OperateFileEvent;
import com.zhiting.clouddisk.event.RefreshDataEvent;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.MoveCopyFileActivity;
import com.zhiting.clouddisk.home.activity.ShareFolderActivity;
import com.zhiting.clouddisk.home.activity.UpDownLoadActivity;
import com.zhiting.clouddisk.home.fragment.HomeFragment;
import com.zhiting.clouddisk.main.contract.MainContract;
import com.zhiting.clouddisk.main.presenter.MainPresenter;
import com.zhiting.clouddisk.mine.activity.TrafficTipActivity;
import com.zhiting.clouddisk.mine.fragment.MineFragment;
import com.zhiting.clouddisk.popup_window.FamilyPopupWindow;
import com.zhiting.clouddisk.popup_window.SettingPopupWindow;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.clouddisk.share.fragment.ShareFragment;
import com.zhiting.clouddisk.util.AESUtil;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.clouddisk.util.FastUtil;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.Md5Util;
import com.zhiting.clouddisk.util.udp.ByteUtil;
import com.zhiting.clouddisk.util.udp.UDPSocket;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.entity.ChannelEntity;
import com.zhiting.networklib.event.FourZeroFourEvent;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.RetrofitManager;
import com.zhiting.networklib.http.cookie.PersistentCookieStore;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.NetworkUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.StringUtil;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;

/**
 * a
 * dev 1.4.0
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
    private boolean isFirst = true;

    private UDPSocket udpSocket; // 发现sa udpsocket
    private Set<String> updAddressSet; // 存储已获取upd设备
    private ConcurrentHashMap<String, ScanDeviceByUDPBean> scanMap = new ConcurrentHashMap<>();  // 存储udp扫描的设备信息
    private CountDownTimer mCountDownTimer; // 发现sa设备扫描倒计时

    private long sendId = 0; // 发送upd id

    protected FamilyPopupWindow familyPopupWindow;
    private SettingPopupWindow settingPopupWindow;

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
        binding.setHandler(new OnClickHandler());
        binding.tvHome.setText(Constant.currentHome.getName());
        binding.navigation.setItemIconTintList(null);
        binding.navigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        updAddressSet = new HashSet<>();
        initPopupWindow();
        initSettingView();
        initFragment();
        initRvOperateFile();
        basePermissionTask();
        initCountDownTimer();

        if (Constant.currentHome != null && TextUtils.isEmpty(Constant.currentHome.getSa_lan_address())) {
            getSAUrl();
        }
    }

    private void initGonet() {
        UiUtil.postDelayed(() -> {
            GonetUtil.initUploadManager();
            GonetUtil.initDownloadManager();
            uploadDownCount();
            WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
            if (wifiManager != null && checkSA(wifiManager.getConnectionInfo())) {
                setBackup();
            }
        }, 200);

    }

    /**
     * 家庭选择弹窗
     */
    private void initPopupWindow() {
        familyPopupWindow = new FamilyPopupWindow(this);

        familyPopupWindow.setSelectFamilyListener(new FamilyPopupWindow.OnSelectFamilyListener() {
            @Override
            public void selectedFamily(AuthBackBean authBackBean) {
                binding.tvHome.setText(authBackBean.getHomeCompanyBean().getName());
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
                HttpConfig.baseSAHost = saLanAddress;
                HttpConfig.baseTestUrl = TextUtils.isEmpty(saLanAddress) ? HttpUrlParams.SC_URL : saLanAddress;
                refreshHCData();
                familyPopupWindow.dismiss();
            }
        });

        familyPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.tvHome.setSelected(false);
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshHCData(){
        uploadDownCount();
        connectWifiHandle();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new ChangeHomeEvent());
            }
        }, 200);
    }

    /**
     * 设置弹窗的数据
     */
    private void initSettingView() {
        settingPopupWindow = new SettingPopupWindow(this);
        List<SettingBean> settingBeans = new ArrayList<>();
        settingBeans.add(new SettingBean(0, UiUtil.getString(R.string.mine_logout), R.drawable.icon_mine_logout));
        settingPopupWindow.setSelectedSettingListener(settingBean -> {
            logoutClearData();
        });
        settingPopupWindow.setSettingData(settingBeans);

    }

    /**
     * 退出登录后清除数据
     */
    private void logoutClearData() {
        SpUtil.put("loginInfo", "");
        SpUtil.put(Config.KEY_AUTH_INFO, "");
        SpUtil.put(SpConstant.COOKIE, "");
        PersistentCookieStore.getInstance().removeAll();
        switchToActivity(Login2Activity.class);
        finish();
    }

    /**
     * 设置备份
     */
    private void setBackup() {
        if (GonetUtil.mUploadManager != null) {
            if (SpUtil.getBoolean(SpConstant.ALBUM_AUTO + Constant.AREA_ID + Constant.USER_ID)) {
                GonetUtil.addUploadFile(Constant.BACKUP_CAMERA, Constant.BACK_ALBUM_TYPE);
            }
            if (SpUtil.getBoolean(SpConstant.VIDEO_AUTO + Constant.AREA_ID + Constant.USER_ID)) {
                GonetUtil.addUploadFile(Constant.BACKUP_VIDEO, Constant.BACK_VIDEO_TYPE);
            }
            if (SpUtil.getBoolean(SpConstant.FILE_AUTO + Constant.AREA_ID + Constant.USER_ID)) {
                GonetUtil.addUploadFile(Constant.BACKUP_FILE, Constant.BACK_FILE_TYPE);
            }
            if (SpUtil.getBoolean(SpConstant.AUDIO_AUTO + Constant.AREA_ID + Constant.USER_ID)) {
                GonetUtil.addUploadFile(Constant.BACKUP_AUDIO, Constant.BACK_AUDIO_TYPE);
            }
        }
    }

    /**
     * 上传下载数量
     */
    private void uploadDownCount() {
        int fileCount = GonetUtil.getUnderwayFileCount() + GonetUtil.getBackupFileCount();
        LogUtil.e("onMessageEvent1==" + fileCount);
        if (fileCount == 0) {
            binding.tvFileCount.setVisibility(View.GONE);
        } else {
            binding.tvFileCount.setVisibility(View.VISIBLE);
            binding.tvFileCount.setText(String.valueOf(fileCount));
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mWifiReceiver = new WifiReceiver();
        registerWifiReceiver();
    }

    /**
     * 获取SA地址
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FourZeroFourEvent event) {
        getSAUrl();
    }

    /**
     * 移除家庭
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HomeRemoveEvent event) {
        if (familyPopupWindow!=null) {
            familyPopupWindow.removeHome();
        }
    }

    /**
     * 更新家庭名称显示
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateHomeNameEvent event) {
        if (Constant.currentHome!=null){
            binding.tvHome.setText(Constant.currentHome.getName());
            refreshHCData();
        }
    }

    /**
     * 上传下载数量更新通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadDownloadEvent event) {
        uploadDownCount();
    }

    /**
     * 获取SA地址
     */
    private void getSAUrl() {
        if (AndroidUtil.getNetworkType().equals(AndroidUtil.NET_WIFI)) {
            startUDPScan();
            mCountDownTimer.start();
        }
    }

    /**
     * upd发现设备
     */
    private void startUDPScan() {
        if (udpSocket == null) {
            udpSocket = new UDPSocket(Constant.FIND_DEVICE_URL, Constant.FIND_DEVICE_PORT,
                    new UDPSocket.OnReceiveCallback() {
                        @Override
                        public void onReceiveByteData(String address, int port, byte[] data, int length) {
                            scanDeviceSuccessByUDP(address, port, data, length);
                        }

                        @Override
                        public void onReceive(String msg) {

                        }
                    });
        }
        udpSocket.startUDPSocket();
        udpSocket.sendMessage(Constant.SEND_HELLO_DATA, Constant.FIND_DEVICE_URL);
    }

    /**
     * 计时
     */
    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(10_000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.e("initCountDownTimer=" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                LogUtil.e("initCountDownTimer=onFinish=");
                clearCollection();
            }
        };
    }

    /**
     * 处理udp发现的设备
     *
     * @param address
     * @param port
     * @param data
     * @param length
     */
    private void scanDeviceSuccessByUDP(String address, int port, byte[] data, int length) {
        byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
        try {
            String deviceId = ByteUtil.bytesToHex(deviceIdData);
            if (scanMap.containsKey(deviceId)) {  // 已经获取到hello数据包信息
                ScanDeviceByUDPBean sdb = scanMap.get(deviceId);
                String token = sdb.getToken();
                byte[] dealData = Arrays.copyOfRange(data, 32, length);
                if (TextUtils.isEmpty(token)) { // 没有获取过token信息
                    String key = sdb.getPassword();
                    String decryptKeyMD5 = Md5Util.getMD5(key);
                    byte[] decryptKeyDta = ByteUtil.md5Str2Byte(decryptKeyMD5);
                    byte[] ivData = ByteUtil.byteMergerAll(decryptKeyDta, key.getBytes());
                    byte[] ivEncryptedData = Md5Util.getMD5(ivData);
                    String tokenFromServer = AESUtil.decryptAES(dealData, decryptKeyDta, ivEncryptedData, AESUtil.PKCS7, true);
                    if (!TextUtils.isEmpty(tokenFromServer)) {
                        sdb.setToken(tokenFromServer);
                        sdb.setId(sendId);
                        String deviceStr = "{\"method\":\"get_prop.info\",\"params\":[],\"id\":" + sendId + "}";  // 获取设备信息体
                        sendId++;
                        byte[] bodyData = AESUtil.encryptAES(deviceStr.getBytes(), tokenFromServer, AESUtil.PKCS7); // 获取设备信息体转字节加密
                        int len = bodyData.length + 32;  // 包长
                        byte[] lenData = ByteUtil.intToByte2(len);  // 包长用占两位字节
                        byte[] headData = {(byte) 0x21, (byte) 0x31}; // 包头固定
                        byte[] preData = {(byte) 0xFF, (byte) 0xFF}; // 预留固定
                        byte[] serData = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 序列号固定
                        byte[] tokenData = sdb.getPassword().getBytes();  // 之前获取设备信息时生成的16位随机密码
                        byte[] getDeviceInfoData = ByteUtil.byteMergerAll(headData, lenData, preData, deviceIdData, serData, tokenData, bodyData); //  拼接获取设备信息包
                        udpSocket.sendMessage(getDeviceInfoData, address);
                    }
                } else { // 获取过token信息
                    GetDeviceInfoBean deviceInfoBean = sdb.getDeviceInfoBean();
                    byte[] decryptDeviceData = Md5Util.getMD5(ByteUtil.md5Str2Byte(token));
                    byte[] ivDeviceData = ByteUtil.byteMergerAll(decryptDeviceData, ByteUtil.md5Str2Byte(token));
                    byte[] ivEncryptedDeviceData = Md5Util.getMD5(ivDeviceData);
                    String infoJson = AESUtil.decryptAES(dealData, decryptDeviceData, ivEncryptedDeviceData, AESUtil.PKCS7, false);

                    GetDeviceInfoBean getDeviceInfoBean = new Gson().fromJson(infoJson, GetDeviceInfoBean.class);

                    if (deviceInfoBean == null && getDeviceInfoBean != null && sdb.getId() == getDeviceInfoBean.getId()) {
                        GetDeviceInfoBean.ResultBean gdifb = getDeviceInfoBean.getResult();
                        sdb.setDeviceInfoBean(getDeviceInfoBean);
                        if (gdifb != null) {
                            String model = gdifb.getModel();
                            if (!TextUtils.isEmpty(model) && model.equals(Constant.SMART_ASSISTANT)) { // 如果时sa设备
                                String saId = gdifb.getSa_id();
                                if (!TextUtils.isEmpty(saId) && saId.equals(Constant.currentHome.getSa_id())) {
                                    String url = Constant.HTTP_HEAD + sdb.getHost() + ":" + gdifb.getPort();
                                    findSuccess(url);
                                    clearCollection();
                                }

                            }
                        }
                    }
                }
            } else {  // 获取到hello数据包信息
                ScanDeviceByUDPBean scanDeviceByUDPBean = new ScanDeviceByUDPBean(address, port, deviceId);
                String password = StringUtil.getUUid().substring(0, 16);
                scanDeviceByUDPBean.setPassword(password);
                scanMap.put(deviceId, scanDeviceByUDPBean);
                getDeviceToken(address, data, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备token
     *
     * @param address
     * @param data
     */
    private void getDeviceToken(String address, byte[] data, String password) {
        if (!updAddressSet.contains(address)) {
            updAddressSet.add(address);
            byte[] tokenHeadData = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFE}; // 包头，包长，预留字节固定
            byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
            byte[] passwordData = password.getBytes();  // 密码
            byte[] serData = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 序列号 固定
            byte[] tokenData = ByteUtil.byteMergerAll(tokenHeadData, deviceIdData, serData, passwordData);  // 拼接获取token数据包
            udpSocket.sendMessage(tokenData, address);
        }
    }

    /**
     * 发现sa成功之后的操作
     *
     * @param url
     */
    private void findSuccess(String url) {
        Constant.currentHome.setSa_lan_address(url);  // 当前家庭设置sa地址
        HttpConfig.baseTestUrl = url;
        HttpConfig.baseSAHost = url;
        String bssid = "";  // wifi 的 bssid
        WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
        if (wifiManager != null) { // wifi管理器不为空
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {  // wifi信息不为空
                bssid = wifiInfo.getBSSID();
            }
        }
        if (TextUtils.isEmpty(Constant.currentHome.getMac_address())) {  // 把wifi的bssid设置到当前家庭mac_address
            Constant.currentHome.setMac_address(bssid);
        }
        // 更新缓存信息
        String authInfo = SpUtil.getString(Config.KEY_LOGIN_INFO);
        AuthBackBean authBackBean = GsonConverter.getGson().fromJson(authInfo, AuthBackBean.class);//授权信息
        if (authBackBean != null) {
            HomeCompanyBean homeCompanyBean = authBackBean.getHomeCompanyBean();
            if (homeCompanyBean != null) {
                homeCompanyBean.setSa_lan_address(url);
                if (TextUtils.isEmpty(homeCompanyBean.getMac_address())) {
                    homeCompanyBean.setMac_address(bssid);
                }
                String backInfo = new Gson().toJson(authBackBean);
                SpUtil.put(Config.KEY_LOGIN_INFO, backInfo);
            }
        }

        List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(SpUtil.getString(Config.KEY_AUTH_INFO), new TypeToken<List<AuthBackBean>>() {
        }.getType());
        for (AuthBackBean abb : authBackList) {
            HomeCompanyBean homeCompanyBean = abb.getHomeCompanyBean();
            if (homeCompanyBean.getSa_id().equals(Constant.currentHome.getSa_id())) {
                homeCompanyBean.setSa_lan_address(url);
                if (TextUtils.isEmpty(homeCompanyBean.getMac_address())) {
                    homeCompanyBean.setMac_address(bssid);
                }
                break;
            }
        }
        String authBackInfo = new Gson().toJson(authBackList);
        SpUtil.put(Config.KEY_AUTH_INFO, authBackInfo);
        // 取消倒计时
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        // 通知刷新数据
        EventBus.getDefault().post(new RefreshDataEvent());
    }

    /**
     * 清除发现设备信息缓存
     */
    private void clearCollection() {
        if (udpSocket != null && udpSocket.isRunning()) {
            udpSocket.stopUDPSocket();
        }
        scanMap.clear();
        updAddressSet.clear();
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

    private boolean needShowTraffic;

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {//断开wifi
                    if (GonetUtil.mDownloadManager != null && GonetUtil.getBackupFileCount() > 0 && SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true)) {
                        needShowTraffic = true;
                        GonetUtil.stopAllUploadTask(1);
                    }
                    switchNetwork(false);
                    GonetUtil.netWorkNotice();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {//连接wifi
                        connectWifiHandle();
                    }
                }
            } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                showTrafficDialog();
            }
        }
    }

    private void showTrafficDialog() {
        int type = NetworkUtil.getNetworkerStatus();
        boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
        int fileCount = GonetUtil.getUnderwayFileCount();
        EventBus.getDefault().post(new UploadDownloadEvent());
        if (type >= 2 && isOnlyWifi && !isFirst && (fileCount > 0 || needShowTraffic)) {
            switchToActivity(TrafficTipActivity.class);
        }
        isFirst = false;
    }

    /**
     * 连接wifi成功后的操作
     *
     */
    private void connectWifiHandle() {
        UiUtil.postDelayed(() -> {
            WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
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
        LogUtil.e("执行到这里");
        if (isSA) {
            LogUtil.e("执行到这里" + "true");
            String url = Constant.currentHome.getSa_lan_address();
            ChannelUtil.resetApiServiceFactory(url);
        } else {
            LogUtil.e("执行到这里" + "false");
            checkTempChannelWithinTime();
        }
    }

    /**
     * 处理临时通道
     */
    private void checkTempChannelWithinTime() {
        HomeCompanyBean home = Constant.currentHome;
        if (home.isSAEnvironment()) return;
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
        if (TextUtils.isEmpty(oldUrl)) {
            oldUrl = HttpUrlParams.SC_URL;
        }
        if (!TextUtils.isEmpty(host)) {
            url = oldUrl.replace(HttpConfig.baseSCHost, host);
        }
        return url;
    }

    /**
     * 获取临时通道
     */
    private void requestTempChannelUrl(HomeCompanyBean home) {
        Map<String, String> map = new HashMap<>();
        map.put("scheme", RetrofitManager.HTTPS);
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
        GonetUtil.mDownloadPath = SPUtils.getInstance().getString(Config.KEY_DOWNLOAD_PATH, GonetUtil.mDownloadPath);
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
        uploadDownCount();
        switch (index) {
            // 我的文件
            case FRAGMENT_HOME:
                checkTempChannelWithinTime();
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.fl_content, homeFragment, HOME_TAG);
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;

            // 共享文件
            case FRAGMENT_SHARE:
                checkTempChannelWithinTime();
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
        binding.flUpDownload.setVisibility(mIndex < 2 ? View.VISIBLE : View.GONE);
        binding.ivSetting.setVisibility(mIndex == 2 ? View.VISIBLE : View.GONE);
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
        setBackup();
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
            if (!TextUtils.isEmpty(url))
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
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (udpSocket != null) {
            udpSocket.stopUDPSocket();
        }
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvHome) { // 我的家
                binding.tvHome.setSelected(true);
                familyPopupWindow.showAsDown(binding.tvHome);
            } else if (viewId == R.id.flUpDownload) { // 上传下载列表
                switchToActivity(UpDownLoadActivity.class);
            } else if (viewId == R.id.ivSetting) { // 设置
                settingPopupWindow.showAsDown(binding.ivSetting);
            }
        }
    }
}