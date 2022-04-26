package com.zhiting.clouddisk.tbswebview;

import android.os.Bundle;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.home.activity.TBSWebActivity;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.http.SSLSocketClient;
import com.zhiting.networklib.http.cookie.CookieJarImpl;
import com.zhiting.networklib.http.cookie.PersistentCookieStore;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class DownloadUtil {
    public static String previewPath = BaseFileUtil.getCachePath(UiUtil.getContext());
    private static final String TAG = "DownloadUtil===";
    private static DownloadUtil downloadUtil;
    private static OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    public DownloadUtil() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .allEnabledCipherSuites()
                .build();
        // 兼容http接口
        ConnectionSpec spec1 = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder
                .connectTimeout(HttpConfig.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.writeTimeOut, TimeUnit.SECONDS)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .retryOnConnectionFailure(true)
                .connectionSpecs(Arrays.asList(spec, spec1))
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance()));
        okHttpClient = builder.build();
    }

    public void startDownload(BaseActivity activity, String url, String name) {
        showLoading(activity, true);
        String newUrl = url.replace("/api/wangpan/api/","/wangpan/api/");
        if (!newUrl.startsWith("http")) {
            showLoading(activity, false);
            previewFile(activity, newUrl, name);
            return;
        }
        Observable.create((ObservableOnSubscribe<FileVo>) e -> {
            final FileVo fileVo = new FileVo();
            downloadUtil.download(newUrl, previewPath, name, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file) {
                    fileVo.setFile(file);
                    e.onNext(fileVo);
                    e.onComplete();
                }

                @Override
                public void onDownloading(int progress) {
                    Log.e(TAG, "progress=" + progress);
                }

                @Override
                public void onDownloadFailed(Exception e) {
                    Log.e(TAG, "progress=onDownloadFailed=" + e.getMessage());
                    ToastUtil.show("打开失败");
                    showLoading(activity, false);
                }
            });
        }).compose(RxUtils.schedulersTransformer()).subscribe((Consumer<FileVo>) fileVo -> {
            showLoading(activity, false);
            previewFile(activity, fileVo.getFile().getAbsolutePath(), name);
        });
    }

    public void showLoading(BaseActivity activity, boolean show) {
        if (activity != null && !activity.isDestroyed()) {
            if (show) {
                activity.showLoading();
            } else {
                activity.hideLoading();
            }
        }
    }

    public void previewFile(BaseActivity activity, String url, String name) {
        if (!Constant.isX5Success) {
            QbSdk.openFileReader(activity, url, null, null);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TITLE, name);
            bundle.putString(IntentConstant.WEB_URL, url);
            activity.switchToActivity(TBSWebActivity.class, bundle);
        }
    }

    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     * @param listener     下载监听
     */
    public void download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
        Log.e(TAG, "scope_token=" + Constant.scope_token);
        Log.e(TAG, "area_id=" + Constant.AREA_ID);
        Request request = new Request.Builder()
                .addHeader(HttpUrlParams.SCOPE_TOKEN, Constant.scope_token)
                .addHeader("area_id", "" + Constant.AREA_ID)
                .url(url)
                .build();

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public interface OnDownloadListener {
        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);
    }
}
