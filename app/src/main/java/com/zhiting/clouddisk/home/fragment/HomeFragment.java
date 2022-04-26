package com.zhiting.clouddisk.home.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.HomeFileAdapter;
import com.zhiting.clouddisk.databinding.FragmentHomeBinding;
import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.dialog.InputPwdDialog;
import com.zhiting.clouddisk.dialog.OperateFileDialog;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;
import com.zhiting.clouddisk.event.ChangeHomeEvent;
import com.zhiting.clouddisk.event.OperateFileEvent;
import com.zhiting.clouddisk.event.RefreshAuthEvent;
import com.zhiting.clouddisk.event.RefreshDataEvent;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.FileDetailActivity;
import com.zhiting.clouddisk.home.activity.UpDownLoadActivity;
import com.zhiting.clouddisk.home.contract.HomeContract;
import com.zhiting.clouddisk.home.presenter.HomePresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.util.TimeUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件
 */
public class HomeFragment extends BaseMVPDBFragment<FragmentHomeBinding, HomeContract.View, HomePresenter> implements HomeContract.View {

    private int start;
    private boolean mRefresh;
    public static String filePwd; // 文件夹密码
    private FileBean mFileBean; // 选择的文件
    private InputPwdDialog inputPwdDialog; // 输入密码弹窗
    private OperateFileDialog operateFileDialog;
    private HomeFileAdapter homeFileAdapter;
    private List<FileOperateBean> operateData;
    public static FolderPassword mFolderPwd;  // 从数据库查的文件夹密码

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        binding.setHandler(new OnClickHandler());
        initRv();
        initPwdDialog();
        initOperateDialog();
    }

    @Override
    protected void lazyLoad() {
        getData(true, true);
    }

    @Override
    protected void refreshAuth() {
        super.refreshAuth();
        if (Constant.authBackBean != null) {
            if (Constant.authBackBean.getHomeCompanyBean() != null) {
                binding.tvHome.setText(Constant.authBackBean.getHomeCompanyBean().getName());
            }
        }
    }

    @Override
    protected void selectedHome(AuthBackBean authBackBean) {
        super.selectedHome(authBackBean);
        binding.tvHome.setText(authBackBean.getHomeCompanyBean().getName());
        getData(true, true);
    }

    @Override
    protected void familyPopupWindowDismiss() {
        super.familyPopupWindowDismiss();
        binding.tvHome.setSelected(false);
    }

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

    /**
     * 初始化列表
     */
    private void initRv() {
        homeFileAdapter = new HomeFileAdapter(1, true);
        binding.rrv.setAdapter(homeFileAdapter)
                .setOnRefreshAndLoadMoreListener(refreshLoadMoreListener);
        homeFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            mFileBean = homeFileAdapter.getItem(position);
            if (mFileBean.getType() == 0 && homeFileAdapter.getSelectedSize() <= 0) { // 如果是文件夹，且没有处于编辑状态
                if (mFileBean.getRead() == 1) {  // 有读权限
                    if (mFileBean.getIs_encrypt() == 1) { // 加密文件
                        mPresenter.getFolderPwdByScopeTokenAndPath(Constant.scope_token, mFileBean.getPath());
                    } else {  // 非加密文件
                        filePwd = "";
                        toFolderDetail(false);
                    }
                } else {  // 没有读权限
                    ToastUtil.show(UiUtil.getString(R.string.mine_without_read_permission));
                }
            }
        });

        homeFileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivSelected) {
                FileBean fileBean = homeFileAdapter.getItem(position);
                fileBean.setSelected(!fileBean.isSelected());
                homeFileAdapter.notifyItemChanged(position);
                EventBus.getDefault().post(new OperateFileEvent(homeFileAdapter.getSelectedSize(), homeFileAdapter.isOnlyFolder()));
                binding.rrv.setRefreshAndLoadMore(homeFileAdapter.getSelectedSize() <= 0);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            filePwd = "";
        } else {
            uploadDownCount();
            LogUtil.e("onHiddenChanged1");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uploadDownCount();
        LogUtil.e("onHiddenChanged1=onResume");
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        binding.rrv.finishRefresh(true);
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

    /**
     * 找到sa地址后刷新数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshDataEvent event){
        if (homeFileAdapter != null && CollectionUtil.isEmpty(homeFileAdapter.getData())){
            getData(true, false);
        }
    }

    /**
     * 家庭改变之后刷新数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangeHomeEvent event) {
        binding.tvFile.setVisibility(View.GONE);
        binding.rrv.setVisibility(View.GONE);
        if (isVisible()) {
            getData(true, true);
        }
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
     * 刷新授权信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshAuthEvent event) {
        refreshAuth();
    }

    /**
     * 初始化操作文件弹窗
     */
    private void initOperateDialog() {
        operateFileDialog = new OperateFileDialog();
        operateData = new ArrayList<>();
        operateData.add(new FileOperateBean(R.drawable.icon_share, UiUtil.getString(R.string.home_share), true));
        operateData.add(new FileOperateBean(R.drawable.icon_download, UiUtil.getString(R.string.home_download), true));
        operateData.add(new FileOperateBean(R.drawable.icon_move, UiUtil.getString(R.string.home_move), true));
        operateData.add(new FileOperateBean(R.drawable.icon_copy, UiUtil.getString(R.string.home_copy), true));
        operateData.add(new FileOperateBean(R.drawable.icon_rename, UiUtil.getString(R.string.home_rename), true));
        operateData.add(new FileOperateBean(R.drawable.icon_remove, UiUtil.getString(R.string.home_remove), true));
        operateFileDialog.setOperateData(operateData);
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
                homeFileAdapter.setNewData(files);
                binding.tvFile.setVisibility(CollectionUtil.isNotEmpty(files) ? View.VISIBLE : View.GONE);
                if (binding.rrv.getVisibility() != View.VISIBLE) {
                    binding.rrv.setVisibility(View.VISIBLE);
                }
                binding.rrv.showEmptyView(CollectionUtil.isEmpty(files));
            } else {
                homeFileAdapter.addData(files);
            }
            boolean hasMore = false;
            PagerBean pagerBean = fileListBean.getPager();
            if (pagerBean != null) {
                hasMore = pagerBean.isHas_more();
            }
            binding.rrv.finishRefresh(!hasMore);
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

    @Override
    public void showError(int errorCode, String msg) {
        super.showError(errorCode, msg);
        binding.rrv.finishRefresh(false);
    }

    /**
     * 解密文件成功
     */
    @Override
    public void decryptPwdSuccess() {
        if (inputPwdDialog != null && inputPwdDialog.isShowing()) {
            inputPwdDialog.dismiss();
        }
        if (mFolderPwd == null) {
            mFolderPwd = new FolderPassword(Constant.USER_ID, mFileBean.getPath(), filePwd, Constant.scope_token, TimeUtil.getCurrentTimeMillis());
            mPresenter.insertFolderPwd(mFolderPwd);
        } else {
            updateFolderPwd();
        }
        toFolderDetail(true);
    }

    /**
     * 更新文件夹密码
     */
    private void updateFolderPwd() {
        mFolderPwd.setPassword(filePwd);
        mFolderPwd.setModifyTime(TimeUtil.getCurrentTimeMillis());
        mPresenter.updateFolderPwd(mFolderPwd);
    }

    /**
     * 解密文件失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void decryptPwdFail(int errorCode, String msg) {
        if (errorCode == ErrorConstant.PWD_ERROR) { // 文件夹密码错误
            if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
                filePwd = "";
                updateFolderPwd();
                inputPwdDialog.show(this);
            }
        }
    }

    /**
     * 获取密码成功
     *
     * @param folderPassword
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathSuccess(FolderPassword folderPassword) {
        LogUtil.e("查询文件夹密码成功");
        if (folderPassword != null) {
            filePwd = folderPassword.getPassword();
            long modifyTime = folderPassword.getModifyTime();
            long distinct = TimeUtil.getCurrentTimeMillis() - modifyTime;
            mFolderPwd = folderPassword;
            if (TimeUtil.over72hour(distinct) || TextUtils.isEmpty(filePwd)) {  // 超过72小时
                showInputPwdDialog();
            } else {
                checkFilePwd();
            }
        } else {
            showInputPwdDialog();
        }
    }

    /**
     * 获取密码失败
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathFail() {
        LogUtil.e("查询文件夹密码失败");
        showInputPwdDialog();
    }

    /**
     * 插入密码成功
     *
     * @param result
     */
    @Override
    public void insertFolderPwdSuccess(boolean result) {
        LogUtil.e("插入文件夹密码成功");
    }

    /**
     * 插入密码失败
     */
    @Override
    public void insertFolderFail() {
        LogUtil.e("插入文件夹密码失败");
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
     * 显示输入密码弹窗
     */
    private void showInputPwdDialog() {
        if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
            inputPwdDialog.show(HomeFragment.this);
        }
    }

    /**
     * 获取数据
     *
     * @param refresh
     */
    private void getData(boolean refresh, boolean showLoading) {
        mRefresh = refresh;
        map.clear();
        start = refresh ? 1 : start + 1;
        map.put(HttpUrlParams.PAGE, String.valueOf(start));
        map.put(HttpUrlParams.PAGE_SIZE, String.valueOf(Constant.pageSize));
        mPresenter.getFiles(Constant.scope_token, "/", map, showLoading);
    }

    /**
     * 显示输入密码弹窗
     */
    private void initPwdDialog() {
        inputPwdDialog = InputPwdDialog.getInstance();
        inputPwdDialog.setConfirmListener(pwd -> {
            filePwd = pwd;
            checkFilePwd();
        });
    }

    /**
     * 校验文件夹密码是否正确
     */
    private void checkFilePwd() {
        if (mFileBean != null) {
            if (TextUtils.isEmpty(filePwd)) { // 如果密码为空，则输入
                inputPwdDialog.show(this);
            } else { // 密码不为空，校验密码
                CheckPwdRequest checkPwdRequest = new CheckPwdRequest(filePwd);
                mPresenter.decryptFile(Constant.scope_token, mFileBean.getPath(), checkPwdRequest);
            }
        }
    }

    /**
     * 文件夹详情
     */
    private void toFolderDetail(boolean hasEncrypted) {
        Bundle bundle = new Bundle();
        List<FileBean> fileData = new ArrayList<>();
        fileData.add(new FileBean(UiUtil.getString(R.string.home_file), 0));
        fileData.add(mFileBean);
        bundle.putInt(Constant.FROM, 0);
        bundle.putSerializable("navigation", (Serializable) fileData);
        bundle.putString("filePwd", hasEncrypted ? filePwd : "");
        switchToActivity(FileDetailActivity.class, bundle);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvHome) { // 我的家
                binding.tvHome.setSelected(true);
                familyPopupWindow.showAsDown(binding.tvHome);
            } else if (viewId == R.id.flList) { // 上传下载列表
                switchToActivity(UpDownLoadActivity.class);
            }
        }
    }
}
