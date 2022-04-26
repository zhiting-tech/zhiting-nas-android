package com.zhiting.clouddisk.util;

import android.text.TextUtils;
import android.util.Log;

import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.entity.BackupFilesBean;
import com.zhiting.clouddisk.entity.SuccessUploadFileBean;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.entity.home.DownLoadFilesBean;
import com.zhiting.clouddisk.entity.home.UnderwayTaskCountBean;
import com.zhiting.clouddisk.entity.home.UploadErrorBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.entity.home.UploadFilesBean;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import java.util.ArrayList;
import java.util.List;

import gonet.DownloadManager;
import gonet.Gonet;
import gonet.UploadCallback;
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
    public static String mDownloadPath = dbPath;//文件下载的路径
    public static boolean hasInit;
    public static int count;

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
     * backUp 1备份
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
                }
            }

            List<UploadFileBean> finalList = list;
            UiUtil.runInMainThread(() -> {
                listener.onCallBack(finalList);
            });
        });
    }

    /**
     * 获取备份列表
     */
    public static void getUploadBackupList(OnGetBackupFilesListener getBackupFilesListener) {
        UiUtil.starThread(new Runnable() {
            @Override
            public void run() {
                if (mUploadManager != null) {
                    String jsonData = Gonet.getUploadBackupList(dbPath, Constant.USER_ID, Constant.AREA_ID);
                    BackupFilesBean backupFilesBean = null;
                    if (!TextUtils.isEmpty(jsonData)) {
                        backupFilesBean = GsonConverter.getGson().fromJson(jsonData, BackupFilesBean.class);
                    }
                    if (getBackupFilesListener != null) {
                        BackupFilesBean finalBackupFilesBean = backupFilesBean;
//                        UiUtil.runInMainThread(() -> getBackupFilesListener.onCallBack(finalBackupFilesBean));
                        getBackupFilesListener.onCallBack(finalBackupFilesBean);

                    }
                }
            }
        });
    }

    /**
     * 获取备份成功的列表
     *
     * @param backupSuccessListListener
     */
    public static void getBackupSuccessLis(OnBackupSuccessListListener backupSuccessListListener) {
        UiUtil.starThread(new Runnable() {
            @Override
            public void run() {
                if (mUploadManager != null) {
                    String jsonData = Gonet.getUploadBackupOnSuccessList(dbPath, Constant.USER_ID, Constant.AREA_ID);
                    BackupFilesBean backupFilesBean = null;
                    if (!TextUtils.isEmpty(jsonData)) {
                        backupFilesBean = GsonConverter.getGson().fromJson(jsonData, BackupFilesBean.class);
                    }
                    if (backupSuccessListListener!=null) {
                        BackupFilesBean finalBackupFilesBean = backupFilesBean;
                        backupSuccessListListener.onSuccessList(finalBackupFilesBean);
                    }
                }
            }
        });
    }

    /**
     * 初始化上传管理类
     */
    public static synchronized void initUploadManager() {
        if (mUploadManager == null && !TextUtils.isEmpty(Constant.scope_token)) {
            if (hasInit) {
                return;
            }
            hasInit = true;
            count++;
            LogUtil.e("UploadManager初始化第"+ count + "次");
            UiUtil.starThread(() -> {
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mUploadManager = Gonet.newUploadManager(getBaseUrl(), dbPath, headerStr, 1);

                mUploadManager.run(headerStr, new UploadCallback() {

                    @Override
                    public void sendFailResult(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            UploadErrorBean filesBean = GsonConverter.getGson().fromJson(content, UploadErrorBean.class);
                            if (listener != null && filesBean != null) {
                                UiUtil.runInMainThread(() -> listener.onError(filesBean));
                            }
                        } else {
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

                    @Override
                    public void sendFinishBackupTaskFileInfo(String uploadFileJson) {
                        if (!TextUtils.isEmpty(uploadFileJson)) {
                            SuccessUploadFileBean successUploadFileBean = GsonConverter.getGson().fromJson(uploadFileJson, SuccessUploadFileBean.class);
                            if (backupFileListener != null) {
                                UploadFileBean uploadFileBean = successUploadFileBean.getUploadOnSuccessList();
                                backupFileListener.onSuccess(uploadFileBean);
                            }
                        }
                    }
                });
            });
        }
    }

    public static void setOnUploadListener(OnUploadListener listener) {
        GonetUtil.listener = listener;
    }

    public static OnUploadListener listener;

    public interface OnUploadListener {
        void onError(UploadErrorBean filesBean);
    }


    private static String getBaseUrl() {
        String baseUrl = HttpConfig.baseTestUrl;
//        if (!baseUrl.contains("api")) {
//            if (baseUrl.endsWith("/")) {
//                baseUrl = baseUrl + "api";
//            } else {
//                baseUrl = baseUrl + "/api";
//            }
//        }
        if (baseUrl.contains("api")) {
            baseUrl.replace("api", "");
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
     * 删除上传任务
     *
     * @param id
     */
    public static void deleteUpload(long id) {
        LogUtil.e(TAG + "=stopUpload=" + id);
        if (mUploadManager != null) {
            mUploadManager.delete(id);
        }
    }

    /**
     * 上传文件
     *
     * @param filePath   文件地址
     * @param folderPath 文件夹地址
     * @param backUp     1备份
     */
    public synchronized static void uploadFile(String filePath, String folderPath, String pwd, int backUp, UpOrDownloadListener upOrDownloadListener) {
        if (mUploadManager != null) {
            UiUtil.starThread(() -> {
                String fileName = getFileName(filePath);
                String url = HttpConfig.uploadFileUrl + folderPath + "/" + fileName;
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mUploadManager.androidCreateFileUploader(url, filePath, fileName, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID, backUp, "");
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
    public synchronized static void downloadFile(String filePath, String coverUrl, String pwd, UpOrDownloadListener upOrDownloadListener) {
        if (mDownloadManager != null) {
            UiUtil.starThread(() -> {
                String url = HttpConfig.downLoadFileUrl + filePath;
                String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                mDownloadManager.androidCreateFileDownloader(url, mDownloadPath, coverUrl, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID);
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
                mDownloadManager.androidCreateDirDownloader(url1, url2, filePath, mDownloadPath, headerStr, pwd, Constant.USER_ID, Constant.AREA_ID);
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
     * 获取正在备份的数量
     *
     * @return
     */
    public static int getBackupFileCount() {
        int count = 0;
        if (mUploadManager != null) {
            String json = mUploadManager.getUploadAllBackupsTaskNum(Constant.USER_ID, Constant.AREA_ID);
            count = Integer.parseInt(json);
        }
        return count;
    }

    /**
     * 添加上传路径
     *
     * @param filePath 备份路径
     * @param fileType 1. 相册  2.视频  3.文档  4.音频
     */
    public static void addUploadFile(String filePath, int fileType) {
        if (mUploadManager != null) {
            UiUtil.starThread(new Runnable() {
                @Override
                public void run() {
                    String headerStr = "{\"scope-token\":\"" + Constant.scope_token + "\"}";
                    mUploadManager.androidAddUploadFile(filePath, AndroidUtil.getDeviceBrand(), headerStr, Constant.USER_ID, Constant.AREA_ID, fileType);
                }
            });

        }
    }

    /**
     * 关闭文件备份
     *
     * @param fileType 1. 相册  2.视频  3.文档  4.音频
     */
    public static void closeFileBackup(int fileType) {
        if (mUploadManager != null) {
            UiUtil.starThread(new Runnable() {
                @Override
                public void run() {
                    mUploadManager.androidCloseFileBackup(Constant.USER_ID, Constant.AREA_ID, fileType);
                }
            });
        }
    }

    /**
     * 关闭后台备份
     */
    public static void closeBackup() {
        if (mUploadManager != null) {
            UiUtil.starThread(new Runnable() {
                @Override
                public void run() {
//                    mUploadManager.closeBackup(Constant.USER_ID, Constant.AREA_ID);
                }
            });
        }
    }

    /**
     * 全部重试
     */
    public static void allFailRetry() {
        if (mUploadManager != null) {
            UiUtil.starThread(() -> mUploadManager.allFailReTry(Constant.USER_ID, Constant.AREA_ID));
        }
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
     * 备份列表监听
     */
    public interface OnGetBackupFilesListener {
        void onCallBack(BackupFilesBean backupFilesBean);
    }

    /**
     * 备份完成列表数据
     */
    public interface OnBackupSuccessListListener {
        void onSuccessList(BackupFilesBean backupFilesBean);
    }

    /**
     * 备份成功
     */
    public interface OnBackupFileListener {
        void onSuccess(UploadFileBean uploadFileBean);
    }

    private static OnBackupFileListener backupFileListener;

    public static void setBackupFileListener(OnBackupFileListener backupFileListener) {
        GonetUtil.backupFileListener = backupFileListener;
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

    /**
     * 全部开始下载任务
     */
    public static void startAllDownTask() {
        if (mDownloadManager != null) {
            mDownloadManager.startAll(Constant.USER_ID, Constant.AREA_ID);
        }
    }

    /**
     * 全部暂停下载任务
     */
    public static void stopAllDownTask() {
        if (mDownloadManager != null) {
            mDownloadManager.stopAll(Constant.USER_ID, Constant.AREA_ID);
        }
    }

    /**
     * 全部开始上传任务
     */
    public static void startAllUploadTask(int backUp) {
        if (mDownloadManager != null) {
            mUploadManager.startAll(Constant.USER_ID, Constant.AREA_ID, backUp);
        }
    }

    /**
     * 全部开始下载任务
     */
    public static void stopAllUploadTask(int backUp) {
        if (mDownloadManager != null) {
            mUploadManager.stopAll(Constant.USER_ID, Constant.AREA_ID, backUp);
        }
    }

    /**
     * 清除所有下载记录
     */
    public static void clearAllDownTaskRecord() {
        if (mDownloadManager != null) {
            Gonet.delAllDownloadFinishRecode(Constant.USER_ID, Constant.AREA_ID, mDownloadPath);
        }
    }

    /**
     * 清除所有上传记录
     */
    public static void clearAllUploadTaskRecord(int backUp) {
        if (mDownloadManager != null) {
            Gonet.delAllUploadFinishRecode(Constant.USER_ID, Constant.AREA_ID, mDownloadPath, backUp);
        }
    }
}
