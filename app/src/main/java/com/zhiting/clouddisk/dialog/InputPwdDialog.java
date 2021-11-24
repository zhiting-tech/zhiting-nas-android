package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

public class InputPwdDialog extends CommonBaseDialog {

    private EditText etPwd;
    private TextView tvCancel;
    private TextView tvConfirm;

    public static InputPwdDialog getInstance(){
        InputPwdDialog inputPwdDialog = new InputPwdDialog();
        return inputPwdDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_input_pwd;
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
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.EditDialog);
    }

    @Override
    protected void initView(View view) {

        etPwd = view.findViewById(R.id.etPwd);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput(etPwd);
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPwd.getText().toString().trim())){
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_input_pwd));
                    return;
                }
                if (confirmListener!=null){
                    confirmListener.onConfirm(etPwd.getText().toString().trim());
                }
            }
        });
        showInput(etPwd);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        hideInput(etPwd);
        etPwd.setText("");
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm(String pwd);
    }
}
