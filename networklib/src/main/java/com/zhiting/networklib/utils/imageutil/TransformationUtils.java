package com.zhiting.networklib.utils.imageutil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zhiting.networklib.R;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.widget.ScaleImageView;

public class TransformationUtils extends SimpleTarget<Bitmap> {
    private ScaleImageView target;

    public TransformationUtils(ScaleImageView target) {
        this.target = target;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        target.setImageBitmap(resource);
        //获取原图的宽高
        int width = resource.getWidth();
        int height = resource.getHeight();
        int toWidth = UiUtil.getScreenWidth() / 2 - UiUtil.getDimens(R.dimen.dp_18);
        target.setInitSize(toWidth, (int) (height*((float)toWidth/width)));
    }
}
