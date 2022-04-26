package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;

public class ClearCacheDialog extends CommonBaseDialog {

    private TextView tvConfirm;
    private TextView tvCancel;

    public static ClearCacheDialog getInstance() {
        ClearCacheDialog centerAlertDialog = new ClearCacheDialog();
        return centerAlertDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_clear_cache;
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
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(v -> {
            if (confirmListener != null) {
                confirmListener.onCancel();
            }
            dismiss();
        });

        tvConfirm.setOnClickListener(v -> {
            if (confirmListener != null) {
                confirmListener.onConfirm();
            }
            dismiss();
        });
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onCancel();

        void onConfirm();
    }
}
