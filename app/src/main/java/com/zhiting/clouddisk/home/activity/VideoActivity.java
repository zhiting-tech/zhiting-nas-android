package com.zhiting.clouddisk.home.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityVideoBinding;
import com.zhiting.clouddisk.home.contract.VideoContract;
import com.zhiting.clouddisk.home.presenter.VideoPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends BaseMVPDBActivity<ActivityVideoBinding, VideoContract.View, VideoPresenter> implements VideoContract.View {

    private OrientationUtils orientationUtils;
    private String mVideoUrl;//视频播放地址
    private String mTitle;//视频播放标题
    private String mCover;//封面

    /**
     * @param context
     * @param videoUrl 播放地址
     * @param coverUrl 播放封面
     * @param title    视频名称
     */
    public static void startActivity(Context context, String videoUrl, String coverUrl, String title) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(Config.KEY_VIDEO_URL, videoUrl);
        intent.putExtra(Config.KEY_COVER_URL, coverUrl);
        intent.putExtra(Config.KEY_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mTitle = intent.getStringExtra(Config.KEY_TITLE);
        mCover = intent.getStringExtra(Config.KEY_COVER_URL);
        mVideoUrl = intent.getStringExtra(Config.KEY_VIDEO_URL);
        mVideoUrl = mVideoUrl.replace("//api", "/api");
        if (!mVideoUrl.startsWith("http")) mVideoUrl = "file:/" + mVideoUrl;
        try {
            mVideoUrl = URLDecoder.decode(mVideoUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mVideoUrl = mVideoUrl.replace("/api/wangpan/api/","/wangpan/api/");
        LogUtil.e(TAG+"mVideoUrl="+mVideoUrl);
    }

    @Override
    protected void initData() {
        super.initData();
        Map<String, String> header = new HashMap<>();
        header.put(HttpUrlParams.SCOPE_TOKEN, Constant.scope_token);

        LogUtil.e(TAG + "fileBean1=" + mVideoUrl);
        LogUtil.e(TAG + "fileBean2=Constant.scope_token=" + Constant.scope_token);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(mVideoUrl)
                .setMapHeadData(header)
                .setCacheWithPlay(false)
                .setVideoTitle(mTitle)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        binding.videoPlayer.clearThumbImageView();
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        binding.videoPlayer.getProgressView().setProgress(0);
                        binding.videoPlayer.getCurrent().setText("00:00");
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }

                    @Override
                    public void onPlayError(String url, Object... objects) {
                        super.onPlayError(url, objects);
                        binding.ivRestartPlay.setVisibility(View.VISIBLE);
                    }
                })
                .setLockClickListener((view, lock) -> {
                    if (orientationUtils != null) {
                        orientationUtils.setEnable(!lock);
                    }
                })
                .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) ->
                        Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration))
                .build(binding.videoPlayer);

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideUtil.load(mCover).into(imageView);

        binding.videoPlayer.setThumbImageView(imageView);
        //增加title
        binding.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        binding.videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, binding.videoPlayer);

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        binding.videoPlayer.getFullscreenButton().setOnClickListener(v -> orientationUtils.resolveByClick());

        //是否可以滑动调整
        binding.videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        binding.videoPlayer.getBackButton().setOnClickListener(v -> {
            finish();
        });

        binding.videoPlayer.getCustomBack().setOnClickListener(v -> {
            finish();
        });

        binding.ivRestartPlay.setOnClickListener(view1 -> {
            binding.videoPlayer.startPlayLogic();
            binding.ivRestartPlay.setVisibility(View.GONE);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.videoPlayer.onVideoPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            binding.videoPlayer.getFullscreenButton().performClick();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        //释放所有
        binding.videoPlayer.setVideoAllCallBack(null);
    }
}
