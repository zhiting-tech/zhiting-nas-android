package com.zhiting.clouddisk.home.fragment;

import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;

import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SPUtils;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.UploadFileCompleteAdapter;
import com.zhiting.clouddisk.adapter.UploadingAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.databinding.FragmentUploadBinding;
import com.zhiting.clouddisk.entity.home.UploadErrorBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.AudioActivity;
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
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.NetworkUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;
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
 * 上传列表
 */
public class UploadFragment extends BaseMVPDBFragment<FragmentUploadBinding, UploadContract.View, UploadPresenter> implements UploadContract.View {

    private boolean isVisibleToUser;
    private CountDownTimer mCountDownTimer;//定时器
    private UploadingAdapter mUploadingAdapter;//上传中
    private UploadFileCompleteAdapter mFileCompleteAdapter;//上传完成
    private List<UploadFileBean> mCompleteList = new ArrayList<>();//上传完成列表 3已完成
    private List<UploadFileBean> mUploadingList = new ArrayList<>();//上传状态 0未开始 1上传中 2暂停 4上传失败 5生成临时文件中
    private static int mEventType;
    private static boolean isEvent;
    private boolean isShowError = true;

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_upload;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        initCountDownTimer();
        initRecyclerView();
        initListener();
        if (isEvent) {
            int textId = mEventType == 0 ? R.string.home_all_start_upload : R.string.home_all_stop_upload;
            binding.tvAllUpload.setText(textId);
            mEventType = 0;
            isEvent = false;
        }
    }

    private void initListener() {
        //全部开始、暂停
        binding.tvAllUpload.setOnClickListener(v -> {
            String textDesc = binding.tvAllUpload.getText().toString();
            if (textDesc.equals(UiUtil.getString(R.string.home_all_start_upload))) {
                int type = NetworkUtil.getNetworkerStatus();
                boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
                int fileCount = GonetUtil.getUnderwayFileCount();
                if (type >= 2 && isOnlyWifi && fileCount > 0) {
                    switchToActivity(TrafficTipActivity.class);
                } else {
                    startAllUploadTask();
                }
            } else {
                stopAllUploadTask();
            }
        });

        //清除所有上传记录
        binding.tvClearAll.setOnClickListener(v -> {
            GonetUtil.clearAllUploadTaskRecord(0);
            mCompleteList.clear();
            mFileCompleteAdapter.setNewData(mCompleteList);
        });
    }

    /**
     * 暂停所有任务
     */
    private void stopAllUploadTask() {
        GonetUtil.stopAllUploadTask(0);
        if (binding != null && binding.tvAllUpload != null)
            binding.tvAllUpload.setText(R.string.home_all_start_upload);
    }

    /**
     * 开始所有任务
     */
    private void startAllUploadTask() {
        GonetUtil.startAllUploadTask(0);
        if (binding != null && binding.tvAllUpload != null)
            binding.tvAllUpload.setText(R.string.home_all_stop_upload);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(FileStatusEvent event) {
        LogUtil.e("FileStatusEvent11=" + event.getType());
        isEvent = true;
        mEventType = event.getType();
        if (event.getType() == 0) {//0暂停 1开始
            stopAllUploadTask();
        } else {
            startAllUploadTask();
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

        mUploadingAdapter.setNewData(mUploadingList);
        mFileCompleteAdapter.setNewData(mCompleteList);

        //去掉item动画
        ((SimpleItemAnimator) binding.rvUploaded.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) binding.rvUploading.getItemAnimator()).setSupportsChangeAnimations(false);

        mUploadingAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            UploadFileBean fileBean = mUploadingAdapter.getItem(position);
            if (view.getId() == R.id.ivStatus) {
                switchFileStatus(fileBean);
            } else if (view.getId() == R.id.tvDelete) {
                GonetUtil.deleteUpload(fileBean.getId());
                for (UploadFileBean bean : mUploadingList) {
                    if (fileBean.getId() == bean.getId()) {
                        mUploadingList.remove(fileBean);
                        mUploadingAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        mFileCompleteAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            UploadFileBean fileBean = mFileCompleteAdapter.getItem(position);
            if (view.getId() == R.id.root) {
                showOpenFileDialog(fileBean);
            } else if (view.getId() == R.id.tvDelete) {
                GonetUtil.deleteUpload(fileBean.getId());
                for (UploadFileBean bean : mCompleteList) {
                    if (fileBean.getId() == bean.getId()) {
                        mCompleteList.remove(fileBean);
                        mFileCompleteAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    private void showOpenFileDialog(UploadFileBean fileBean) {
        String filePath = HttpConfig.baseTestUrl + "/api" + HttpConfig.downLoadFileUrl1 + fileBean.getPreview_url();
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

    private void preViewImage(UploadFileBean fileBean) {
        List<LocalMedia> images = new ArrayList<>();
        List<UploadFileBean> fileBeans = mFileCompleteAdapter.getData();
        String baseUrl = HttpConfig.baseTestUrl + "/api" + HttpConfig.downLoadFileUrl1;
        baseUrl = baseUrl.replace("//api", "/api");

        if (CollectionUtil.isNotEmpty(fileBeans)) {
            for (UploadFileBean bean : fileBeans) {
                int type = FileTypeUtil.fileType(bean.getName());
                if (type == 5) {
                    String imageUrl = baseUrl + bean.getPreview_url();
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
            LogUtil.e("preViewImage==" + GsonConverter.getGson().toJson(images));
        }
    }

    /**
     * 文件状态的处理
     *
     * @param fileBean
     */
    private synchronized void switchFileStatus(UploadFileBean fileBean) {
        //status 0等待 1上传中 2暂停 4上传失败
        UiUtil.starThread(() -> {
            if (fileBean.getStatus() == 0 || fileBean.getStatus() == 1) {
                GonetUtil.stopUpload(fileBean.getId());
            } else if (fileBean.getStatus() == 2 || fileBean.getStatus() == 4) {
                GonetUtil.startUpload(fileBean.getId());
            }
            UiUtil.runInMainThread(() -> {
                EventBus.getDefault().post(new UploadDownloadEvent());
                checkAllStatus();
            });
        });
    }

    private void checkAllStatus() {
        if (CollectionUtil.isNotEmpty(mUploadingList)) {
            int uploadingCount = 0;
            for (UploadFileBean fileBean : mUploadingList) {
                if (fileBean.getStatus() == 0 || fileBean.getStatus() == 1) {
                    uploadingCount++;
                }
            }
            if (uploadingCount > 0) {
                binding.tvAllUpload.setText(R.string.home_all_stop_upload);
            } else {
                binding.tvAllUpload.setText(R.string.home_all_start_upload);
            }
        }
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }

    /**
     * 获取数据
     */
    private synchronized void loadData() {
        //错误监听
        GonetUtil.setOnUploadListener(filesBean -> {
            if (isShowError) {
                isShowError = false;
                ToastUtil.show(filesBean.getReason());
            }
        });

        GonetUtil.getUploadList(list -> {
            if (CollectionUtil.isNotEmpty(list)) {
                List<UploadFileBean> tempCompleteList = new ArrayList<>();
                List<UploadFileBean> tempUploadingList = new ArrayList<>();
                for (UploadFileBean fileBean : list) {
                    if (fileBean.getStatus() == 3) { //上传状态 0未开始 1上传中 2暂停 3已完成 4上传失败 5生成临时文件中
                        tempCompleteList.add(fileBean);
                    } else {
                        tempUploadingList.add(fileBean);
                    }
                }
                if (tempUploadingList.size() > 0 && (mUploadingList.size() == tempUploadingList.size())) {
                    for (int position = 0; position < tempUploadingList.size(); position++) {
                        mUploadingAdapter.notifyItemChanged(position, tempUploadingList.get(position));
                        mUploadingList.get(position).setStatus(tempUploadingList.get(position).getStatus());
                    }
                } else if ((mUploadingList.size() != tempUploadingList.size()) || (mCompleteList.size() != tempCompleteList.size())) {
                    mCompleteList.clear();
                    mUploadingList.clear();
                    mCompleteList.addAll(tempCompleteList);
                    mUploadingList.addAll(tempUploadingList);
                    mUploadingAdapter.notifyDataSetChanged();
                    mFileCompleteAdapter.notifyDataSetChanged();
                }
                Collections.sort(mCompleteList, (bean1, bean2) -> -Long.compare(bean1.getCreate_time(), bean2.getCreate_time()));
            }

            //上传中的列表
            if (CollectionUtil.isNotEmpty(mUploadingList)) {
                String format = UiUtil.getString(R.string.home_uploading);
                binding.tvUploadCount.setText(String.format(format, mUploadingList.size()));
                binding.tvUploadCount.setVisibility(View.VISIBLE);
                binding.rvUploading.setVisibility(View.VISIBLE);
                binding.tvAllUpload.setVisibility(View.VISIBLE);
            } else if (binding.tvUploadCount != null && binding.rvUploading != null) {
                binding.tvUploadCount.setVisibility(View.GONE);
                binding.rvUploading.setVisibility(View.GONE);
                binding.tvAllUpload.setVisibility(View.GONE);
            }

            //已经完成的列表
            if (CollectionUtil.isNotEmpty(mCompleteList)) {
                setCompleteCount();
                binding.rvUploaded.setVisibility(View.VISIBLE);
            } else if (binding.tvCompleteCount != null && binding.rvUploaded != null) {
                binding.rvUploaded.setVisibility(View.GONE);
                binding.tvClearAll.setVisibility(View.GONE);
                binding.tvCompleteCount.setVisibility(View.GONE);
            }
            //设置空页面
            if (CollectionUtil.isEmpty(mUploadingList) && CollectionUtil.isEmpty(mCompleteList)) {
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
            String format = UiUtil.getString(R.string.home_uploaded);
            binding.tvCompleteCount.setText(String.format(format, mCompleteList.size()));
            binding.tvCompleteCount.setVisibility(View.VISIBLE);
            binding.tvClearAll.setVisibility(View.VISIBLE);
        }
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
        GonetUtil.listener = null;
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
