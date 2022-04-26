package com.zhiting.clouddisk.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.zhiting.clouddisk.R;

public class VideoPlayerView extends StandardGSYVideoPlayer {

    private Handler refreshNetSpeedHandler;
    private static final long REFRESH_INTERVAL = 200L;
    private String lastNetSpeedText;//最后一次获得的网速

    private boolean needRefreshNetSpeed = false;//是否需要开启更新网速

    private View playButton;

    private View customBack;

    private View rightMenu;

    private View layoutBottom;

    private SeekBar progress;
    private TextView current;

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        playButton = findViewById(R.id.iv_play_button);
        rightMenu = findViewById(R.id.menu);
        layoutBottom = findViewById(R.id.layout_bottom);
        progress = findViewById(R.id.progress);
        current = findViewById(R.id.current);

        layoutBottom.setVisibility(VISIBLE);
        playButton.setOnClickListener(v -> {
            clickStartIcon();
        });
    }

    @Override
    protected void init(Context context) {
        refreshNetSpeedHandler = new Handler();
        super.init(context);
        customBack = findViewById(R.id.custom_back);
        if (mLoadingProgressBar instanceof ProgressBarWithTextView) {
            ((ProgressBarWithTextView) mLoadingProgressBar).setOnVisibilityChangeListener(visibility -> {
                if (visibility == VISIBLE) {
                    startRefreshNetSpeed();
                } else {
                    stopRefreshNetSpeed();
                }
            });
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        //setDismissControlTime(1500);//初始化完成后，设置按钮消失时间为1.5s
    }

    public TextView getCurrent() {
        return current;
    }

    public SeekBar getProgressView() {
        return progress;
    }

    public View getCustomBack() {
        return customBack;
    }

    public View getBottomView() {
        return layoutBottom;
    }

    public View getRightMenu() {
        return rightMenu;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_video_player;
    }

    @Override
    public int getShrinkImageRes() {
        return R.drawable.icon_video_screen;
    }

    @Override
    public int getEnlargeImageRes() {
        return R.drawable.icon_video_screen;
    }

    @Override
    public void onBufferingUpdate(int secPercent) {
        //可以在这处理缓存进度条问题
        super.onBufferingUpdate(secPercent);
    }

    private void setNetSpeed() {
        if (mLoadingProgressBar != null && mLoadingProgressBar.getVisibility() == VISIBLE && mLoadingProgressBar instanceof ProgressBarWithTextView) {
            ((ProgressBarWithTextView) mLoadingProgressBar).setText(lastNetSpeedText);
        }
    }

    private Runnable refreshNetSpeedTask = new Runnable() {
        @Override
        public void run() {
            lastNetSpeedText = getNetSpeedText();
            setNetSpeed();
            if (needRefreshNetSpeed) {
                refreshNetSpeedHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        }
    };

    private void startRefreshNetSpeed() {
        needRefreshNetSpeed = true;
        refreshNetSpeedHandler.post(refreshNetSpeedTask);
    }

    private void stopRefreshNetSpeed() {
        needRefreshNetSpeed = false;
        refreshNetSpeedHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void updateStartImage() {
        if (playButton instanceof ImageView) {
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                playButton.setSelected(false);
            } else {
                playButton.setSelected(true);
            }
        }
    }
}
