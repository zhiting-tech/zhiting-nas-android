package com.zhiting.networklib.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhiting.networklib.base.activity.BaseActivity;


public class TBSWebViewInitUtil {
    private BaseActivity activity;
    private ProgressBar progressBar;

    public TBSWebViewInitUtil(BaseActivity activity) {
        this.activity = activity;
    }

    public TBSWebViewInitUtil setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(WebView webView) {
        webView.setHorizontalScrollBarEnabled(false);// 滚动条水平不显示
        webView.setVerticalScrollBarEnabled(false); // 垂直不显示
        webView.getSettings().setJavaScriptEnabled(true);// 允许 JS的使用
        webView.getSettings().setAllowFileAccess(false); //修复file域漏洞
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 可以使用JS打开新窗口
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setNeedInitialFocus(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 解决缓存问题
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new BankWebChromeClient());
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (activity != null && !activity.isFinishing())
                activity.hideLoading();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtil.i("errorCode = " + errorCode + " description = " + description + " failingUrl = " + failingUrl);
            view.loadUrl("file:///android_asset/html_error_page.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class BankWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (activity != null && !activity.isFinishing())
                    activity.hideLoading();
            }
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100)
                    progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean onJsTimeout() {
            return super.onJsTimeout();
        }
    }
}
