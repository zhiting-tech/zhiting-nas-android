package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;

public class More10MDialog extends CommonBaseDialog {

    private TextView tvPreview;
    private TextView tvDownload;

    public static More10MDialog getInstance() {
        More10MDialog centerAlertDialog = new More10MDialog();
        return centerAlertDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_more_10m;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        tvPreview = view.findViewById(R.id.tvPreview);
        tvDownload = view.findViewById(R.id.tvDownload);

        tvPreview.setOnClickListener(v -> {
            if (confirmListener != null) {
                confirmListener.onReview();
            }
            dismiss();
        });

        tvDownload.setOnClickListener(v -> {
            if (confirmListener != null) {
                confirmListener.onDownload();
            }
            dismiss();
        });
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onReview();

        void onDownload();
    }
}
