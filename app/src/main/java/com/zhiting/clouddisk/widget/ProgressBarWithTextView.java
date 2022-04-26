package com.zhiting.clouddisk.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhiting.clouddisk.R;

public class ProgressBarWithTextView extends RelativeLayout {

    private TextView tvText;

    private OnVisibilityChangeListener listener;

    public ProgressBarWithTextView(Context context){
        this(context, null);
    }

    public ProgressBarWithTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.layout_progress_bar_with_text, this);
        tvText = view.findViewById(R.id.tv_text);
    }

    public void setText(@NonNull String text){
        if(tvText != null && !TextUtils.isEmpty(text)){
            tvText.setText(text);
        }
    }

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void setVisibility(int visibility){
        int lastVisibility = getVisibility();
        if(lastVisibility != visibility){
            super.setVisibility(visibility);
            if(listener != null){
                listener.onVisibilityChange(visibility);
            }
        }
    }

    static interface OnVisibilityChangeListener{
        void onVisibilityChange(int visibility);
    }

}
