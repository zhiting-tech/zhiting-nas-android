package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.UiUtil;

public class TrafficTipsDialog extends CommonBaseDialog {

    private TextView tvCancel, tvConfirm;

    public static TrafficTipsDialog getInstance() {
        TrafficTipsDialog confirmDialog = new TrafficTipsDialog();
        return confirmDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_traffic_tips;
    }

    @Override
    protected int obtainWidth() {
        return UiUtil.getDimens(R.dimen.dp_300);
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
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
            if (mConfirmListener != null) {
                mConfirmListener.onPause();
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(v -> {
            if (mConfirmListener != null) {
                mConfirmListener.onResume();
                dismiss();
            }
        });
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    private OnConfirmListener mConfirmListener;

    public TrafficTipsDialog setConfirmListener(OnConfirmListener mConfirmListener) {
        this.mConfirmListener = mConfirmListener;
        return this;
    }

    public interface OnConfirmListener {
        void onResume();

        void onPause();
    }
}
