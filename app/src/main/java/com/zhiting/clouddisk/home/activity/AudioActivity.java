package com.zhiting.clouddisk.home.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityAudioBinding;
import com.zhiting.clouddisk.home.contract.VideoContract;
import com.zhiting.clouddisk.home.presenter.VideoPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.networklib.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class AudioActivity extends BaseMVPDBActivity<ActivityAudioBinding, VideoContract.View, VideoPresenter> implements VideoContract.View {

    private String mAudioUrl;//视频播放地址
    private String mTitle;//视频播放标题
    private String mCover;//封面
    private long mDurationTime;//音频时长
    private ObjectAnimator mAnimatorRotation;

    /**
     * @param context
     * @param videoUrl 播放地址
     * @param title    视频名称
     * @param duration 时长
     */
    public static void startActivity(Context context, String videoUrl, String title, long duration) {
        Intent intent = new Intent(context, AudioActivity.class);
        intent.putExtra(Config.KEY_AUDIO_URL, videoUrl);
        intent.putExtra(Config.KEY_TITLE, title);
        intent.putExtra(Config.KEY_SIZE, duration);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mAudioUrl = intent.getStringExtra(Config.KEY_AUDIO_URL);
        mAudioUrl = mAudioUrl.replace("//api", "/api");
        mTitle = intent.getStringExtra(Config.KEY_TITLE);
        binding.tvTitle.setText(mTitle);
        LogUtil.e(TAG+"mAudioUrl="+mAudioUrl);
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.ivPlayMusic.setOnClickListener(v -> {
            if (binding.detailPlayer.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING) {
                binding.detailPlayer.onVideoPause();
                binding.ivPlayMusic.setSelected(false);
                mAnimatorRotation.pause();
            } else if (binding.detailPlayer.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE) {
                binding.detailPlayer.onVideoResume();
                binding.ivPlayMusic.setSelected(true);
                mAnimatorRotation.resume();
            } else {
                binding.detailPlayer.startPlayLogic();
                binding.ivPlayMusic.setSelected(true);
                mAnimatorRotation.resume();
            }
        });
        binding.ivBack.setOnClickListener(v -> finish());
        initAnimation();
    }

    private void initAnimation() {
        mAnimatorRotation = ObjectAnimator.ofFloat(binding.ivMusic, "rotation", 0.5f, 360f);
        //旋转不停顿
        mAnimatorRotation.setInterpolator(new LinearInterpolator());
        //设置动画重复次数
        mAnimatorRotation.setRepeatCount(-1);
        //旋转时长
        mAnimatorRotation.setDuration(3000);
    }

    @Override
    protected void initData() {
        super.initData();
        //mVideoUrl = getUrl();
        //mVideoUrl = "http://img-qn.51miz.com/preview/sound/00/23/30/51miz-S233057-BC6DBC8F-thumb.mp3";
        Map<String, String> header = new HashMap<>();
        header.put(HttpUrlParams.SCOPE_TOKEN, Constant.scope_token);
        header.put("area_id", Constant.AREA_ID + "");
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();

        gsyVideoOption
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(mAudioUrl)
                .setMapHeadData(header)
                .setCacheWithPlay(false)
                .setVideoTitle(mTitle)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        mAnimatorRotation.start();
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        mAnimatorRotation.end();
                        binding.sbProgress.setProgress(100);
                        binding.ivPlayMusic.setSelected(false);
                        binding.tvCurrentTime.setText(CommonUtil.stringForTime((int) mDurationTime));
                    }

                    @Override
                    public void onClickSeekbar(String url, Object... objects) {
                        super.onClickSeekbar(url, objects);

                    }
                })
                .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {
                    if (mDurationTime == 0) mDurationTime = duration;
                    binding.tvDurationTime.setText(CommonUtil.stringForTime(duration));
                    binding.tvCurrentTime.setText(CommonUtil.stringForTime(currentPosition));

                    int sbProgress = (int) (currentPosition * 100.0 / duration);
                    binding.sbProgress.setProgress(sbProgress);

                    LogUtil.e(TAG + "progress1" + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + ",duration " + duration + ",sbProgress=" + sbProgress);
                })
                .build(binding.detailPlayer);

        binding.sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlay = binding.detailPlayer.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING;
                binding.detailPlayer.onVideoPause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long current = (long) (mDurationTime * (seekBar.getProgress() * 1.0 / 100));
                LogUtil.e(TAG + "onStopTrackingTouch=" + seekBar.getProgress() + ",mDurationTime=" + mDurationTime + ",current=" + current);
                binding.detailPlayer.setSeekOnStart(current);
                if (isPlay) {
                    binding.detailPlayer.startPlayLogic();
                } else {
                    binding.detailPlayer.onCompletion();
                }
                isPlay = false;
            }
        });
    }

    boolean isPlay = false;

    private String getUrl() {
        String url = "android.resource://" + getPackageName() + "/" + R.raw.test3;
        //注意，用ijk模式播放raw视频，这个必须打开
        GSYVideoManager.instance().enableRawPlay(getApplicationContext());
        ///exo raw 支持
        //String url =  RawResourceDataSource.buildRawResourceUri(R.raw.test).toString();
        return url;
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.detailPlayer.onVideoPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.detailPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放所有
        if (binding.detailPlayer != null) {
            binding.detailPlayer.setVideoAllCallBack(null);
            binding.detailPlayer.release();
        }
        if (mAnimatorRotation != null) {
            mAnimatorRotation = null;
        }
    }
}
