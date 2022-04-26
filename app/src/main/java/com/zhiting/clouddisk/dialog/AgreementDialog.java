package com.zhiting.clouddisk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.home.activity.WebActivity;
import com.zhiting.clouddisk.util.IntentConstant;
import com.zhiting.networklib.utils.AgreementPolicyListener;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;

import org.jetbrains.annotations.NotNull;


public class AgreementDialog extends Dialog implements View.OnClickListener {

    private TextView tvContent;
    private TextView tvDisagree;
    private TextView tvAgree;

    private AgreementPolicyListener mAgreementPolicyListener;

    public AgreementDialog(@NonNull Context context) {
        super(context);
    }

    public AgreementDialog(@NonNull @NotNull Context context, AgreementPolicyListener agreementPolicyListener) {
        super(context);
        this.mAgreementPolicyListener = agreementPolicyListener;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setLayout((int) (UiUtil.getScreenWidth()/1.25), (int) (UiUtil.getScreenWidth()/1.52));
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.shape_white_c10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agreement);
        setCancelable(false);
        tvContent = findViewById(R.id.tvContent);
        tvDisagree = findViewById(R.id.tvDisagree);
        tvAgree = findViewById(R.id.tvAgree);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setText(StringUtil.setAgreementAndPolicyStyle(UiUtil.getString(R.string.warm_tips_content), UiUtil.getColor(R.color.color_2da3f6), false, false, mAgreementPolicyListener));
        tvDisagree.setOnClickListener(this::onClick);
        tvAgree.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tvDisagree){ // 不同意
            if(onOperateListener!=null){
                onOperateListener.onDisagree();
            }
        }else if (viewId == R.id.tvAgree){  // 同意
            if(onOperateListener!=null){
                onOperateListener.onAgree();
            }
        }
    }

    private OnOperateListener onOperateListener;

    public OnOperateListener getOnOperateListener() {
        return onOperateListener;
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    public interface OnOperateListener{
        void onDisagree();
        void onAgree();
    }
}
