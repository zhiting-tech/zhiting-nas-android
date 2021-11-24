package com.zhiting.networklib.base.mvp;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonSyntaxException;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.entity.BaseUrlEvent;
import com.zhiting.networklib.entity.ChannelEntity;
import com.zhiting.networklib.factory.BaseApiServiceFactory;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter<V> {

    protected M mModel;
    protected V mView;

    @Override
    public void attachView(V view) {
        this.mView = view;
        mModel = createModel();
    }

    @Override
    public void detachView() {
        if (mModel != null) {
            mModel.removeDisposal();
        }
        mModel = null;
        mView = null;
    }

    public abstract M createModel();

    /**
     * 网络请求
     *
     * @param observable
     * @param callback
     * @param <T>
     */
    public <T> void executeObservable(Observable<BaseResponseEntity<T>> observable, RequestDataCallback<T> callback) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponseEntity<T>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mModel.addDisposable(d);
                        if (mView != null && callback != null && callback.isLoading())
                            mView.showLoading();
                    }

                    @Override
                    public void onNext(@NonNull BaseResponseEntity<T> response) {
                        if (callback != null) {
                            if (response.getStatus() == 0) {  // 成功
                                callback.onSuccess(response.getData());
                            } else {  // 失败
                                if (mView != null) {
                                    if (callback != null && callback.isLoading())
                                        mView.hideLoading();
                                    if (response.getStatus() != ErrorConstant.INVALID_AUTH)  // 如果不是无效的授权才需要土司提示
                                        showError(response.getReason());  // 提示错误信息
                                    mView.showError(response.getStatus(), response.getReason());  // 提示错误信息
                                }
                                callback.onFailed(response.getStatus(), response.getReason());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        String error = "";
                        if (e instanceof ConnectException || e instanceof UnknownHostException) {
                            error = "网络异常，请检查网络";
                        } else if (e instanceof TimeoutException || e instanceof SocketTimeoutException) {
                            error = "网络不畅，请稍后再试！";
                        } else if (e instanceof JsonSyntaxException) {
                            error = "数据解析异常";
                        } else {
                            //error = "服务端错误";
                        }
                        if (mView != null) {
                            if (callback != null && callback.isLoading())
                                mView.hideLoading();
                            showError(error);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null && callback != null && callback.isLoading())
                            mView.hideLoading();
                    }
                });
    }

    /**
     * 数据库
     *
     * @param observable
     * @param callback
     * @param <T>
     */
    public <T> void executeDBObservable(Observable<T> observable, RequestDataCallback<T> callback) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mModel.addDisposable(d);
                    }

                    @Override
                    public void onNext(@NotNull T data) {
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (callback != null) {
                            callback.onFailed();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 提示错误
     *
     * @param msg
     */
    private void showError(String msg) {
        if (mView != null && !TextUtils.isEmpty(msg)) {
            mView.showError(msg);
        }
    }

    /**
     * 临时通道
     */
    public static class OnTempChannelListener {
        public void onSuccess() {
        }

        public void onError(int code, String msg) {
        }
    }

    public void checkTempChannel(OnTempChannelListener listener) {
        boolean isSA = SpUtil.getBoolean(SpConstant.IS_SA);
        if (isSA && HttpConfig.baseTestUrl.contains(HttpConfig.baseSAHost)) {
            listener.onSuccess();
        } else {
            if (isWithinTime()) {
                Log.e("BasePresenter1=", "111");
                listener.onSuccess();
            } else {
                Log.e("BasePresenter2=", "222");
                checkTemporaryChannel(listener);
            }
        }
    }

    /**
     * 是否在有限时间内
     */
    private boolean isWithinTime() {
        String tokenKey = SpUtil.getString(SpConstant.SA_TOKEN);
        String json = SpUtil.getString(tokenKey);
        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                long currentTime = TimeFormatUtil.getCurrentTime();
                if ((currentTime - channel.getCreate_channel_time() + 60) < channel.getExpires_time()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 请求新的通道
     */
    private void checkTemporaryChannel(OnTempChannelListener listener) {
        String areaId = SpUtil.getString(SpConstant.HOME_ID);
        String cookie = SpUtil.getString(SpConstant.COOKIE);
        Map<String, String> map = new HashMap<>();
        map.put("scheme", "http");
        Observable observable = BaseApiServiceFactory.getApiService().getChannel(areaId, cookie, map);
        executeObservable(observable, new RequestDataCallback<ChannelEntity>(false) {
            @Override
            public void onSuccess(ChannelEntity response) {
                super.onSuccess(response);
                String newUrl = "http://" + response.getHost() + "/";
                LogUtil.e("realCheckTemporaryChannel===" + newUrl);
                EventBus.getDefault().post(new BaseUrlEvent(newUrl));
                UiUtil.postDelayed(() -> {
                    saveTemporaryUrl(response);
                    listener.onSuccess();
                }, 200);
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                listener.onError(errorCode, errorMessage);
            }
        });
    }

    private void saveTemporaryUrl(ChannelEntity bean) {
        bean.setGenerate_channel_time(TimeFormatUtil.getCurrentTime());
        String tokenKey = SpUtil.getString(SpConstant.SA_TOKEN);
        String value = GsonConverter.getGson().toJson(bean);
        SpUtil.put(tokenKey, value);
    }
}
