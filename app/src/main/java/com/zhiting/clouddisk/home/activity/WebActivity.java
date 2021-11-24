package com.zhiting.clouddisk.home.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityWebBinding;
import com.zhiting.clouddisk.home.contract.WebContract;
import com.zhiting.clouddisk.home.presenter.WebPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.utils.WebViewInitUtil;

/**
 * 加载网页
 */
public class WebActivity extends BaseMVPDBActivity<ActivityWebBinding, WebContract.View, WebPresenter> implements WebContract.View {

    private String title;
    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.setProgressBar(binding.progressbar);
        webViewInitUtil.initWebView(binding.webView);
        binding.webView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        title = intent.getStringExtra(IntentConstant.TITLE);
        url = intent.getStringExtra(IntentConstant.WEB_URL);
        binding.tvTitle.setText(title);
        binding.webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.webView.clearCache(true);
        binding.webView.clearHistory();
        binding.webView.destroy();
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack){
                finish();
            }
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "onReceivedError=errorCode=" + errorCode + ",description=" + description + ",failingUrl=" + failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.e(TAG, "shouldOverrideUrlLoading");
            return true;
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    }
}