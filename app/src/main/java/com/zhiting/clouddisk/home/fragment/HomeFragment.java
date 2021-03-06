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
 * ??????
 */
public class HomeFragment extends BaseMVPDBFragment<FragmentHomeBinding, HomeContract.View, HomePresenter> implements HomeContract.View {

    private int start;
    private boolean mRefresh;
    public static String filePwd; // ???????????????
    private FileBean mFileBean; // ???????????????
    private InputPwdDialog inputPwdDialog; // ??????????????????
    private OperateFileDialog operateFileDialog;
    private HomeFileAdapter homeFileAdapter;
    private List<FileOperateBean> operateData;
    public static FolderPassword mFolderPwd;  // ?????????????????????????????????

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
     * ????????????????????????
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
     * ???????????????
     */
    private void initRv() {
        homeFileAdapter = new HomeFileAdapter(1, true);
        binding.rrv.setAdapter(homeFileAdapter)
                .setOnRefreshAndLoadMoreListener(refreshLoadMoreListener);
        homeFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            mFileBean = homeFileAdapter.getItem(position);
            if (mFileBean.getType() == 0 && homeFileAdapter.getSelectedSize() <= 0) { // ????????????????????????????????????????????????
                if (mFileBean.getRead() == 1) {  // ????????????
                    if (mFileBean.getIs_encrypt() == 1) { // ????????????
                        mPresenter.getFolderPwdByScopeTokenAndPath(Constant.scope_token, mFileBean.getPath());
                    } else {  // ???????????????
                        filePwd = "";
                        toFolderDetail(false);
                    }
                } else {  // ???????????????
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
     * ??????????????????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadDownloadEvent event) {
        uploadDownCount();
    }

    /**
     * ??????sa?????????????????????
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshDataEvent event){
        if (homeFileAdapter != null && CollectionUtil.isEmpty(homeFileAdapter.getData())){
            getData(true, false);
        }
    }

    /**
     * ??????????????????????????????
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
     * ??????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshAuthEvent event) {
        refreshAuth();
    }

    /**
     * ???????????????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ?????????????????????
     */
    private void updateFolderPwd() {
        mFolderPwd.setPassword(filePwd);
        mFolderPwd.setModifyTime(TimeUtil.getCurrentTimeMillis());
        mPresenter.updateFolderPwd(mFolderPwd);
    }

    /**
     * ??????????????????
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void decryptPwdFail(int errorCode, String msg) {
        if (errorCode == ErrorConstant.PWD_ERROR) { // ?????????????????????
            if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
                filePwd = "";
                updateFolderPwd();
                inputPwdDialog.show(this);
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param folderPassword
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathSuccess(FolderPassword folderPassword) {
        LogUtil.e("???????????????????????????");
        if (folderPassword != null) {
            filePwd = folderPassword.getPassword();
            long modifyTime = folderPassword.getModifyTime();
            long distinct = TimeUtil.getCurrentTimeMillis() - modifyTime;
            mFolderPwd = folderPassword;
            if (TimeUtil.over72hour(distinct) || TextUtils.isEmpty(filePwd)) {  // ??????72??????
                showInputPwdDialog();
            } else {
                checkFilePwd();
            }
        } else {
            showInputPwdDialog();
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathFail() {
        LogUtil.e("???????????????????????????");
        showInputPwdDialog();
    }

    /**
     * ??????????????????
     *
     * @param result
     */
    @Override
    public void insertFolderPwdSuccess(boolean result) {
        LogUtil.e("???????????????????????????");
    }

    /**
     * ??????????????????
     */
    @Override
    public void insertFolderFail() {
        LogUtil.e("???????????????????????????");
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void updateFolderPwdSuccess() {
        LogUtil.e("???????????????????????????");
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void updateFolderPwdFail() {
        LogUtil.e("???????????????????????????");
    }

    /**
     * ????????????????????????
     */
    private void showInputPwdDialog() {
        if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
            inputPwdDialog.show(HomeFragment.this);
        }
    }

    /**
     * ????????????
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
     * ????????????????????????
     */
    private void initPwdDialog() {
        inputPwdDialog = InputPwdDialog.getInstance();
        inputPwdDialog.setConfirmListener(pwd -> {
            filePwd = pwd;
            checkFilePwd();
        });
    }

    /**
     * ?????????????????????????????????
     */
    private void checkFilePwd() {
        if (mFileBean != null) {
            if (TextUtils.isEmpty(filePwd)) { // ??????????????????????????????
                inputPwdDialog.show(this);
            } else { // ??????????????????????????????
                CheckPwdRequest checkPwdRequest = new CheckPwdRequest(filePwd);
                mPresenter.decryptFile(Constant.scope_token, mFileBean.getPath(), checkPwdRequest);
            }
        }
    }

    /**
     * ???????????????
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
     * ????????????
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvHome) { // ?????????
                binding.tvHome.setSelected(true);
                familyPopupWindow.showAsDown(binding.tvHome);
            } else if (viewId == R.id.flList) { // ??????????????????
                switchToActivity(UpDownLoadActivity.class);
            }
        }
    }
}
