package com.zhiting.networklib.utils.toast;

import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.UiUtil;


public class ToastUtil {
    private static MToast TOAST;

    public static void init() {
        TOAST = MToast.makeText(LibLoader.getApplication(), "", Toast.LENGTH_SHORT);
    }

    public static void show(@NonNull String message) {
        showCenter(message);
    }

    public static void showTop(String message) {
        TOAST.setText(message)
                .setGravity(Gravity.CENTER | Gravity.TOP, 0, 0)
                .show();
    }

    public static void showTop(@StringRes int resId) {
        showTop(UiUtil.getString(resId));
    }

    public static void showCenter(String message) {
        TOAST.setText(message)
                .setGravity(Gravity.CENTER, 0, 0)
                .show();
    }

    public static void showCenter(@StringRes int resId) {
        showCenter(UiUtil.getString(resId));
    }

    public static void showBottom(String message) {
        TOAST.setText(message)
                .setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0)
                .show();
    }

    public static void showBottom(@StringRes int resId) {
        showBottom(UiUtil.getString(resId));
    }
}
