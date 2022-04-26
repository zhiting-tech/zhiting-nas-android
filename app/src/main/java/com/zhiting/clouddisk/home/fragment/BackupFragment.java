package com.zhiting.clouddisk.home.fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SPUtils;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.BackupAdapter;
import com.zhiting.clouddisk.adapter.BackupRecordAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.FragmentBackupBinding;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.TrafficTipsDialog;
import com.zhiting.clouddisk.entity.BackupFilesBean;
import com.zhiting.clouddisk.entity.BackupSettingBean;
import com.zhiting.clouddisk.entity.home.BackupBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.AudioActivity;
import com.zhiting.clouddisk.home.activity.PictureCustomPreviewActivity;
import com.zhiting.clouddisk.home.activity.VideoActivity;
import com.zhiting.clouddisk.home.contract.BackupContract;
import com.zhiting.clouddisk.home.presenter.BackupPresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.mine.activity.TrafficTipActivity;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.clouddisk.tbswebview.DownloadUtil;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.event.FileStatusEvent;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.NetworkUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.StringUtil;
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
 * 备份列表
 */
public class BackupFragment extends BaseMVPDBFragment<FragmentBackupBinding, BackupContract.View, BackupPresenter> implements BackupContract.View {

    private boolean isVisibleToUser;
    private CountDownTimer mCountDownTimer;//定时器
    private BackupAdapter mBackupFailAdapter; // 备份失败
    private BackupAdapter mBackupAdapter; // 正在进行；
    private BackupRecordAdapter mBackupRecordAdapter; // 备份记录
    private List<UploadFileBean> mBackupFailList = new ArrayList<>(); // 4上传失败
    private List<UploadFileBean> mBackupList = new ArrayList<>(); // 上传状态 |0未开始|1上传中|2已暂停
    private List<UploadFileBean> mRecordList = new ArrayList<>();  // 3已完成
    private static int mEventType;
    private static boolean isEvent;
    private boolean isShowError = true;

    private final int BACKUP_LIST = 0X1;
    private final int BACKUP_FINISH_LIST = 0X2;
    private final int BACKUP_SUCCESS = 0X3;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BACKUP_LIST:
                    setBackupOnData(msg);
                    break;
                case BACKUP_FINISH_LIST:
                    setBackupFinishData(msg);
                    break;

                case BACKUP_SUCCESS:
                    UploadFileBean uploadFileBean = (UploadFileBean) msg.obj;
                    mRecordList.add(uploadFileBean);
                    mBackupRecordAdapter.notifyItemInserted(mRecordList.size()-1);
                    if (binding.rvBackupRecord.getVisibility() != View.VISIBLE) {
                        binding.llRecordTop.setVisibility(View.VISIBLE);
                        binding.rvBackupRecord.setVisibility(View.VISIBLE);
                    }
                    binding.tvRecordCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.home_backup_record), mRecordList.size()));
                    break;
            }
        }
    };

    /**
     * 正在进行
     * @param msg
     */
    private void setBackupOnData(Message msg) {
        BackupFilesBean backupFilesBean = (BackupFilesBean) msg.obj;
        if (backupFilesBean != null) {
            List<UploadFileBean> UploadOnFailList = backupFilesBean.getUploadOnFailList();
            List<UploadFileBean> UploadOnGoingList = backupFilesBean.getUploadOnGoingList();
            // 失败
            if (CollectionUtil.isNotEmpty(UploadOnFailList)) {

                if (mBackupFailList.size() == UploadOnFailList.size()) {
                    for (int position = 0; position < UploadOnFailList.size(); position++) {
                        mBackupFailAdapter.notifyItemChanged(position, UploadOnFailList.get(position));
                    }
                } else {
                    binding.tvBackupFailCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.home_backup_fail), UploadOnFailList.size()));
                    mBackupFailList.clear();
                    mBackupFailList.addAll(UploadOnFailList);
                    mBackupFailAdapter.notifyDataSetChanged();
                }
            } else {
                mBackupFailList.clear();
                mBackupFailAdapter.notifyDataSetChanged();
            }
            binding.llFailTop.setVisibility(CollectionUtil.isNotEmpty(UploadOnFailList) ? View.VISIBLE : View.GONE);

            binding.rvBackupFail.setVisibility(CollectionUtil.isNotEmpty(UploadOnFailList) ? View.VISIBLE : View.GONE);

            // 正在进行
            if (CollectionUtil.isNotEmpty(UploadOnGoingList)) {
                for (int i=0; i<mBackupList.size(); i++) {
                    UploadFileBean backupFile = mBackupList.get(i);
                    if (checkUploadingDataHasFinish(UploadOnGoingList, backupFile)){
                        mBackupList.remove(backupFile);
                        mBackupAdapter.notifyItemRemoved(i);
                    }
                }
                for (UploadFileBean fileBean : UploadOnGoingList) {
                    notifyUploadingDataChange(fileBean);
                }
            } else {
                mBackupList.clear();
                mBackupAdapter.notifyDataSetChanged();
            }
            binding.tvAllPause.setText(GonetUtil.getBackupFileCount()>0 ? R.string.home_all_stop_download : R.string.home_all_start_upload);
            binding.tvBackupOn.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.home_backup_on), backupFilesBean.getOnGoingNum()));
            binding.llOnTop.setVisibility(CollectionUtil.isNotEmpty(UploadOnGoingList) ? View.VISIBLE : View.GONE);

            binding.rvBackupOn.setVisibility(CollectionUtil.isNotEmpty(UploadOnGoingList) ? View.VISIBLE : View.GONE);

        }
        setEmptyView();
    }

    /**
     * 完成列表
     * @param msg
     */
    private void setBackupFinishData(Message msg) {
        BackupFilesBean backupFinishFilesBean = (BackupFilesBean) msg.obj;
        if (backupFinishFilesBean != null) {
            List<UploadFileBean> uploadOnSuccessList = backupFinishFilesBean.getUploadOnSuccessList();
            if (CollectionUtil.isNotEmpty(uploadOnSuccessList)) {
                binding.tvRecordCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.home_backup_record), uploadOnSuccessList.size()));
                mRecordList.clear();
                mRecordList.addAll(uploadOnSuccessList);
            }else {
                mRecordList.clear();
            }
            binding.llRecordTop.setVisibility(CollectionUtil.isNotEmpty(uploadOnSuccessList) ? View.VISIBLE : View.GONE);
            binding.rvBackupRecord.setVisibility(CollectionUtil.isNotEmpty(uploadOnSuccessList) ? View.VISIBLE : View.GONE);
            mBackupRecordAdapter.notifyDataSetChanged();
        }
        setEmptyView();
    }

    /**
     * 设置是否需要空数据
     */
    private void setEmptyView(){
        boolean empty = CollectionUtil.isEmpty(mBackupFailList) && CollectionUtil.isEmpty(mBackupList) && CollectionUtil.isEmpty(mRecordList);
        setEmpty(empty);
    }

    /**
     * 刷新数据
     * @param uploadFileBean
     */
    private void notifyUploadingDataChange(UploadFileBean uploadFileBean){
        if (!checkUploadingDataHasContain(uploadFileBean)) {
            mBackupList.add(mBackupList.size(), uploadFileBean);
            mBackupAdapter.notifyItemInserted(uploadFileBean.getStatus() == 1 ? 0 : mBackupList.size()-1);
        }
    }

    /**
     * 检测正在备份列表是否已存在
     * @param uploadFileBean
     * @return
     */
    private boolean checkUploadingDataHasContain(UploadFileBean uploadFileBean) {
        for (int i=0; i<mBackupList.size(); i++) {
            UploadFileBean uploadingBean = mBackupList.get(i);
            if (uploadingBean.getId() == uploadFileBean.getId()){
                mBackupList.set(i, uploadFileBean);
                mBackupAdapter.notifyItemChanged(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否在正在上传列表中
     * @param uploadingList
     * @param uploadFileBean
     * @return
     */
    private boolean checkUploadingDataHasFinish(List<UploadFileBean> uploadingList, UploadFileBean uploadFileBean){
        for (UploadFileBean uploading : uploadingList) {
            if (uploading.getId() == uploadFileBean.getId()) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_backup;
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }


    @Override
    protected void initUI(View view) {
        super.initUI(view);
        initCountDownTimer();
        initRvBackupFail();
        initRvBackup();
        initRvBackupRecord();
        initListener();
        if (isEvent) {
            int textId = mEventType == 0 ? R.string.home_all_start_upload : R.string.home_all_stop_upload;
            binding.tvAllPause.setText(textId);
            mEventType = 0;
            isEvent = false;
        }
        /**
         * 完成列表
         */
        GonetUtil.getBackupSuccessLis(new GonetUtil.OnBackupSuccessListListener() {
            @Override
            public void onSuccessList(BackupFilesBean backupFilesBean) {
                Message message = new Message();
                message.what = BACKUP_FINISH_LIST;
                message.obj = backupFilesBean;
                mHandler.sendMessage(message);
            }
        });
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

        /**
         * 进行列表
         */
        GonetUtil.getUploadBackupList(new GonetUtil.OnGetBackupFilesListener() {
            @Override
            public void onCallBack(BackupFilesBean backupFilesBean) {
                Message message = new Message();
                message.what = BACKUP_LIST;
                message.obj = backupFilesBean;
                mHandler.sendMessage(message);
            }
        });



    }

    /**
     * 没有数据
     *
     * @param visible
     */
    private void setEmpty(boolean visible) {
        binding.empty.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.llData.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    /**
     * 备份失败列表
     */
    private void initRvBackupFail() {
        mBackupFailAdapter = new BackupAdapter();
        binding.rvBackupFail.setLayoutManager(new LinearLayoutManager(mContext));
        ((SimpleItemAnimator) binding.rvBackupFail.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvBackupFail.setAdapter(mBackupFailAdapter);
        mBackupFailAdapter.setNewData(mBackupFailList);
        mBackupFailAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UploadFileBean backupBean = mBackupFailAdapter.getItem(position);
                if (view.getId() == R.id.ivStatus) {
                    switchFileStatus(backupBean);
                } else {
                    GonetUtil.deleteUpload(backupBean.getId());
                    for (UploadFileBean bean : mBackupFailList) {
                        if (backupBean.getId() == bean.getId()) {
                            mBackupFailList.remove(backupBean);
                            mBackupFailAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 备份正在进行列表
     */
    private void initRvBackup() {
        mBackupAdapter = new BackupAdapter();
        binding.rvBackupOn.setLayoutManager(new LinearLayoutManager(mContext));
//        ((SimpleItemAnimator) binding.rvBackupOn.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvBackupOn.setItemAnimator(null);
        binding.rvBackupOn.setAdapter(mBackupAdapter);
        mBackupAdapter.setNewData(mBackupList);
        mBackupAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UploadFileBean backupBean = mBackupAdapter.getItem(position);
                int viewId = view.getId();
                if (viewId == R.id.ivStatus) {
                    switchFileStatus(backupBean);
                } else {
                    GonetUtil.deleteUpload(backupBean.getId());
                    for (UploadFileBean bean : mBackupList) {
                        if (backupBean.getId() == bean.getId()) {
                            mBackupList.remove(backupBean);
                            mBackupAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 备份记录列表
     */
    private void initRvBackupRecord() {
        mBackupRecordAdapter = new BackupRecordAdapter();
        mBackupRecordAdapter.setHasStableIds(true);
        binding.rvBackupRecord.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvBackupRecord.setAdapter(mBackupRecordAdapter);
        mBackupRecordAdapter.setNewData(mRecordList);
        mBackupRecordAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UploadFileBean backupBean = mBackupRecordAdapter.getItem(position);
                int viewId = view.getId();
                if (viewId == R.id.root) {
                    showOpenFileDialog(backupBean);
                } else if (viewId == R.id.tvDelete) {
                    GonetUtil.deleteUpload(backupBean.getId());
                    for (UploadFileBean bean : mRecordList) {
                        if (backupBean.getId() == bean.getId()) {
                            mRecordList.remove(backupBean);
                            mBackupRecordAdapter.notifyDataSetChanged();
                            binding.tvRecordCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.home_backup_record), mRecordList.size()));
                            if (CollectionUtil.isEmpty(mRecordList)) {
                                binding.llRecordTop.setVisibility(View.GONE);
                                binding.rvBackupRecord.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void initListener() {
        binding.tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GonetUtil.allFailRetry();
            }
        });
        //全部开始、暂停
        binding.tvAllPause.setOnClickListener(v -> {
            String textDesc = binding.tvAllPause.getText().toString();
            if (textDesc.equals(UiUtil.getString(R.string.home_all_start_upload))) {
                int type = NetworkUtil.getNetworkerStatus();
                boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
                if (type >= 2 && isOnlyWifi) {
                    showTrafficTipsDialog();
                } else {
                    startAllUploadTask();
                }

            } else {
                stopAllUploadTask();
            }
        });

        //清除所有上传记录
        binding.tvClear.setOnClickListener(v -> {
            showRemoveDialog();
        });

        /**
         * 成功数据监听
         */
        GonetUtil.setBackupFileListener(new GonetUtil.OnBackupFileListener() {
            @Override
            public void onSuccess(UploadFileBean uploadFileBean) {
                Message message = new Message();
                message.what = BACKUP_SUCCESS;
                message.obj = uploadFileBean;
                mHandler.sendMessage(message);
            }
        });
    }

    /**
     * 删除确认弹窗
     */
    private void showRemoveDialog(){
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(UiUtil.getString(R.string.home_remove_backup_tip), UiUtil.getString(R.string.home_remove_backup_content),
              "", R.color.color_ff0000, "", UiUtil.getString(R.string.common_confirm));
        centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                GonetUtil.clearAllUploadTaskRecord(1);
                mRecordList.clear();
                mBackupRecordAdapter.setNewData(mRecordList);
                centerAlertDialog.dismiss();
            }
        });
        centerAlertDialog.show(this);
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
        List<UploadFileBean> fileBeans = mBackupRecordAdapter.getData();
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
     * 暂停所有任务
     */
    private void stopAllUploadTask() {
        GonetUtil.stopAllUploadTask(1);
        if (binding != null && binding.tvAllPause != null)
            binding.tvAllPause.setText(R.string.home_all_start_upload);
    }

    /**
     * 开始所有任务
     */
    private void startAllUploadTask() {
        GonetUtil.startAllUploadTask(1);
        if (binding != null && binding.tvAllPause != null)
            binding.tvAllPause.setText(R.string.home_all_stop_upload);
    }

    /**
     * 显示流量弹窗
     */
    private void showTrafficTipsDialog(){
        TrafficTipsDialog mTrafficTipsDialog = TrafficTipsDialog.getInstance();
        mTrafficTipsDialog.setConfirmListener(new TrafficTipsDialog.OnConfirmListener() {
            @Override
            public void onResume() {
                startAllUploadTask();
                mTrafficTipsDialog.dismiss();
            }

            @Override
            public void onPause() {

            }
        });
        mTrafficTipsDialog.show(this);
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

    @Override
    public boolean bindEventBus() {
        return true;
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


    /**
     * 计时
     */
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
