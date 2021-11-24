package com.zhiting.networklib.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.zhiting.networklib.utils.UiUtil;


public abstract class CommonBaseDialog extends BitBaseDialogFragment {

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater,
                                        @Nullable ViewGroup container,
                                        @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(getLayoutResource(),
                container, false);
        return fragmentView;
    }

    abstract protected int getLayoutResource();

    protected boolean getCancelable() {
        return true;
    }

    abstract protected int obtainWidth();

    abstract protected int obtainHeight();

    abstract protected int obtainGravity();

    @Override
    protected void initWindows(Window window) {

        setCancelable(getCancelable());

        View decorView = window.getDecorView();
        decorView.setPadding(0, 0, 0, 0);
        window.setLayout(obtainWidth(), obtainHeight());
        window.setGravity(obtainGravity());
        super.initWindows(window);

    }

    protected int dp2px(float dp) {
        return UiUtil.dip2px((int) dp);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    /**
     * 显示软键盘
     */
    public void showInput(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }
    /**
     * 隐藏软键盘
     */
    public void hideInput(View view){
        InputMethodManager inputManager =
                (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
