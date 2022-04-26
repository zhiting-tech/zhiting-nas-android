package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luck.picture.lib.tools.SPUtils;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;

public class DownloadSelectNetDialog extends CommonBaseDialog {

    private TextView tvWifi, tvAll;

    public static DownloadSelectNetDialog getInstance() {
        DownloadSelectNetDialog confirmDialog = new DownloadSelectNetDialog();
        return confirmDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_download_select_net;
    }

    @Override
    protected int obtainWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {
    }

    @Override
    protected void initView(View view) {
        tvWifi = view.findViewById(R.id.tvWifi);
        tvAll = view.findViewById(R.id.tvAll);
        boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
        tvWifi.setSelected(isOnlyWifi);
        tvAll.setSelected(!isOnlyWifi);

        tvWifi.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectType(tvWifi.getText().toString());
            }
            SPUtils.getInstance().put(Config.KEY_ONLY_WIFI, true);
            dismiss();

        });

        tvAll.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectType(tvAll.getText().toString());
            }
            SPUtils.getInstance().put(Config.KEY_ONLY_WIFI, false);
            dismiss();
        });
    }

    public void setListener(onSelectListener listener) {
        this.listener = listener;
    }

    public onSelectListener listener;

    public interface onSelectListener {
        void onSelectType(String text);
    }
}
