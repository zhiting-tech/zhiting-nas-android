package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.UiUtil;

public class ConfirmDialog extends CommonBaseDialog {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvConfirm;

    private String title;
    private String content;
    private String confirmText;
    private boolean mCancelable;

    public static ConfirmDialog getInstance() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        return confirmDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_confirm;
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
        title = arguments.getString("title");
        content = arguments.getString("content");
        confirmText = arguments.getString("confirmText");
        mCancelable = arguments.getBoolean("cancelable", true);
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvContent = view.findViewById(R.id.tvContent);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        tvContent.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(confirmText))
            tvConfirm.setText(confirmText);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
            }
        });
    }

    @Override
    protected boolean getCancelable() {
        return mCancelable;
    }


    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
