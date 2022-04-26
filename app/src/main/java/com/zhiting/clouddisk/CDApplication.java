package com.zhiting.clouddisk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.db.DBManager;
import com.zhiting.clouddisk.home.activity.ErrorActivity;
import com.zhiting.clouddisk.util.FileUtils;
import com.zhiting.networklib.base.BaseApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CDApplication extends BaseApplication {

    private String TAG = "CDApplication==";
    private static DBManager mDBManger;

    @Override
    public void onCreate() {
        super.onCreate();
        mDBManger = DBManager.getInstance(this);
        initUncaughtException();
        initBugly();
        initVideo();
        initTBSWebView();
    }

    /**
     * 腾讯X5浏览器初始化
     */
    private void initTBSWebView() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.disableAutoCreateX5Webview();

        QbSdk.initX5Environment(CDApplication.this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "onCoreInitFinished=");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e(TAG, "onViewInitFinished=" + b);
                Constant.isX5Success = b;
                if (!QbSdk.canLoadX5(CDApplication.this) && !TbsDownloader.isDownloading()) {
                    TbsDownloader.startDownload(CDApplication.this);
                }
            }
        });

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                Log.e(TAG, "onDownloadFinish=" + i);
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.e(TAG, "onInstallFinish=" + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.e(TAG, "onDownloadProgress=" + i);
            }
        });
    }

    /**
     * 初始化播放器
     */
    private void initVideo() {
        VideoOptionModel option18 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        //视频格式
        VideoOptionModel option19 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        //网络不好的情况下进行丢包 跳帧处理（-1~120）。CPU处理慢时，进行跳帧处理，保证音视频同步
        VideoOptionModel option20 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        //0为一进入就播放,1为进入时不播放
        VideoOptionModel option21 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        //域名检测
        VideoOptionModel option22 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        VideoOptionModel option23 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        //最大缓冲大小,单位kb
        VideoOptionModel option24 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024);
        //http重定向https
        VideoOptionModel option27 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        VideoOptionModel option30 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        VideoOptionModel option31 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1);

        List<VideoOptionModel> list = new ArrayList<>();
        list.add(option18);
        list.add(option19);
        list.add(option20);
        list.add(option21);
        list.add(option22);
        list.add(option23);
        list.add(option24);
        list.add(option27);
        list.add(option30);
        list.add(option31);
        GSYVideoManager.instance().setOptionModelList(list);

        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        CacheFactory.setCacheManager(ProxyCacheManager.class);

        //PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
    }

    /**
     * Bugly注册
     */
    private void initBugly() {
        //if (!BuildConfig.DEBUG) return;
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "570c5c7054", BuildConfig.DEBUG, strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private void initUncaughtException() {
        if (BuildConfig.DEBUG) {
            Thread.UncaughtExceptionHandler handler = (thread, throwable) -> {
                String errorLogPath = this.getExternalCacheDir().getAbsolutePath() + "/error";
                Log.e("errorLogPath=", errorLogPath);
                String errorLog = FileUtils.writeErrorLog(throwable, errorLogPath);
                Intent intent = new Intent(this, ErrorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ErrorActivity.KEY_ERROR_LOG, errorLog);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            };
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }

    public static DBManager getDBManager() {
        return mDBManger;
    }
}
