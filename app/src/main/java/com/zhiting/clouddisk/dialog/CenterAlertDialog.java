package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.UiUtil;

public class CenterAlertDialog extends CommonBaseDialog {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvTip;
    private TextView tvCancel;
    private TextView tvConfirm;

    private String title;  // 标题
    private String content;  // 内容
    private String tips;  // 提示
    private int tipsTextColor; // 提示文案颜色
    private String leftText; // 操作按钮左边文案
    private String rightText; // 操作按钮右边文案

    public static CenterAlertDialog getInstance(String title,  String tips){
        return getInstance(title, "", tips, 0, "", "");
    }

    public static CenterAlertDialog getInstance(String title, String content, String tips, int tipsTextColor, String leftText, String rightText){
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("tips", tips);
        args.putString("leftText", leftText);
        args.putString("rightText", rightText);
        args.putInt("tipsTextColor", tipsTextColor);
        CenterAlertDialog centerAlertDialog = new CenterAlertDialog();
        centerAlertDialog.setArguments(args);
        return centerAlertDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_center_alert;
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
        title = arguments.getString("title");
        content = arguments.getString("content");
        tips = arguments.getString("tips");
        leftText = arguments.getString("leftText");
        rightText = arguments.getString("rightText");
        tipsTextColor = arguments.getInt("tipsTextColor");
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvContent = view.findViewById(R.id.tvContent);
        tvTip = view.findViewById(R.id.tvTip);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        tvTitle.setText(title);
        tvContent.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        tvContent.setText(content);
        tvTip.setVisibility(TextUtils.isEmpty(tips) ? View.GONE : View.VISIBLE);
        tvTip.setText(tips);
        if (!TextUtils.isEmpty(leftText)) {  // 设置左边操作按钮文案
            tvCancel.setText(leftText);
        }
        if (!TextUtils.isEmpty(rightText)){ // 设置右边操作按钮文案
            tvConfirm.setText(rightText);
        }
        if (tipsTextColor!=0){
            tvTip.setTextColor(UiUtil.getColor(tipsTextColor));
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener!=null){
                    confirmListener.onConfirm();
                }
            }
        });
    }

    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm();
    }
}
