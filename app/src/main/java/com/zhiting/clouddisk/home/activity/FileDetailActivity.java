package com.zhiting.clouddisk.home.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileDetailAdapter;
import com.zhiting.clouddisk.adapter.FileNavigationAdapter;
import com.zhiting.clouddisk.adapter.FileOperateAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityFileDetailBinding;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.FileDetailDialog;
import com.zhiting.clouddisk.dialog.InputPwdDialog;
import com.zhiting.clouddisk.dialog.More10MDialog;
import com.zhiting.clouddisk.dialog.RenameFileDialog;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;
import com.zhiting.clouddisk.event.FinishFileDetailEvent;
import com.zhiting.clouddisk.event.RunnableEvent;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.contract.FileDetailContract;
import com.zhiting.clouddisk.home.fragment.HomeFragment;
import com.zhiting.clouddisk.home.presenter.FileDetailPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.mine.activity.TrafficTipActivity2;
import com.zhiting.clouddisk.popup_window.UploadFilePopup;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.clouddisk.tbswebview.DownloadUtil;
import com.zhiting.clouddisk.util.CommonUtil;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.TimeUtil;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.fileutil.BaseFileUtil;
import com.zhiting.networklib.utils.pictureselectorutil.PicSelectorUtils;
import com.zhiting.networklib.utils.toast.ToastUtil;
import com.zhiting.networklib.utils.toast.ToastUtil1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

/**
 * 文件详情
 */
public class FileDetailActivity extends BaseMVPDBActivity<ActivityFileDetailBinding, FileDetailContract.View, FileDetailPresenter> implements FileDetailContract.View {

    private int from;//0 从文件夹/共享文件夹 1 从别人共享给我文件夹来
    private boolean hasLoad;  // 是否已经加载过
    private boolean needReduce;
    private int start;
    private boolean mRefresh; // 是否刷新
    private String filePwd; // 文件夹密码
    private RenameFileDialog renameFileDialog;
    private UploadFilePopup uploadFilePopup; // 上传文件类型选择
    private FileDetailAdapter fileDetailAdapter;
    private FileOperateAdapter fileOperateAdapter; // 文件操作列表适配器
    private FileNavigationAdapter fileNavigationAdapter; // 头部导航
    private List<FileBean> fileData; // 头部导航数据
    private List<FileOperateBean> mOperateData = new ArrayList<>(); // 文件操作列表数据
    private InputPwdDialog inputPwdDialog; // 输入密码弹窗

    private boolean needUpdatePwdDb; // 是否需要更新数据库的文件夹密码

    private String[] fn; // 修改文件夹  修改之后的名称
    private String[] ofn;  // // 修改之前的名称
    private boolean createOrUpdateFile; // 是否创建/修改文件
    private int createOrUpdateFileType; // 0. 创建文件夹  1.修改文件夹
    private String mCreateOrUpdateName; //  创建/修改文件名称
    private FileBean mUpdateFile; // 要修改的文件夹/文件
    private Runnable mRunnable;

    /**
     * 刷新加载更多监听
     */
    private OnRefreshLoadMoreListener refreshLoadMoreListener = new OnRefreshLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
            getData(false, false);
        }

        @Override
        public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
            getData(true, false);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_file_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOperateVisible(false);
        //如果已经加载过（也就是不是第一次进该界面），需要把跳转下一级的filebean移除
        if (hasLoad && needReduce && fileData != null && fileData.size() > 0) {
            fileData.remove(fileData.size() - 1);
            fileNavigationAdapter.notifyDataSetChanged();
        }
        uploadDownCount();
        binding.rrv.setRefreshAndLoadMore(true);
        if (fileData != null && fileData.size() > 0) {
            binding.ivAdd.setVisibility(fileData.get(fileData.size() - 1).getWrite() == 1 ? View.VISIBLE : View.GONE);
            binding.ivUpload.setVisibility(fileData.get(fileData.size() - 1).getWrite() == 1 ? View.VISIBLE : View.GONE);
        }
//        getData(true, true);
        hasLoad = true;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        LibLoader.addFileDetailAct(this);
        ToastUtil1.init(this);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        fileData = (List<FileBean>) intent.getSerializableExtra("navigation");
        String title = "";
        if (CollectionUtil.isNotEmpty(fileData))
            title = fileData.get(fileData.size() - 1).getName();
        filePwd = intent.getStringExtra("filePwd");
        from = intent.getIntExtra(Constant.FROM, 0);
        binding.tvTitle.setText(TextUtils.isEmpty(title) ? Constant.HOME_NAME : title);
        initRv();
        initRvNavigation();
        initRvOperateFile();
        initUploadFilePopup();
        initPwdDialog();
        fileNavigationAdapter.setNewData(fileData);
        getData(true, true);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (binding.rvOperate.getVisibility() == View.VISIBLE) {
            binding.rvOperate.setVisibility(View.GONE);
            if (fileDetailAdapter != null && CollectionUtil.isNotEmpty(fileDetailAdapter.getData())) {
                for (FileBean fileBean : fileDetailAdapter.getData()) {
                    fileBean.setSelected(false);
                }
                fileDetailAdapter.notifyDataSetChanged();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishFileDetailEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RunnableEvent event) {
        UiUtil.runInMainThread(mRunnable);
    }

    /**
     * 上传下载数量更新通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadDownloadEvent event) {
        uploadDownCount();
    }

    private void uploadDownCount() {
        int fileCount = GonetUtil.getUnderwayFileCount() + GonetUtil.getBackupFileCount();
        LogUtil.e("onMessageEvent1==" + fileCount);
        if (fileCount == 0) {
            binding.tvFileCount.setVisibility(View.GONE);
        } else {
            binding.tvFileCount.setVisibility(View.VISIBLE);
            binding.tvFileCount.setText(String.valueOf(fileCount));
        }
    }

    /**
     * 初始化文件列表
     */
    private void initRv() {
        fileDetailAdapter = new FileDetailAdapter(0);
        binding.rrv.setAdapter(fileDetailAdapter)
                .showLockerVisible(!TextUtils.isEmpty(HomeFragment.filePwd))
                .setOnRefreshAndLoadMoreListener(refreshLoadMoreListener);
        fileDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            FileBean fileBean = fileDetailAdapter.getItem(position);
            if (fileBean.getType() == 0) {  // 文件夹
                // 如果是加密文件夹且密码为空时，需要输入密码
                if (fileBean.getIs_encrypt() == 1 && TextUtils.isEmpty(HomeFragment.filePwd)) {
                    inputPwdDialog.show(this);
                    return;
                }
                if (fileDetailAdapter.getSelectedSize() <= 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.PATH, fileBean.getPath());
                    bundle.putString(Constant.NAME, fileBean.getName());
                    fileData.add(fileBean);
                    bundle.putInt(Constant.FROM, from);
                    bundle.putSerializable("navigation", (Serializable) fileData);
                    bundle.putString("filePwd", HomeFragment.filePwd);
                    needReduce = true;
                    switchToActivity(FileDetailActivity.class, bundle);
                }
            } else {  // 文件
                if (fileDetailAdapter.getSelectedSize() <= 0)
                    showDetailDialog(fileBean);
            }
        });

        fileDetailAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivSelected) {
                FileBean fileBean = fileDetailAdapter.getItem(position);
                fileBean.setSelected(!fileBean.isSelected());
                fileDetailAdapter.notifyItemChanged(position);
                List<FileBean> selectedData = fileDetailAdapter.getSelectedData();
                if (CollectionUtil.isNotEmpty(selectedData)) {
                    if (isEncrypt()) { // 如果是加密文件，不能共享
                        mOperateData.get(0).setEnabled(false);
                    } else {
                        mOperateData.get(0).setEnabled(fileDetailAdapter.isOnlyFolder() && FileUtil.hasWritePermission(selectedData));  // 是文件夹且有写权限才能共享
                    }
                    mOperateData.get(1).setEnabled(FileUtil.hasWritePermission(selectedData));  // 有写权限才能下载
                    mOperateData.get(2).setEnabled(FileUtil.hasDelPermission(selectedData));    // 有写权限才能移动
                    mOperateData.get(3).setEnabled(true);    // 复制不受权限影响
                    mOperateData.get(4).setEnabled(selectedData.size() == 1 && FileUtil.hasWritePermission(selectedData));  // 有写权限才能重命名
                    mOperateData.get(5).setEnabled(FileUtil.hasDelPermission(selectedData));  // 有删权限才能删除
                    fileOperateAdapter.notifyDataSetChanged();
                } else {
                    setAllEnabled();
                }
                setOperateVisible(selectedData.size() > 0);  // 设置底部文件操作是否可见
                binding.rrv.setRefreshAndLoadMore(selectedData.size() <= 0);  // 设置列表是否可刷新和加载更多
            }
        });
    }

    /**
     * 初始化头部导航
     */
    private void initRvNavigation() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvNavigation.setLayoutManager(layoutManager);
        fileNavigationAdapter = new FileNavigationAdapter();
        binding.rvNavigation.setAdapter(fileNavigationAdapter);
        fileNavigationAdapter.setOnItemClickListener((adapter, view, position) -> {
            FileBean fileBean = fileNavigationAdapter.getItem(position);
            if (position < fileNavigationAdapter.getData().size() - 1) {  // 不是最后一个才可点击
                if (TextUtils.isEmpty(fileBean.getPath())) {  //点击的路径为空
                    EventBus.getDefault().post(new FinishFileDetailEvent());
                } else {  //点击的路径不为空
                    int size = fileData.size();
                    for (int i = 0; i < size; i++) {  // 结束点击之后的文件夹详情
                        if (i > position) {
                            LibLoader.finishFileDetailAct();
                        }
                    }
                }
            }
        });
    }

    /**
     * 文件的操作
     */
    private void initRvOperateFile() {
        binding.rvOperate.setLayoutManager(new GridLayoutManager(this, 6));
        fileOperateAdapter = new FileOperateAdapter();
        binding.rvOperate.setAdapter(fileOperateAdapter);
        mOperateData.add(new FileOperateBean(R.drawable.icon_share, UiUtil.getString(R.string.home_share), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_download, UiUtil.getString(R.string.home_download), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_move, UiUtil.getString(R.string.home_move), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_copy, UiUtil.getString(R.string.home_copy), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_rename, UiUtil.getString(R.string.home_rename), true));
        mOperateData.add(new FileOperateBean(R.drawable.icon_remove, UiUtil.getString(R.string.home_remove), true));
        fileOperateAdapter.setNewData(mOperateData);
        fileOperateAdapter.setOnItemClickListener((adapter, view, position) -> {
            FileOperateBean fileOperateBean = fileOperateAdapter.getItem(position);
            if (fileOperateBean.isEnabled()) {
                switch (position) {
                    case 0:  // 共享
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("folder", (Serializable) fileDetailAdapter.getSelectedData());
                        bundle.putBoolean("originalWrite", FileUtil.hasWritePermission(fileDetailAdapter.getSelectedData()));
                        bundle.putBoolean("originalDel", FileUtil.hasDelPermission(fileDetailAdapter.getSelectedData()));
                        switchToActivity(ShareFolderActivity.class, bundle);
                        clearSelectedData();
                        break;

                    case 1:  // 下载
                        mRunnable = () -> {
                            downloadFiles(fileDetailAdapter.getSelectedData());
                        };
                        if (CommonUtil.isShowTraffic()) {
                            switchToActivity(TrafficTipActivity2.class);
                        } else {
                            UiUtil.runInMainThread(mRunnable);
                        }
                        break;

                    case 2:  // 移动
                        toMoveCopyActivity(0, fileDetailAdapter.getSelectedPath());
                        clearSelectedData();
                        break;

                    case 3:  // 复制到
                        toMoveCopyActivity(1, fileDetailAdapter.getSelectedPath());
                        clearSelectedData();
                        break;

                    case 4:  // 重命名
                        FileBean fileBean = fileDetailAdapter.getSingleSelectedData();
                        int drawableRes = R.drawable.icon_file_big;
                        if (fileBean.getType() == 0) {
                            drawableRes = R.drawable.icon_file_big;
                        } else {
                            /**
                             * 1. word
                             * 2. excel
                             * 3. ppt
                             * 4. 压缩包
                             * 5. 图片
                             * 6. 音频
                             * 7. 视频
                             * 8. 文本
                             *
                             */
                            int fileType = FileTypeUtil.fileType(fileBean.getName());
                            drawableRes = FileTypeUtil.getFileBigLogo(fileType);
                        }
                        showCreateFileDialog(1, drawableRes, fileBean);
                        break;

                    case 5:  // 删除
                        showRemoveFileTips(fileDetailAdapter.getSelectedPath());
                        break;
                }
            }
        });
    }

    /**
     * 清除选择数据
     */
    private void clearSelectedData(){
        if (fileDetailAdapter != null && CollectionUtil.isNotEmpty(fileDetailAdapter.getData())) {
            List<FileBean> data = fileDetailAdapter.getData();
            for (int i=0; i<data.size(); i++) {
                FileBean fileBean = data.get(i);
                if (fileBean.isSelected()) {
                    fileBean.setSelected(false);
                    fileDetailAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    /**
     * 显示输入密码弹窗
     */
    private void initPwdDialog() {
        inputPwdDialog = InputPwdDialog.getInstance();
        inputPwdDialog.setConfirmListener(pwd -> {
            needUpdatePwdDb = true;
            HomeFragment.filePwd = pwd;
            checkFilePwd();
        });
    }

    /**
     * 校验文件夹密码是否正确
     */
    private void checkFilePwd() {
        if (TextUtils.isEmpty(HomeFragment.filePwd)) { // 如果密码为空，则输入
            inputPwdDialog.show(this);
        } else {  // 密码不为空，校验密码
            CheckPwdRequest checkPwdRequest = new CheckPwdRequest(HomeFragment.filePwd);
            mPresenter.decryptFile(Constant.scope_token, getNowFolder().getPath(), checkPwdRequest);
        }
    }

    /**
     * 下载多个文件
     *
     * @param list 选中的文件
     */
    private void downloadFiles(List<FileBean> list) {
        if (CollectionUtil.isEmpty(list)) return;
        for (FileBean fileBean : list) {
            downloadFile(fileBean);
        }
    }

    /**
     * @param fileBean
     */
    private void downloadFile(FileBean fileBean) {
        ToastUtil.show(UiUtil.getString(R.string.common_add_upload_list));
        if (fileBean.getType() == 1) {
            GonetUtil.downloadFile(fileBean.getPath(), fileBean.getThumbnail_url(), HomeFragment.filePwd, () -> changeDownloadCount());
        } else {
            GonetUtil.downloadFolder(fileBean.getPath(), HomeFragment.filePwd, () -> changeDownloadCount());
        }

        UiUtil.postDelayed(() -> {
            resetSelectStatus();
            setOperateVisible(false);
        }, 500);
    }

    private void changeDownloadCount() {
        UiUtil.runInMainThread(() -> uploadDownCount());
    }

    private void resetSelectStatus() {
        List<FileBean> list = fileDetailAdapter.getData();
        if (CollectionUtil.isNotEmpty(list)) {
            for (FileBean fileBean : list) {
                fileBean.setSelected(false);
            }
        }
        fileDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 移动复制
     *
     * @param type
     * @param paths
     */
    private void toMoveCopyActivity(int type, List<String> paths) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putSerializable("paths", (Serializable) paths);
        bundle.putInt(Constant.FROM, from);
        List<FileBean> encryptData = new ArrayList<>();
        // 只获取加密文件的根路径
        if (fromNormalFolderAndEncrypt()) {
            encryptData.add(fileData.get(1));
        }

        if (!anyWhere(type)) {
            bundle.putSerializable("navigation", fromNormalFolderAndEncrypt() ? (Serializable) encryptData : (Serializable) fileData);
        }
        needReduce = false;
        bundle.putString("filePwd", filePwd);

        bundle.putString("parentPath", getNowFolder().getPath());
        switchToActivity(anyWhere(type) ? MoveCopyFileActivity.class : MoveCopyDetailActivity.class, bundle);
    }

    /**
     * 是否从加密文件过来
     *
     * @return
     */
    private boolean fromNormalFolderAndEncrypt() {
        if (from == 0 && !TextUtils.isEmpty(filePwd)) {
            return true;
        }
        return false;
    }

    /**
     * 是否可以移到任何位置
     *
     * @return
     */
    private boolean anyWhere(int type) {
        if (type == 1) {  // 复制的情况
            return !isEncrypt();
        } else { // 移动的情况
            if (from == 0) {  // 文件/共享文件夹
                return !isEncrypt();
            } else {  // 别人共享给我的文件夹
                return false;
            }
        }
    }

    /**
     * 判断是否加密
     *
     * @return
     */
    private boolean isEncrypt() {
        if (TextUtils.isEmpty(filePwd)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置所有操作都可以点击
     */
    private void setAllEnabled() {
        for (FileOperateBean fileOperateBean : mOperateData) {
            fileOperateBean.setEnabled(true);
        }
        fileOperateAdapter.notifyDataSetChanged();
    }

    /**
     * 上传文件裂隙选择
     */
    private void initUploadFilePopup() {
        uploadFilePopup = new UploadFilePopup(this);
        uploadFilePopup.setOnDismissListener(() -> binding.viewMask.setVisibility(View.GONE));
        uploadFilePopup.setUploadOperateListener(position -> {
            switch (position) {
                case 0:
                    selectLocalMedia(PictureConfig.TYPE_VIDEO);
                    break;
                case 1:
                    selectLocalMedia(PictureConfig.TYPE_IMAGE);
                    break;
                case 2:
                    selectOtherFile();
                    break;
            }
        });
    }

    /**
     * 打开其他文件
     */
    private void selectOtherFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * 打开本地图片或者视频
     *
     * @param pictureType
     */
    private void selectLocalMedia(int pictureType) {
        PicSelectorUtils.openFileType(FileDetailActivity.this, pictureType, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                mRunnable = () -> {
                    ToastUtil1.show(UiUtil.getString(R.string.common_add_upload_list));
                    for (LocalMedia localMedia : result) {
                        uploadFile(localMedia.getRealPath());
                    }
                };
                if (CommonUtil.isShowTraffic()) {
                    switchToActivity(TrafficTipActivity2.class);
                } else {
                    UiUtil.runInMainThread(mRunnable);
                }
            }

            @Override
            public void onCancel() {
                LogUtil.e("用户取消了");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = BaseFileUtil.getFilePathByUri(this, uri);
            mRunnable = () -> {
                ToastUtil1.show(UiUtil.getString(R.string.common_add_upload_list));
                uploadFile(filePath);
            };
            if (CommonUtil.isShowTraffic()) {
                switchToActivity(TrafficTipActivity2.class);
            } else {
                UiUtil.runInMainThread(mRunnable);
            }
        }
    }

    /**
     * 上传文件
     *
     * @param filePath
     */
    private void uploadFile(String filePath) {
        String folderPath = fileData.get(fileData.size() - 1).getPath();
        GonetUtil.uploadFile(filePath, folderPath, HomeFragment.filePwd, 0, () -> changeDownloadCount());
    }

    /**
     * 显示文件详情弹窗
     * 1. word
     * 2. excel
     * 3. ppt
     * 4. 压缩包
     * 5. 图片
     * 6. 音频
     * 7. 视频
     * 8. 文本
     */
    private void showDetailDialog(FileBean fileBean) {
        FileDetailDialog fileDetailDialog = FileDetailDialog.getInstance(fileBean);
        fileDetailDialog.setOperateListener((fileTypeName, fileBean1) -> {
            fileDetailDialog.dismiss();
            List<String> paths = new ArrayList<>();
            paths.add(fileBean1.getPath());

            if (fileTypeName.equals(UiUtil.getString(R.string.home_download))) {
                mRunnable = () -> {
                    downloadFile(fileBean);
                };
                if (CommonUtil.isShowTraffic()) {
                    switchToActivity(TrafficTipActivity2.class);
                } else {
                    UiUtil.runInMainThread(mRunnable);
                }
            } else if (fileTypeName.equals(UiUtil.getString(R.string.home_move))) {
                toMoveCopyActivity(0, paths);
            } else if (fileTypeName.equals(UiUtil.getString(R.string.home_copy))) {
                toMoveCopyActivity(1, paths);
            } else if (fileTypeName.equals(UiUtil.getString(R.string.home_review))) {
                int size = (int) (fileBean.getSize() / 1024 / 1024);
                if (size >= 10) {
                    shoSizeDialog(fileBean);
                    return;
                }
                previewMedia(fileBean);
            } else if (fileTypeName.equals(UiUtil.getString(R.string.home_rename))) {
                int drawableRes = R.drawable.icon_file_big;
                if (fileBean1.getType() == 1) {
                    int fileType = FileTypeUtil.fileType(fileBean1.getName());
                    drawableRes = FileTypeUtil.getFileLogo(fileType);
                }
                showCreateFileDialog(1, drawableRes, fileBean1);
            } else if (fileTypeName.equals(UiUtil.getString(R.string.home_remove))) {
                showRemoveFileTips(paths);
            }
        });
        fileDetailDialog.show(this);
    }

    /**
     * 多媒体预览
     * * 显示文件详情弹窗
     * * 1. word
     * * 2. excel
     * * 3. ppt
     * * 4. 压缩包
     * * 5. 图片
     * * 6. 音频
     * * 7. 视频
     * * 8. 文本
     */
    private void previewMedia(FileBean fileBean) {
        String baseUrl = HttpConfig.baseTestUrl + "/api" + HttpConfig.downLoadFileUrl;
        int fileType = FileTypeUtil.fileType(fileBean.getName());

        if (fileType == 7) {//视频
            String videoUrl = baseUrl + fileBean.getPath();
            if (videoUrl.endsWith("3gp") || videoUrl.endsWith("mpg")) {
                PlayerFactory.setPlayManager(Exo2PlayerManager.class);
                CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            } else {
                PlayerFactory.setPlayManager(IjkPlayerManager.class);
                CacheFactory.setCacheManager(ProxyCacheManager.class);
            }
            VideoActivity.startActivity(this, videoUrl, fileBean.getThumbnail_url(), fileBean.getName());
        } else if (fileType == 5) {//图片
            preViewImage(fileBean.getName());
        } else if (fileType == 6) {//音频
            PlayerFactory.setPlayManager(Exo2PlayerManager.class);
            CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            String audioUrl = baseUrl + fileBean.getPath();
            AudioActivity.startActivity(this, audioUrl, fileBean.getName(), fileBean.getSize());
        } else if (fileType == 1 || fileType == 2 || fileType == 3 || fileType == 4 || fileType == 8 || fileType == 9) {
            String url = baseUrl + fileBean.getPath();
            DownloadUtil.get().startDownload(this, url, fileBean.getName());
        }
    }

    /**
     * 大于10M提示
     *
     * @param fileBean
     */
    private void shoSizeDialog(FileBean fileBean) {
        More10MDialog dialog = More10MDialog.getInstance();
        dialog.setConfirmListener(new More10MDialog.OnConfirmListener() {
            @Override
            public void onReview() {
                previewMedia(fileBean);
            }

            @Override
            public void onDownload() {
                mRunnable = () -> {
                    GonetUtil.downloadFile(fileBean.getPath(), fileBean.getThumbnail_url(), HomeFragment.filePwd, () -> changeDownloadCount());
                };
                if (CommonUtil.isShowTraffic()) {
                    switchToActivity(TrafficTipActivity2.class);
                } else {
                    UiUtil.runInMainThread(mRunnable);
                }
            }
        });
        dialog.show(this);
    }

    /**
     * 预览图片
     */
    private void preViewImage(String selectName) {
        List<LocalMedia> images = new ArrayList<>();
        List<FileBean> fileBeans = fileDetailAdapter.getData();
        String baseDownLoadFileUrl = HttpConfig.baseTestUrl + "/api" + HttpConfig.downLoadFileUrl;
        baseDownLoadFileUrl = baseDownLoadFileUrl.replace("//api", "/api").replace("download//", "download/");

        if (CollectionUtil.isNotEmpty(fileBeans)) {
            for (FileBean bean : fileBeans) {
                int type = FileTypeUtil.fileType(bean.getName());
                if (type == 5) {
                    String imageUrl = baseDownLoadFileUrl + bean.getPath();
                    imageUrl = imageUrl.replace("//api", "/api").replace("download//", "download/");
                    LocalMedia media = new LocalMedia();
                    media.setPath(imageUrl);
                    media.setOriginalPath(imageUrl);
                    media.setRealPath(imageUrl);
                    media.setFileName(bean.getName());
                    images.add(media);
                }
            }
            int position = 0;
            for (int i = 0; i < images.size(); i++) {
                if (selectName.equalsIgnoreCase(images.get(i).getFileName())) {
                    position = i;
                    break;
                }
            }
            PicSelectorUtils.openPreviewCustomImages(this, position, images, PictureCustomPreviewActivity.class);
        }
    }

    /**
     * 设置底部文件操作是否可见
     *
     * @param visible
     */
    private void setOperateVisible(boolean visible) {
        binding.rvOperate.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取数据
     *
     * @param refresh     是否刷新
     * @param showLoading 是否显示加载弹窗
     */
    private void getData(boolean refresh, boolean showLoading) {
        mRefresh = refresh;
        map.clear();
        start = refresh ? 1 : start + 1;
        map.put(HttpUrlParams.PAGE, String.valueOf(start));
        map.put(HttpUrlParams.PAGE_SIZE, String.valueOf(Constant.pageSize));
        FileBean fileBean = getNowFolder();
        if (fileBean != null && !TextUtils.isEmpty(fileBean.getPath())) {
            mPresenter.getFiles(Constant.scope_token, HomeFragment.filePwd, fileBean.getPath(), map, showLoading);
        }
    }

    /**
     * 当前文件夹
     *
     * @return
     */
    private FileBean getNowFolder() {
        if (fileData != null && fileData.size() > 0) {
            return fileData.get(fileData.size() - 1);
        }
        return null;
    }

    /**
     * 文件列表成功
     *
     * @param fileListBean
     */
    @Override
    public void getFilesSuccess(FileListBean fileListBean) {
        if (fileListBean != null) {
            List<FileBean> files = fileListBean.getList();
            if (mRefresh) {
                fileDetailAdapter.setNewData(files);
                binding.rrv.showEmptyView(CollectionUtil.isEmpty(files));
            } else {
                fileDetailAdapter.addData(files);
            }
            boolean hasMore = false;
            PagerBean pagerBean = fileListBean.getPager();
            if (pagerBean != null) {
                hasMore = pagerBean.isHas_more();
            }
            binding.rrv.finishRefresh(!hasMore);
            binding.rvOperate.setVisibility(View.GONE);
        }
    }

    /**
     * 文件列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFilesFail(int errorCode, String msg) {
        LogUtil.e(msg);
        binding.rrv.finishRefresh(false);

    }

    /**
     * 创建文件夹成功
     *
     * @param uploadCreateFileBean
     */
    @Override
    public void createFileSuccess(UploadCreateFileBean uploadCreateFileBean) {
        ToastUtil.show(UiUtil.getString(R.string.home_save_success));
        getData(true, true);
        closeDialog();
    }

    /**
     * 创建文件夹失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void createFileFail(int errorCode, String msg) {

    }

    /**
     * 重命名成功
     */
    @Override
    public void renameSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_rename_success));
        getData(true, true);
        setAllEnabled();
        setOperateVisible(false);
        closeDialog();
    }

    /**
     * 重命名失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void renameFail(int errorCode, String msg) {

    }

    /**
     * 删除成功
     */
    @Override
    public void removeFileSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_remove_success));
        setOperateVisible(false);
        getData(true, true);
    }

    /**
     * 删除失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void removeFileFail(int errorCode, String msg) {

    }

    /**
     * 密码校验成功
     */
    @Override
    public void decryptPwdSuccess() {
        if (inputPwdDialog != null && inputPwdDialog.isShowing()) {
            inputPwdDialog.dismiss();
        }
        if (needUpdatePwdDb) { // 如果是重新输入密码，需要更新数据库
            HomeFragment.mFolderPwd.setPassword(HomeFragment.filePwd);
            HomeFragment.mFolderPwd.setModifyTime(TimeUtil.getCurrentTimeMillis());
            mPresenter.updateFolderPwd(HomeFragment.mFolderPwd);
        }
        if (createOrUpdateFile) {
            createOrUpdateFile();
            createOrUpdateFile = false;
        } else {
            getData(true, true);
        }
        needUpdatePwdDb = false;
    }

    /**
     * 更新文件夹密码
     */
    private void updateFolderPwd() {
        HomeFragment.mFolderPwd.setPassword(HomeFragment.filePwd);
        HomeFragment.mFolderPwd.setModifyTime(TimeUtil.getCurrentTimeMillis());
        mPresenter.updateFolderPwd(HomeFragment.mFolderPwd);
    }

    /**
     * 密码校验失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void decryptPwdFail(int errorCode, String msg) {
        // 不要删，不确定需求会不会改
//        if (errorCode == ErrorConstant.PWD_ERROR) { // 文件夹密码错误
//            HomeFragment.filePwd = "";
//            updateFolderPwd();
//            if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
//                inputPwdDialog.show(this);
//            }
//        }
//        needUpdatePwdDb = false;
        if (errorCode == ErrorConstant.PWD_ERROR) { // 文件夹密码错误
            HomeFragment.filePwd = "";
            updateFolderPwd();
            EventBus.getDefault().post(new FinishFileDetailEvent());
        }
    }


    /**
     * 修改文件夹密码成功
     */
    @Override
    public void updateFolderPwdSuccess() {
        LogUtil.e("修改文件夹密码成功");
    }

    /**
     * 修改文件夹密码失败
     */
    @Override
    public void updateFolderPwdFail() {
        LogUtil.e("修改文件夹密码失败");
    }

    /**
     * 关闭创建文件夹弹窗
     */
    private void closeDialog() {
        if (renameFileDialog != null && renameFileDialog.isShowing()) {
            renameFileDialog.dismiss();
        }
    }

    /**
     * 创建/重新命名 文件夹
     */
    private void showCreateFileDialog(int type, int drawable, FileBean fileBean) {
        renameFileDialog = RenameFileDialog.getInstance(type, drawable, type == 1 ? fileBean.getName() : "");
        renameFileDialog.setCompleteListener(new RenameFileDialog.OnCompleteListener() {
            @Override
            public void onComplete(int type, String fileName) {
                createOrUpdateFile = true;
                createOrUpdateFileType = type;
                mCreateOrUpdateName = fileName;
                mUpdateFile = fileBean;
                if (TextUtils.isEmpty(HomeFragment.filePwd)) {
                    createOrUpdateFile();
                } else {
                    checkFilePwd();
                }
            }

            @Override
            public void onDismissListener(DialogInterface dialog) {
                createOrUpdateFile = false;
                binding.rrv.setRefreshAndLoadMore(true);
            }
        });
        renameFileDialog.show(this);
    }

    private void createOrUpdateFile() {
        if (createOrUpdateFileType == 0) { // 创建文件夹
            mPresenter.createFile(Constant.scope_token, filePwd, getNowFolder().getPath() + "/" + mCreateOrUpdateName + "/");
        } else {  // 重新命名
            fn = mCreateOrUpdateName.split("\\."); // 修改之后的名称
            ofn = mUpdateFile.getName().split("\\."); // 修改之前的名称
            int fnLength = fn.length; // 修改之后的名称长度
            int ofnLength = ofn.length; // 修改之前的名称长度
            if (fnLength != ofnLength) {
                showFileTypeChangeDialog(mCreateOrUpdateName, mUpdateFile);
            } else {
                if (fnLength > 1 && ofnLength > 1) {  // 文件类型改变
                    if (!fn[fnLength - 1].equalsIgnoreCase(ofn[ofnLength - 1])) {  // 文件类型改变
                        showFileTypeChangeDialog(mCreateOrUpdateName, mUpdateFile);
                    } else { // 文件类型没改变
                        mPresenter.renameFile(Constant.scope_token, mUpdateFile.getPath(), new NameRequest(mCreateOrUpdateName));
                    }
                } else {  // 文件类型没改变
                    mPresenter.renameFile(Constant.scope_token, mUpdateFile.getPath(), new NameRequest(mCreateOrUpdateName));
                }
            }
        }
    }

    /**
     * 文件类型改变
     */
    private void showFileTypeChangeDialog(String fileName, FileBean fileBean) {
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance("",
                UiUtil.getString(R.string.home_modify_file_type_tips));
        centerAlertDialog.setConfirmListener(() -> {
            mPresenter.renameFile(Constant.scope_token, fileBean.getPath(), new NameRequest(fileName));
            centerAlertDialog.dismiss();
        });
        centerAlertDialog.show(this);
    }

    /**
     * 删除文件弹窗
     *
     * @param paths
     */
    private void showRemoveFileTips(List<String> paths) {
        String title = String.format(UiUtil.getString(R.string.home_remove_file_title), paths.size());
        String tips = UiUtil.getString(R.string.home_remove_file_tips);
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(title, tips);
        centerAlertDialog.setConfirmListener(() -> {
            mPresenter.removeFile(Constant.scope_token, new ShareRequest(paths));
            centerAlertDialog.dismiss();
        });
        centerAlertDialog.show(this);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            } else if (viewId == R.id.ivAdd) { // 添加文件夹
                showCreateFileDialog(0, R.drawable.icon_file_big, null);
            } else if (viewId == R.id.ivUpload) { // 上传
                uploadFilePopup.showAsDropDown(binding.llTop);
                binding.viewMask.setVisibility(View.VISIBLE);
            } else if (viewId == R.id.flList) { // 上传下载列表
                switchToActivity(UpDownLoadActivity.class);
            }
        }
    }
}