package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 添加到新的存储池/修改名称
 */
public class AddStoragePoolDialog extends BaseBottomDialog {

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvComplete;
    private ImageView ivLogo;
    private ImageView ivClear;
    private EditText etName;

    /**
     * 0. 添加到新的存储池
     * 1. 修改名称
     */
    private int type;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AddStoragePoolDialog getInstance(int type, String name){
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("name", name);
        AddStoragePoolDialog addStoragePoolDialog = new AddStoragePoolDialog();
        addStoragePoolDialog.setArguments(args);
        return addStoragePoolDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_add_storage_pool;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.EditDialog);
    }

    @Override
    protected void initArgs(Bundle arguments) {
        type = arguments.getInt("type");
        name = arguments.getString("name");
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvComplete = view.findViewById(R.id.tvComplete);
        ivLogo = view.findViewById(R.id.ivLogo);
        ivClear = view.findViewById(R.id.ivClear);
        etName = view.findViewById(R.id.etName);
        tvTitle.setText(type == 0 ? UiUtil.getString(R.string.mine_add_new_storage_pool) : UiUtil.getString(R.string.mine_modify_name));
        if (!TextUtils.isEmpty(name)){
            etName.setText(name);
        }

        /**
         * 取消
         */
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput(etName);
                dismiss();
            }
        });

        /**
         * 完成
         */
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poolName = etName.getText().toString();
                if (TextUtils.isEmpty(poolName)){
                    ToastUtil.show(UiUtil.getString(R.string.home_please_input_name));
                    return;
                }
                hideInput(etName);
                if (completeListener!=null){
                    completeListener.onComplete(type, poolName);
                }

            }
        });

        /**
         * 清空输入内容
         */
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
            }
        });

        /**
         * 文本变化
         */
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ivClear.setVisibility(TextUtils.isEmpty(etName.getText().toString().trim()) ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        etName.setText("");
    }

    private OnCompleteListener completeListener;

    public OnCompleteListener getCompleteListener() {
        return completeListener;
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public interface OnCompleteListener{
        void onComplete(int type, String poolName);
    }
}
