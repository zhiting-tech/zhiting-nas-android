package com.zhiting.clouddisk.util;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhiting.networklib.utils.UiUtil;

public class ViewSizeUtil {

    public static void setViewSize(ImageView view, int type) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (type == 5 || type == 7) {
            params.width = UiUtil.dip2px(36);
            params.height = UiUtil.dip2px(36);
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        view.setLayoutParams(params);
    }
}
