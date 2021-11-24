package com.zhiting.networklib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.zhiting.networklib.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class LoadingDialog extends Dialog {

    private GifImageView gifImageView;
    private GifDrawable gifDrawable;
    private Context mContext;


    public LoadingDialog(@NonNull @NotNull Context context) {
        super(context);
        this.mContext = context;
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        gifImageView = (GifImageView) findViewById(R.id.givLoading);

    }

    public void reset(){
        try {
            gifDrawable = new GifDrawable(mContext.getResources(), R.drawable.loading);
            gifDrawable.setLoopCount(0);
            gifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if (gifDrawable!=null){
            gifDrawable.stop();
            gifImageView.setImageResource(R.drawable.loading_static);
            gifDrawable = null;
        }
    }

}
