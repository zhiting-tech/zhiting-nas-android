package com.zhiting.networklib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.zhiting.networklib.R;

import org.jetbrains.annotations.NotNull;

public class NoScrollViewPager extends ViewPager {

    private boolean scrollable;

    public NoScrollViewPager(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public NoScrollViewPager(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs!=null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NoScrollViewPager);
            scrollable = array.getBoolean(R.styleable.RefreshRecyclerView_enabledRefresh, false);
            array.recycle();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollable) {
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable) {
            return super.onTouchEvent(ev);
        }else {
            return true;
        }
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
