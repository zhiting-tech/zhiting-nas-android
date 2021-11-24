package com.zhiting.clouddisk.util;

import android.text.TextUtils;

import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.entity.home.DownLoadFilesBean;
import com.zhiting.clouddisk.entity.home.UnderwayTaskCountBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.entity.home.UploadFilesBean;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gonet.DownloadManager;
import gonet.Gonet;
import gonet.UploadManager;

/**
 * date : 2021/7/20 16:29
 * desc :文件上传下载工具
 */
public class GonetUtil {
    public static String TAG = "GonetUtil==";
    public static String dbName = "mobile.db";//数据库
    public static String dbPath = BaseFileUtil.getProjectCachePath(LibLoader.getApplication());//数据库的目录
    public static UploadManager mUploadManager;
    public static DownloadManager mDownloadManager;

    /**
     * 获取下载文件列表
     *
     * @param listener
     */
    public static void getDownloadList(OnDownLoadFilesListener listener) {
        UiUtil.starThread(() -> {
            List<DownLoadFileBean> list = new ArrayList<>();
            String jsonData = Gonet.getDownloadList(dbPath, "file", 0, Constant.USER_ID, Constant.AREA_ID);
            LogUtil.e(TAG + "getDownloadList=" + jsonData);

            if (!TextUtils.isEmpty(jsonData)) {
                DownLoadFilesBean filesBean = GsonConverter.getGson().fromJson(jsonData, DownLoadFilesBean.class);
                if (filesBean != null) {
                    list = filesBean.getUploadList();
                    if (list != null && list.size() > 0) {
                        Collections.sort(list, (bean1, bean2) -> -Long.compare(bean1.getCreate_time(), bean2.getCreate_time()));
                    }
                }
            }

            List<DownLoadFileBean> finalList = list;
            UiUtil.runInMainThread(() -> {
                listener.onCallBack(finalList);
            });
        });
    }

    /**
     * 获取文件夹列表
     *
     * @param listener
     * @param id
     */
    public static void getDownloadDirList(long id, OnDownLoadFilesListener listener) {
        UiUtil.starThread(() -> {
            List<DownLoadFileBean> list = new ArrayList<>();
            String jsonData = Gonet.getDownloadList(dbPath, "dir", id, Constant.USER_ID, Constant.AREA_ID);
            LogUtil.e(TAG + "getDownloadDirList=" + jsonData);

            if (!TextUtils.isEmpty(jsonData)) {
                DownLoadFilesBean filesBean = GsonConverter.getGson().fromJson(jsonData, DownLoadFilesBean.class);
                if (filesBean != null) {
                    list = filesBean.getUploadList();
                }
            }

            List<DownLoadFileBean> finalList = list;
            UiUtil.runInMainThread(() -> {
                listener.onCallBack(finalList);
            });
        });
    }

    /**
     * 获取上传文件列表
     */
    public static void getUploadList(OnGetFilesListener listener) {
        UiUtil.starThread(() -> {
            List<UploadFileBean> list = new ArrayList<>();
            String jsonData = Gonet.getUploadList(dbPath, Constant.USER_ID, Constant.AREA_ID);
            LogUtil.e(TAG + "getUploadFiles=" + jsonData);

            if (!TextUtils.isEmpty(jsonData)) {
                UploadFilesBean filesBean = GsonConverter.getGson().fromJson(jsonData, UploadFilesBean.class);
                if (filesBean != null) {
                    list = filesBean.getUploadList();
                    if (list != null && list.size() > 0) {
                        Collections.sort(list, (bean1, bean2) -> -Long.compare(bean1.getCreate_time(), bean2.getCreate_time()));
                    }
                }
            }

            List<UploadFileBean> finalList = list;
            UiUtil.runInMainThread(() -> {
                listener.onCallBack(finalList);
            });
        });
    }

    /**
     * 初始化上传管理类
     */
    public static void initUploadManager() {
        if (mUploadManager == null && !TextUtils.isEmpty(Constant.scope_token)) {
            UiUtil.starThread(() -> {
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mUploadManager = Gonet.newUploadManager(getBaseUrl(), dbPath, headerStr);
                mUploadManager.run(headerStr);
            });
        }
    }

    private static String getBaseUrl() {
        String baseUrl = HttpConfig.baseTestUrl;
        if (!baseUrl.contains("api")) {
            baseUrl = baseUrl + "/api";
        }
        LogUtil.e("baseUrl=" + baseUrl);
        return baseUrl;
    }

    /**
     * 开始上传
     */
    public static void startUpload(long id) {
        LogUtil.e(TAG + "=startUpload=" + id);
        if (mUploadManager != null) {
            mUploadManager.start(id);
        }
    }

    /**
     * 停止上传
     */
    public static void stopUpload(long id) {
        LogUtil.e(TAG + "=stopUpload=" + id);
        if (mUploadManager != null) {
            mUploadManager.stop(id);
        }
    }

    /**
     * 上传文件
     *
     * @param filePath   文件地址
     * @param folderPath 文件夹地址
     */
    public synchronized static void uploadFile(String filePath, String folderPath, String pwd, UpOrDownloadListener upOrDownloadListener) {
        if (mUploadManager != null) {
            UiUtil.starThread(() -> {
                String fileName = getFileName(filePath);
                String url = HttpConfig.uploadFileUrl + folderPath + "/" + fileName;
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mUploadManager.createFileUploader(url, filePath, fileName, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID);
                if (upOrDownloadListener != null) {
                    upOrDownloadListener.upOrDownload();
                }
                LogUtil.e(TAG + "\nuploadFile:\nurl=" + url + "\ndbPath=" + dbPath + "\nfilePath=" + filePath + "\nfileName=" + fileName + "\npwd=" + pwd + "\nheaderStr=" + headerStr);
            });
        }
    }

    /**
     * 文件下载
     *
     * @param filePath
     */
    public synchronized static void downloadFile(String filePath, String pwd, UpOrDownloadListener upOrDownloadListener) {
        if (mDownloadManager != null) {
            UiUtil.starThread(() -> {
                String url = HttpConfig.downLoadFileUrl + filePath;
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mDownloadManager.createFileDownloader(url, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID);
                if (upOrDownloadListener != null) {
                    upOrDownloadListener.upOrDownload();
                }
                LogUtil.e(TAG + "\ndownloadFile:\nurl=" + url + "\ndbPath=" + dbPath + "\nheaderStr=" + headerStr + "\npwd=" + pwd);
            });
        }
    }

    /**
     * 下载文件夹
     *
     * @param filePath
     */
    public static void downloadFolder(String filePath, String pwd, UpOrDownloadListener startDownListener) {
        if (mDownloadManager != null) {
            UiUtil.starThread(() -> {
                String url1 = HttpConfig.downLoadFolderUrl1;//网络请求地址1
                String url2 = HttpConfig.downLoadFolderUrl2;//网络请求地址2
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";//请求头
                mDownloadManager.createDirDownloader(url1, url2, filePath, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID);
                if (startDownListener != null) {
                    startDownListener.upOrDownload();
                }
                LogUtil.e(TAG + "\ndownloadFolder:\nurl1=" + url1 + "\nurl2=" + url2 + "\nfilePath=" + filePath + "\ndbPath=" + dbPath + "\nheaderStr=" + headerStr + "\npwd=" + pwd);
            });
        }
    }

    /**
     * 初始下載管理类
     */
    public static void initDownloadManager() {
        LogUtil.e(TAG + "初始化");
        if (mDownloadManager == null && !TextUtils.isEmpty(Constant.scope_token)) {
            UiUtil.starThread(() -> {
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mDownloadManager = Gonet.newDownloadManager(getBaseUrl(), dbPath, headerStr);
                mDownloadManager.run(headerStr);
            });
        }
    }

    /**
     * 获取正在上传和下载文件的数量
     *
     * @return
     */
    public static int getUnderwayFileCount() {
        LogUtil.e(TAG + "getUnderwayFileCount=" + dbPath);
        String json = Gonet.getOnGoingTaskNum(dbPath, Constant.USER_ID, Constant.AREA_ID);
        UnderwayTaskCountBean bean = GsonConverter.getGson().fromJson(json, UnderwayTaskCountBean.class);
        return bean.getAllNum();
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    public static String getFileName(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            int start = filePath.lastIndexOf("/") + 1;
            return filePath.substring(start);
        }
        return "";
    }

    public interface OnDownLoadFilesListener {
        void onCallBack(List<DownLoadFileBean> list);
    }

    public interface OnGetFilesListener {
        void onCallBack(List<UploadFileBean> list);
    }

    /**
     * 开始下载
     */
    public static void startDownload(long id) {
        if (mDownloadManager != null) {
            mDownloadManager.start(id);
            LogUtil.e(TAG + "startDownload=" + id);
        }
    }

    /**
     * 停止下载
     */
    public static void stopDownload(long id) {
        if (mDownloadManager != null) {
            mDownloadManager.stop(id);
            LogUtil.e(TAG + "stopDownload=" + id);
        }
    }

    /**
     * 删除任务
     *
     * @param id
     */
    public static void deleteDownload(long id) {
        if (mDownloadManager != null) {
            mDownloadManager.delete(id);
            LogUtil.e(TAG + "deleteDownload=" + id);
        }
    }

    /**
     * 退出APP
     */
    public static void exitApp() {
        if (mUploadManager != null) {
            mUploadManager.quitAPP();
            LogUtil.e(TAG + "exitApp");
        }
        if (mDownloadManager != null) {
            mDownloadManager.quitAPP();
        }
    }

    /**
     * 网络通知
     */
    public static void netWorkNotice() {
        if (mUploadManager != null) {
            mUploadManager.networkNil();
        }
        if (mDownloadManager != null) {
            mDownloadManager.networkNil();
        }
    }

    /**
     * 上传域名的更变
     */
    public static void changeHost() {
        String baseUrl = getBaseUrl();
        if (mUploadManager != null) {
            mUploadManager.changeHost(baseUrl);
        }
        if (mDownloadManager != null) {
            mDownloadManager.changeHost(baseUrl);
        }
    }

    public interface UpOrDownloadListener {
        void upOrDownload();
    }
}
