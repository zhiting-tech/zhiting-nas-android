package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

/**
 * 修改文件夹密码
 */
public class ChangeFolderPwdDialog extends BaseBottomDialog implements View.OnClickListener {

    private TextView tvCancel;
    private TextView tvConfirm;
    private EditText etOld;
    private EditText etNew;
    private EditText etConfirmNew;
    private ImageView ivClearOld;
    private ImageView ivClearNew;
    private ImageView ivClearConfirmNew;

    public static ChangeFolderPwdDialog getInstance(){
        ChangeFolderPwdDialog changeFolderPwdDialog = new ChangeFolderPwdDialog();
        return changeFolderPwdDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_change_folder_pwd;
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
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        etOld = view.findViewById(R.id.etOld);
        etNew = view.findViewById(R.id.etNew);
        etConfirmNew = view.findViewById(R.id.etConfirmNew);
        ivClearOld = view.findViewById(R.id.ivClearOld);
        ivClearNew = view.findViewById(R.id.ivClearNew);
        ivClearConfirmNew = view.findViewById(R.id.ivClearConfirmNew);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        ivClearOld.setOnClickListener(this);
        ivClearNew.setOnClickListener(this);
        ivClearConfirmNew.setOnClickListener(this);
        etOld.setFocusable(true);
        etOld.requestFocus();
        showInput(etOld);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tvCancel){  // 取消
            dismiss();
        }else if (viewId == R.id.tvConfirm){  // 确定
            confirm();
        }else if (viewId == R.id.ivClearOld){  // 清除旧密码
            etOld.setText("");
        }else if (viewId == R.id.ivClearNew){  // 清除新密码
            etNew.setText("");
        }else if (viewId == R.id.ivClearConfirmNew){  // 清除确认密码
            etConfirmNew.setText("");
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        hideInput(etOld);
        etOld.setText("");
        etNew.setText("");
        etConfirmNew.setText("");
    }

    /**
     * 确定
     */
    private void confirm(){
        String oldPwd = etOld.getText().toString().trim();
        String newPwd = etNew.getText().toString().trim();
        String confirmPwd = etConfirmNew.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)){
            ToastUtil.show(UiUtil.getString(R.string.mine_not_input_pwd));
            return;
        }
        if (oldPwd.length()<6){
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_length));
            return;
        }

        if (TextUtils.isEmpty(newPwd)){
            ToastUtil.show(UiUtil.getString(R.string.mine_not_input_pwd));
            return;
        }
        if (newPwd.length()<6){
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_length));
            return;
        }

        if (TextUtils.isEmpty(confirmPwd)){
            ToastUtil.show(UiUtil.getString(R.string.mine_not_input_pwd));
            return;
        }
        if (confirmPwd.length()<6){
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_length));
            return;
        }
        if (!newPwd.equals(confirmPwd)){
            ToastUtil.show(UiUtil.getString(R.string.mine_new_is_inconsistent_with_confirm));
            return;
        }
        if (confirmListener!=null){
            confirmListener.onConfirm(oldPwd, newPwd, confirmPwd);
        }
    }

    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm(String oldPwd, String newPwd, String confirmPwd);
    }
}
