package com.zhiting.clouddisk.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.TbsReaderView;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityTbsWebBinding;
import com.zhiting.clouddisk.home.contract.WebContract;
import com.zhiting.clouddisk.home.presenter.WebPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.io.File;

/**
 * 腾讯X5浏览器
 */
public class TBSWebActivity extends BaseMVPDBActivity<ActivityTbsWebBinding, WebContract.View, WebPresenter> implements WebContract.View {

    private TbsReaderView mTbsReaderView;
    private String mFileUrl;
    private String mFileName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tbs_web;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mFileName = intent.getStringExtra(IntentConstant.TITLE);
        mFileUrl = intent.getStringExtra(IntentConstant.WEB_URL);
        binding.tvTitle.setText(mFileName);
    }

    @Override
    protected void initData() {
        super.initData();
        binding.ivBack.setOnClickListener(v -> finish());
        mTbsReaderView = new TbsReaderView(this, null);
        binding.llRoot.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        File file = new File(mFileUrl);
        if (file.exists()) {
            openFile(mFileUrl);
        }
    }

    public void openFile(String path) {
        String bsReaderTemp = Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                ToastUtil.show("发生了错误，请稍后重试");
                return;
            }
        }
        //加载文件
        Bundle localBundle = new Bundle();
        localBundle.putString("filePath", path);
        localBundle.putString("tempPath", bsReaderTemp);

        boolean canOpen = mTbsReaderView.preOpen(parseFormat(parseName(path)), false);
        if (canOpen) {
            mTbsReaderView.openFile(localBundle);
        } else {
            ToastUtil.show("无法打开此文件");
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String parseName(String url) {
        String fileName = null;
        try {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        } finally {
            if (TextUtils.isEmpty(fileName)) {
                fileName = String.valueOf(System.currentTimeMillis());
            }
        }
        return fileName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁界面的时候一定要加上，否则后面加载文件会发生异常。
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
            binding.llRoot.removeView(mTbsReaderView);
        }
    }
}