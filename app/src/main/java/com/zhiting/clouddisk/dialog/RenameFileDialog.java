package com.zhiting.clouddisk.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 文件/文件夹 重命名
 */
public class RenameFileDialog extends BaseBottomDialog {

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvComplete;
    private ImageView ivLogo;
    private ImageView ivClear;
    private EditText etName;

    /**
     * 0. 新建文件
     * 1. 重新命名
     */
    private int type;
    private int drawable;
    private String name;

    public static RenameFileDialog getInstance(int type, int drawable, String name){
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("drawable", drawable);
        args.putString("name", name);
        RenameFileDialog renameFileDialog = new RenameFileDialog();
        renameFileDialog.setArguments(args);
        return renameFileDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_rename_file;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.EditDialog);
    }

    @Override
    protected void initArgs(Bundle arguments) {
        type = arguments.getInt("type");
        drawable = arguments.getInt("drawable");
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
        tvTitle.setText(type == 0 ? UiUtil.getString(R.string.home_create_file) : UiUtil.getString(R.string.home_rename));
        ivLogo.setImageResource(drawable);
        if (!TextUtils.isEmpty(name)){
            etName.setText(name);
            etName.setSelection(name.length());
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
                String fileName = etName.getText().toString();
                if (TextUtils.isEmpty(fileName)){
                    ToastUtil.show(UiUtil.getString(R.string.home_please_input_name));
                    return;
                }
                hideInput(etName);
                if (completeListener!=null){
                    completeListener.onComplete(type, fileName);
                }
            }
        });

        /**
         * 情况输入内容
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
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (completeListener!=null){
            completeListener.onDismissListener(dialog);
        }
    }

    private OnCompleteListener completeListener;

    public OnCompleteListener getCompleteListener() {
        return completeListener;
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public interface OnCompleteListener{
        void onComplete(int type, String fileName);
        void onDismissListener(DialogInterface dialog);
    }
}
