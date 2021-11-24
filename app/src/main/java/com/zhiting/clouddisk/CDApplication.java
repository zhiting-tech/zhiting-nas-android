package com.zhiting.clouddisk;

import com.tencent.bugly.crashreport.CrashReport;
import com.zhiting.clouddisk.db.DBManager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.zhiting.clouddisk.home.activity.ErrorActivity;
import com.zhiting.clouddisk.util.AppFrontBackHelper;
import com.zhiting.clouddisk.util.FileUtils;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.base.BaseApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CDApplication extends BaseApplication {

    private static DBManager mDBManger;

    @Override
    public void onCreate() {
        super.onCreate();
        mDBManger = DBManager.getInstance(this);
        initUncaughtException();
        initBugly();
    }

    /**
     * Bugly注册
     */
    private void initBugly() {
        if (!BuildConfig.DEBUG) return;
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

    private void initLifeCycleListener() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                Log.e("initLifeCycleListener1=", "前台");
            }

            @Override
            public void onBack() {
                //应用切到后台处理
                GonetUtil.exitApp();
                Log.e("initLifeCycleListener1=", "后台");
            }
        });
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

    public static DBManager getDBManager(){
        return mDBManger;
    }

}
