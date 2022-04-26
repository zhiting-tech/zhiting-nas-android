package com.zhiting.clouddisk.mine.activity;

import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.tools.SPUtils;
import com.thl.filechooser.FileChooser;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.databinding.ActivityDownloadSettingBinding;
import com.zhiting.clouddisk.dialog.DownloadSelectNetDialog;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.main.activity.MainActivity;
import com.zhiting.clouddisk.mine.contract.DownloadSettingContract;
import com.zhiting.clouddisk.mine.presenter.DownloadSettingPresenter;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;

import java.io.File;

/**
 * 下载设置
 */
public class DownloadSettingActivity extends BaseMVPDBActivity<ActivityDownloadSettingBinding,
        DownloadSettingContract.View, DownloadSettingPresenter> implements DownloadSettingContract.View {

    @Override
    public int getLayoutId() {
        return R.layout.activity_download_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
        String path = SPUtils.getInstance().getString(Config.KEY_DOWNLOAD_PATH, GonetUtil.mDownloadPath);
        boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
        binding.tvDefaultPath.setText(path);
        if (isOnlyWifi) {
            binding.tvSelectNetDesc.setText("仅WiFi环境上传/下载");
        } else {
            binding.tvSelectNetDesc.setText("允许使用流量上传/下载");
        }
        binding.ivBack.setOnClickListener(v -> finish());
        binding.llNetSetting.setOnClickListener(v -> {
            showDownloadSelectNetDialog();
        });
        binding.llDownloadSetting.setOnClickListener(v -> selectDefaultPath());
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        selectDefaultPath();
    }

    private void selectDefaultPath() {
        FileChooser fileChooser = new FileChooser(DownloadSettingActivity.this, filePath -> {
            SPUtils.getInstance().put(Config.KEY_DOWNLOAD_PATH, filePath);
            GonetUtil.mDownloadPath = filePath;
            binding.tvDefaultPath.setText(filePath);
        });
        fileChooser.setCurrentPath(Environment.getExternalStorageDirectory().getAbsolutePath());
        fileChooser.showFile(false);
        fileChooser.open();
    }

    private void showDownloadSelectNetDialog() {
        DownloadSelectNetDialog dialog = DownloadSelectNetDialog.getInstance();
        dialog.show(this);
        dialog.setListener(text -> binding.tvSelectNetDesc.setText(text));
    }

    @Override
    public void restartTaskSuccess() {

    }

    @Override
    public void restartTaskFail(int errorCode, String msg) {

    }
}