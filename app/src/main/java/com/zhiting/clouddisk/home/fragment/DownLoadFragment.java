package com.zhiting.clouddisk.home.fragment;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SPUtils;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.DownLoadingAdapter;
import com.zhiting.clouddisk.adapter.DownloadFileCompleteAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.FragmentDownBinding;
import com.zhiting.clouddisk.dialog.DownFailListDialog;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.AudioActivity;
import com.zhiting.clouddisk.home.activity.DownDetailActivity;
import com.zhiting.clouddisk.home.activity.PictureCustomPreviewActivity;
import com.zhiting.clouddisk.home.activity.VideoActivity;
import com.zhiting.clouddisk.home.contract.UploadContract;
import com.zhiting.clouddisk.home.presenter.UploadPresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.mine.activity.TrafficTipActivity;
import com.zhiting.clouddisk.tbswebview.DownloadUtil;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.event.FileStatusEvent;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.NetworkUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.pictureselectorutil.PicSelectorUtils;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

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
    private static int mEventType;
    private static boolean isEvent;
    private boolean first = true;

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_down;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        initRecyclerView();
        initCountDownTimer();
        initListener();
        if (isEvent) {
            int textId = mEventType == 0 ? R.string.home_all_start_download : R.string.home_all_stop_download;
            binding.tvAllDown.setText(textId);
            mEventType = 0;
            isEvent = false;
        }
    }

    private void initListener() {
        binding.tvAllDown.setOnClickListener(v -> {
            String textDesc = binding.tvAllDown.getText().toString();
            if (textDesc.equals(UiUtil.getString(R.string.home_all_start_download))) {
                int type = NetworkUtil.getNetworkerStatus();
                boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
                int fileCount = GonetUtil.getUnderwayFileCount();
                if (type >= 2 && isOnlyWifi && fileCount > 0) {
                    switchToActivity(TrafficTipActivity.class);
                } else {
                    startAllDownTask();
                }
            } else {
                stopAllDownTask();
            }
        });

        binding.tvClearAll.setOnClickListener(v -> {
            GonetUtil.clearAllDownTaskRecord();
            mCompleteList.clear();
            mFileCompleteAdapter.setNewData(mCompleteList);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(FileStatusEvent event) {
        LogUtil.e("FileStatusEvent12=" + event.getType());
        isEvent = true;
        mEventType = event.getType();
        if (event.getType() == 0) {//0暂停 1开始
            stopAllDownTask();
        } else {
            startAllDownTask();
        }
    }

    /**
     * 暂停所有任务
     */
    private void stopAllDownTask() {
        LogUtil.e(TAG + "stopAllDownTask");
        GonetUtil.stopAllDownTask();
        if (binding != null && binding.tvAllDown != null)
            binding.tvAllDown.setText(R.string.home_all_start_download);
    }

    /**
     * 开始所有任务
     */
    private void startAllDownTask() {
        LogUtil.e(TAG + "startAllDownTask");
        GonetUtil.startAllDownTask();
        if (binding != null && binding.tvAllDown != null)
            binding.tvAllDown.setText(R.string.home_all_stop_download);
    }

    private void initRecyclerView() {
        mFileCompleteAdapter = new DownloadFileCompleteAdapter();
        binding.rvUploaded.setAdapter(mFileCompleteAdapter);
        mFileCompleteAdapter.setNewData(mCompleteList);

        mDownloadingAdapter = new DownLoadingAdapter();
        binding.rvUploading.setAdapter(mDownloadingAdapter);
        mDownloadingAdapter.setNewData(mDownloadingList);

        //正在下载
        mDownloadingAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DownLoadFileBean fileBean = mDownloadingAdapter.getItem(position);
            if (view.getId() == R.id.ivStatus) {
                switchFileStatus(fileBean);
            } else if (view.getId() == R.id.ivError || view.getId() == R.id.tvStatus) {
                showDownloadFailDialog(fileBean);
            } else if (view.getId() == R.id.tvDelete) {
                GonetUtil.deleteDownload(fileBean.getId());
                for (DownLoadFileBean bean : mDownloadingList) {
                    if (fileBean.getId() == bean.getId()) {
                        mDownloadingList.remove(fileBean);
                        mDownloadingAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        //正在完成
        mFileCompleteAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DownLoadFileBean fileBean = mFileCompleteAdapter.getItem(position);
            if (view.getId() == R.id.tvDelete) {
                GonetUtil.deleteDownload(fileBean.getId());
                for (DownLoadFileBean bean : mCompleteList) {
                    if (fileBean.getId() == bean.getId()) {
                        mCompleteList.remove(fileBean);
                        mFileCompleteAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (view.getId() == R.id.root) {
                String filePath = GonetUtil.dbPath + "/" + fileBean.getName();
                if (fileBean.getType().equals("dir")) {//文件夹
                    if (!TextUtils.isEmpty(fileBean.getName())) {
                        bundle.putString(Constant.PATH, filePath);
                        bundle.putInt(Constant.KEY_ID, fileBean.getId());
                        switchToActivity(DownDetailActivity.class, bundle);
                    }
                } else {
                    showOpenFileDialog(fileBean);
                }
            }
        });
    }

    private void showOpenFileDialog(DownLoadFileBean fileBean) {
        String filePath = GonetUtil.dbPath + "/" + fileBean.getName();
        int fileType = FileTypeUtil.fileType(fileBean.getName());
        if (fileType == 7) {//视频
            if (filePath.endsWith("3gp") || filePath.endsWith("mpg")) {
                PlayerFactory.setPlayManager(Exo2PlayerManager.class);
                CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            } else {
                PlayerFactory.setPlayManager(IjkPlayerManager.class);
                CacheFactory.setCacheManager(ProxyCacheManager.class);
            }
            VideoActivity.startActivity(getActivity(), filePath, filePath, fileBean.getName());
        } else if (fileType == 5) {//图片
            preViewImage(fileBean);
        } else if (fileType == 6) {//音频
            PlayerFactory.setPlayManager(Exo2PlayerManager.class);
            CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            AudioActivity.startActivity(getActivity(), filePath, fileBean.getName(), fileBean.getSize() / 1000);
        } else if (fileType == 1 || fileType == 2 || fileType == 3 || fileType == 4 || fileType == 8 || fileType == 9) {
            DownloadUtil.get().startDownload((BaseActivity) getActivity(), filePath, fileBean.getName());
        }
    }

    private void preViewImage(DownLoadFileBean fileBean) {
        List<LocalMedia> images = new ArrayList<>();
        List<DownLoadFileBean> fileBeans = mFileCompleteAdapter.getData();

        if (CollectionUtil.isNotEmpty(fileBeans)) {
            for (DownLoadFileBean bean : fileBeans) {
                int type = FileTypeUtil.fileType(bean.getName());
                if (type == 5) {
                    String imageUrl = bean.getDownload_to_path() + "/" + bean.getName();
                    LocalMedia media = new LocalMedia();
                    media.setPath(imageUrl);
                    media.setOriginalPath(imageUrl);
                    media.setRealPath(imageUrl);
                    media.setFileName(bean.getName());
                    images.add(media);
                }
            }
            int position = 0;
            if (CollectionUtil.isNotEmpty(images)) {
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).getFileName().equals(fileBean.getName())) {
                        position = i;
                        break;
                    }
                }
            }
            PicSelectorUtils.openPreviewCustomImages(getActivity(), position, images, PictureCustomPreviewActivity.class);
        }
    }

    /**
     * 文件状态的处理
     *
     * @param fileBean //0等待 1上传中 2暂停 4上传失败
     */
    private void switchFileStatus(DownLoadFileBean fileBean) {
        UiUtil.starThread(() -> {
            if (fileBean.getStatus() == 0 || fileBean.getStatus() == 1) {
                GonetUtil.stopDownload(fileBean.getId());
            } else {
                GonetUtil.startDownload(fileBean.getId());
            }
            UiUtil.runInMainThread(() -> {
                EventBus.getDefault().post(new UploadDownloadEvent());
                checkAllStatus();
            });
        });
    }

    private void checkAllStatus() {
        if (CollectionUtil.isNotEmpty(mDownloadingList)) {
            int downloadingCount = 0;
            for (DownLoadFileBean fileBean : mDownloadingList) {
                if (fileBean.getStatus() == 0 || fileBean.getStatus() == 1) {
                    downloadingCount++;
                }
            }
            if (downloadingCount > 0) {
                binding.tvAllDown.setText(R.string.home_all_stop_download);
            } else {
                binding.tvAllDown.setText(R.string.home_all_start_download);
            }
        }
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
            if (CollectionUtil.isNotEmpty(list)) {
                List<DownLoadFileBean> tempCompleteList = new ArrayList<>();
                List<DownLoadFileBean> tempDownloadingList = new ArrayList<>();
                for (DownLoadFileBean fileBean : list) {
                    //下载状态 0未开始 1下载中 2已暂停 3下载完成 4下载失败
                    if (fileBean.getStatus() == 3) {
                        tempCompleteList.add(fileBean);
                    } else {
                        tempDownloadingList.add(fileBean);
                    }
                }
                if (mDownloadingList.size() > 0 && (mDownloadingList.size() == tempDownloadingList.size())) {
                    for (int position = 0; position < tempDownloadingList.size(); position++) {
                        mDownloadingAdapter.notifyItemChanged(position, tempDownloadingList.get(position));
                        mDownloadingList.get(position).setStatus(tempDownloadingList.get(position).getStatus());
                    }
                } else if ((mDownloadingList.size() != tempDownloadingList.size()) || (mCompleteList.size() != tempCompleteList.size())) {
                    mCompleteList.clear();
                    mDownloadingList.clear();
                    mCompleteList.addAll(tempCompleteList);
                    mDownloadingList.addAll(tempDownloadingList);
                    mDownloadingAdapter.notifyDataSetChanged();
                    mFileCompleteAdapter.notifyDataSetChanged();
                }
                Collections.sort(mCompleteList, (bean1, bean2) -> -Long.compare(bean1.getCreate_time(), bean2.getCreate_time()));
            }
            //下载中的列表
            if (CollectionUtil.isNotEmpty(mDownloadingList)) {
                String format = UiUtil.getString(R.string.home_download_count);
                binding.tvUploadCount.setText(String.format(format, mDownloadingList.size()));
                binding.tvUploadCount.setVisibility(View.VISIBLE);
                binding.rvUploading.setVisibility(View.VISIBLE);
                binding.tvAllDown.setVisibility(View.VISIBLE);
            } else {
                binding.tvUploadCount.setVisibility(View.GONE);
                binding.rvUploading.setVisibility(View.GONE);
                binding.tvAllDown.setVisibility(View.GONE);
            }
            //下载完成的列表
            if (CollectionUtil.isNotEmpty(mCompleteList)) {
                setCompleteCount();
                binding.rvUploaded.setVisibility(View.VISIBLE);
            } else {
                binding.tvClearAll.setVisibility(View.GONE);
                binding.rvUploaded.setVisibility(View.GONE);
                binding.tvCompleteCount.setVisibility(View.GONE);
            }
            if (CollectionUtil.isEmpty(mDownloadingList) && CollectionUtil.isEmpty(mCompleteList)) {
                binding.empty.setVisibility(View.VISIBLE);
            } else {
                binding.empty.setVisibility(View.GONE);
            }
            checkAllStatus();
        });
    }

    private void setCompleteCount() {
        if (mCompleteList.size() == 0) {
            binding.tvClearAll.setVisibility(View.GONE);
            binding.tvCompleteCount.setVisibility(View.GONE);
        } else {
            String format = UiUtil.getString(R.string.home_downloaded_count);
            binding.tvCompleteCount.setText(String.format(format, mCompleteList.size()));
            binding.tvCompleteCount.setVisibility(View.VISIBLE);
            binding.tvClearAll.setVisibility(View.VISIBLE);
        }
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
