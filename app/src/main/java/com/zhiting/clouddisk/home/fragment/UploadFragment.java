package com.zhiting.clouddisk.home.fragment;

import android.os.CountDownTimer;
import android.view.View;

import androidx.recyclerview.widget.SimpleItemAnimator;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.UploadFileCompleteAdapter;
import com.zhiting.clouddisk.adapter.UploadingAdapter;
import com.zhiting.clouddisk.databinding.FragmentUploadBinding;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.contract.UploadContract;
import com.zhiting.clouddisk.home.presenter.UploadPresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传列表
 */
public class UploadFragment extends BaseMVPDBFragment<FragmentUploadBinding, UploadContract.View, UploadPresenter> implements UploadContract.View {

    private boolean isVisibleToUser;
    private CountDownTimer mCountDownTimer;//定时器
    private UploadingAdapter mUploadingAdapter;//上传中
    private UploadFileCompleteAdapter mFileCompleteAdapter;//上传完成
    private List<UploadFileBean> mCompleteList = new ArrayList<>();//上传完成列表 3已完成
    private List<UploadFileBean> mUploadingList = new ArrayList<>();//上传状态 0未开始 1上传中 2暂停 4上传失败 5生成临时文件中

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_upload;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        initCountDownTimer();
        initRecyclerView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        controlDownTimer(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCountDownTimer != null && isVisibleToUser) {
            controlDownTimer(true);
        }
    }

    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                loadData();
            }

            @Override
            public void onFinish() {
            }
        };
        controlDownTimer(true);
    }

    /**
     * 初始化列表控件
     */
    private void initRecyclerView() {
        //上传完成
        mFileCompleteAdapter = new UploadFileCompleteAdapter();
        binding.rvUploaded.setAdapter(mFileCompleteAdapter);
        //正在上传
        mUploadingAdapter = new UploadingAdapter();
        binding.rvUploading.setAdapter(mUploadingAdapter);

        //去掉item动画
        ((SimpleItemAnimator) binding.rvUploaded.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) binding.rvUploading.getItemAnimator()).setSupportsChangeAnimations(false);

        mUploadingAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            UploadFileBean fileBean = mUploadingAdapter.getItem(position);
            if (view1.getId() == R.id.ivStatus) {
                switchFileStatus(fileBean);
            }
        });
    }

    /**
     * 文件状态的处理
     *
     * @param fileBean
     */
    private synchronized void switchFileStatus(UploadFileBean fileBean) {
        //status 0未开始 1上传中 2暂停 4上传失败
        UiUtil.starThread(() -> {
            if (fileBean.getStatus() == 0 || fileBean.getStatus() == 1) {
                GonetUtil.stopUpload(fileBean.getId());
            } else if (fileBean.getStatus() == 2 || fileBean.getStatus() == 4) {
                GonetUtil.startUpload(fileBean.getId());
            }
            EventBus.getDefault().post(new UploadDownloadEvent());
        });
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }

    /**
     * 获取数据
     */
    private synchronized void loadData() {
        GonetUtil.getUploadList(list -> {
            mCompleteList.clear();
            mUploadingList.clear();
            if (CollectionUtil.isNotEmpty(list)) {
                //上传状态 0未开始 1上传中 2暂停 3已完成 4上传失败 5生成临时文件中
                for (UploadFileBean fileBean : list) {
                    if (fileBean.getStatus() == 3) {
                        mCompleteList.add(fileBean);
                    } else {
                        mUploadingList.add(fileBean);
                    }
                }
            }
            //上传中的列表
            if (CollectionUtil.isNotEmpty(mUploadingList)) {
                String format = UiUtil.getString(R.string.home_uploading);
                binding.tvUploadCount.setText(String.format(format, mUploadingList.size() + ""));
                binding.tvUploadCount.setVisibility(View.VISIBLE);
                binding.rvUploading.setVisibility(View.VISIBLE);
                mUploadingAdapter.setNewData(mUploadingList);
            } else if (binding.tvUploadCount != null && binding.rvUploading != null) {
                binding.tvUploadCount.setVisibility(View.GONE);
                binding.rvUploading.setVisibility(View.GONE);
            }
            //已经完成的列表
            if (CollectionUtil.isNotEmpty(mCompleteList)) {
                String format = UiUtil.getString(R.string.home_uploaded);
                binding.tvCompleteCount.setText(String.format(format, mCompleteList.size() + ""));
                binding.tvCompleteCount.setVisibility(View.VISIBLE);
                binding.rvUploaded.setVisibility(View.VISIBLE);
                mFileCompleteAdapter.setNewData(mCompleteList);
            } else if (binding.tvCompleteCount != null && binding.rvUploaded != null) {
                binding.tvCompleteCount.setVisibility(View.GONE);
                binding.rvUploaded.setVisibility(View.GONE);
            }
            if (CollectionUtil.isEmpty(mUploadingList) && CollectionUtil.isEmpty(mCompleteList)) {
                binding.empty.setVisibility(View.VISIBLE);
            } else {
                binding.empty.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCountDownTimer != null) {
            controlDownTimer(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            controlDownTimer(false);
            mCountDownTimer = null;
        }
    }

    /**
     * 控制定时任务
     *
     * @param isStart
     */
    public void controlDownTimer(boolean isStart) {
        if (mCountDownTimer != null) {
            if (isStart) {
                mCountDownTimer.start();
            } else {
                mCountDownTimer.cancel();
                mCountDownTimer.onFinish();
            }
        }
    }
}
