package com.zhiting.clouddisk.home.fragment;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.SimpleItemAnimator;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.DownLoadingAdapter;
import com.zhiting.clouddisk.adapter.DownloadFileCompleteAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.FragmentDownBinding;
import com.zhiting.clouddisk.dialog.DownDetailDialog;
import com.zhiting.clouddisk.dialog.DownFailListDialog;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.DownDetailActivity;
import com.zhiting.clouddisk.home.contract.UploadContract;
import com.zhiting.clouddisk.home.presenter.UploadPresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载列表
 */
public class DownLoadFragment extends BaseMVPDBFragment<FragmentDownBinding, UploadContract.View, UploadPresenter> implements UploadContract.View {
    private boolean isVisibleToUser;
    private CountDownTimer mCountDownTimer;//定时器
    private DownLoadingAdapter mDownloadingAdapter;//上传中
    private DownloadFileCompleteAdapter mFileCompleteAdapter;//上传完成
    private List<DownLoadFileBean> mCompleteList = new ArrayList<>();//上传完成列表
    private List<DownLoadFileBean> mDownloadingList = new ArrayList<>();//正在上传列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_down;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        initRecyclerView();
        initCountDownTimer();
    }

    private void initRecyclerView() {
        mFileCompleteAdapter = new DownloadFileCompleteAdapter();
        binding.rvUploaded.setAdapter(mFileCompleteAdapter);

        mDownloadingAdapter = new DownLoadingAdapter();
        binding.rvUploading.setAdapter(mDownloadingAdapter);

        //去掉item动画
        ((SimpleItemAnimator) binding.rvUploaded.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) binding.rvUploading.getItemAnimator()).setSupportsChangeAnimations(false);

        //正在完成
        mDownloadingAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DownLoadFileBean fileBean = mDownloadingAdapter.getItem(position);
            if (view.getId() == R.id.ivStatus) {
                switchFileStatus(fileBean);
            } else if (view.getId() == R.id.ivError || view.getId() == R.id.tvStatus) {
                showDownloadFailDialog(fileBean);
            }
        });
        //正在完成
        mFileCompleteAdapter.setOnItemClickListener((adapter, view, position) -> {
            DownLoadFileBean fileBean = mFileCompleteAdapter.getItem(position);
            String filePath = GonetUtil.dbPath + "/" + fileBean.getName();
            LogUtil.e("DownLoadFragment==filePath1=" + GsonConverter.getGson().toJson(fileBean));
            if (fileBean.getType().equals("dir")) {//文件夹
                LogUtil.e("DownLoadFragment==filePath2=" + filePath);
                if (!TextUtils.isEmpty(fileBean.getName())) {
                    LogUtil.e("DownLoadFragment==filePath3=" + filePath);
                    bundle.putString(Constant.PATH, filePath);
                    switchToActivity(DownDetailActivity.class, bundle);
                }
            } else {
                File file = new File(filePath);
                showOpenFileDialog(file);
            }
        });
    }

    private void showOpenFileDialog(File file) {
        DownDetailDialog downDetailDialog = DownDetailDialog.getInstance(file);
        downDetailDialog.setOperateListener((position, file1) -> FileTypeUtil.openFile(file1.getAbsolutePath()));
        downDetailDialog.show(this);
    }

    /**
     * 文件状态的处理
     *
     * @param fileBean
     */
    private void switchFileStatus(DownLoadFileBean fileBean) {
        //0未开始 1上传中 2暂停 4上传失败
        if (fileBean.getStatus() == 1) {
            GonetUtil.stopDownload(fileBean.getId());
        } else {
            GonetUtil.startDownload(fileBean.getId());
        }
        EventBus.getDefault().post(new UploadDownloadEvent());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (mCountDownTimer != null) {
            if (isVisibleToUser) {
                mCountDownTimer.start();
            } else {
                mCountDownTimer.cancel();
                mCountDownTimer.onFinish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCountDownTimer != null && isVisibleToUser)
            mCountDownTimer.start();
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
        mCountDownTimer.start();
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }

    private synchronized void loadData() {
        GonetUtil.getDownloadList(list -> {
            mCompleteList.clear();
            mDownloadingList.clear();
            if (CollectionUtil.isNotEmpty(list)) {
                for (DownLoadFileBean fileBean : list) {
                    //下载状态 0未开始 1下载中 2已暂停 3下载完成 4下载失败
                    if (fileBean.getStatus() == 3) {
                        mCompleteList.add(fileBean);
                    } else {
                        mDownloadingList.add(fileBean);
                    }
                }
            }
            //下载中的列表
            if (CollectionUtil.isNotEmpty(mDownloadingList)) {
                String format = UiUtil.getString(R.string.home_download_count);
                binding.tvUploadCount.setText(String.format(format, String.valueOf(mDownloadingList.size())));
                binding.tvUploadCount.setVisibility(View.VISIBLE);
                binding.rvUploading.setVisibility(View.VISIBLE);
                mDownloadingAdapter.setNewData(mDownloadingList);
            } else {
                binding.tvUploadCount.setVisibility(View.GONE);
                binding.rvUploading.setVisibility(View.GONE);
            }
            //下载完成的列表
            if (CollectionUtil.isNotEmpty(mCompleteList)) {
                String format = UiUtil.getString(R.string.home_downloaded_count);
                binding.tvCompleteCount.setText(String.format(format, String.valueOf(mCompleteList.size())));
                binding.tvCompleteCount.setVisibility(View.VISIBLE);
                binding.rvUploaded.setVisibility(View.VISIBLE);
                mFileCompleteAdapter.setNewData(mCompleteList);
            } else {
                binding.tvCompleteCount.setVisibility(View.GONE);
                binding.rvUploaded.setVisibility(View.GONE);
            }
            if (CollectionUtil.isEmpty(mDownloadingList) && CollectionUtil.isEmpty(mCompleteList)) {
                binding.empty.setVisibility(View.VISIBLE);
            } else {
                binding.empty.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 下载失败列表弹窗
     *
     * @param fileBean
     */
    private void showDownloadFailDialog(DownLoadFileBean fileBean) {
        GonetUtil.getDownloadDirList(fileBean.getId(), list -> {
            DownFailListDialog downFailListDialog = DownFailListDialog.getInstance(fileBean.getName(), list);
            downFailListDialog.show(getActivity());
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
            mCountDownTimer = null;
        }
    }
}
