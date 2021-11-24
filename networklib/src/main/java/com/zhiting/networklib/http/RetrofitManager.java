package com.zhiting.networklib.http;

import android.text.TextUtils;
import android.util.Log;

import com.hjq.gson.factory.GsonFactory;
import com.zhiting.networklib.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    public final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static RetrofitManager INSTANCE;
    private Retrofit retrofit;

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    private RetrofitManager(String baserUrl) {
        baserUrl = HttpUtils.getHttpUrl(baserUrl);
        Log.e("RetrofitManager","=baseUrl="+baserUrl);
        OkHttpClient client = getOkHttpClient();
        retrofit = new Retrofit.Builder()
                .baseUrl(baserUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    public static RetrofitManager getInstance(String baserUrl) {
        if (INSTANCE == null && !TextUtils.isEmpty(baserUrl)) {
            synchronized (RetrofitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitManager(baserUrl);
                }
            }
        }
        return INSTANCE;
    }

    public static void releaseRetrofitManager() {
        INSTANCE = null;
    }

    private OkHttpClient getOkHttpClient() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .allEnabledCipherSuites()
                .build();
        // 兼容http接口
        ConnectionSpec spec1 = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(getHttpLoginInterceptor())
                .addInterceptor(new BaseUrlInterceptor())
                .connectTimeout(HttpConfig.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.writeTimeOut, TimeUnit.SECONDS)
                //.hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                //.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .retryOnConnectionFailure(true)
                .connectionSpecs(Arrays.asList(spec, spec1));
        return builder.build();
    }

    private HttpLoggingInterceptor getHttpLoginInterceptor() {
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(s -> {
            try {
                String content = new String(s.getBytes("UTF-8"), "UTF-8");
                printJson("content:", URLDecoder.decode(content, "UTF-8"), "");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        httpLoggingInterceptor.setLevel(level);
        return httpLoggingInterceptor;
    }

    private void printLine(String tag, boolean top) {
        if (top) {
            Log.d(tag, "**********************************开始***************************************");
        } else {
            Log.d(tag, "**********************************结束***************************************");
        }
    }

    private void printJson(String tag, String msg, String headerString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        //printLine(tag, true);
        message = headerString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.e(tag, "* " + line);
        }
        //printLine(tag, false);
    }
}
