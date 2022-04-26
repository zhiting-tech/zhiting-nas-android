package com.zhiting.clouddisk.share.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.HomeFileAdapter;
import com.zhiting.clouddisk.databinding.FragmentShareBinding;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.event.CancelFileOperateEvent;
import com.zhiting.clouddisk.event.ChangeHomeEvent;
import com.zhiting.clouddisk.event.OperateFileEvent;
import com.zhiting.clouddisk.event.RefreshAuthEvent;
import com.zhiting.clouddisk.event.RefreshDataEvent;
import com.zhiting.clouddisk.event.UploadDownloadEvent;
import com.zhiting.clouddisk.home.activity.FileDetailActivity;
import com.zhiting.clouddisk.home.activity.UpDownLoadActivity;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.share.contract.ShareContract;
import com.zhiting.clouddisk.share.presenter.SharePresenter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 共享文件
 */
public class ShareFragment extends BaseMVPDBFragment<FragmentShareBinding, ShareContract.View, SharePresenter> implements ShareContract.View {

    private HomeFileAdapter homeFileAdapter;

    private OnRefreshListener refreshLoadMoreListener = refreshLayout -> getData(false);

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        binding.setHandler(new OnClickHandler());
        initRv();
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void lazyLoad() {
        getData(true);
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
    protected void familyPopupWindowDismiss() {
        super.familyPopupWindowDismiss();
        binding.tvHome.setSelected(false);
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        binding.rrv.finishRefresh(true);
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        homeFileAdapter = new HomeFileAdapter(0, false);
        binding.rrv.setEnableLoadable(false);
        binding.rrv.setAdapter(homeFileAdapter)
                .setOnRefreshListener(refreshLoadMoreListener);
        homeFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            FileBean fileBean = homeFileAdapter.getItem(position);
            if (fileBean.getRead() == 1) {  // 有读权限
                if (fileBean.getType() == 0 && homeFileAdapter.getSelectedSize() <= 0) {
                    Bundle bundle = new Bundle();
                    List<FileBean> fileData = new ArrayList<>();
                    fileData.add(new FileBean(UiUtil.getString(R.string.home_share_folder), 0));
                    fileData.add(fileBean);
                    bundle.putInt(Constant.FROM, TextUtils.isEmpty(fileBean.getFrom_user()) ? 0 : 1);
                    bundle.putSerializable("navigation", (Serializable) fileData);
                    switchToActivity(FileDetailActivity.class, bundle);
                }
            } else {
                ToastUtil.show(UiUtil.getString(R.string.mine_without_read_permission));
            }
        });

        homeFileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivSelected) {
                FileBean fileBean = homeFileAdapter.getItem(position);
                fileBean.setSelected(!fileBean.isSelected());
                homeFileAdapter.notifyItemChanged(position);
                EventBus.getDefault().post(new OperateFileEvent(homeFileAdapter.isOnlyFolder(), homeFileAdapter.getSelectedData()));
                binding.rrv.setRefreshAndLoadMore(homeFileAdapter.getSelectedSize() <= 0);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            uploadDownCount();
            LogUtil.e("onHiddenChanged2");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        uploadDownCount();
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
        if (homeFileAdapter != null && CollectionUtil.isNotEmpty(homeFileAdapter.getData())){
            getData( false);
        }
    }

    /**
     * 家庭改变之后刷新数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangeHomeEvent event) {
        binding.rrv.setVisibility(View.GONE);
        if (isVisible()) {
            getData(true);
        }
    }

    private void uploadDownCount() {
        int fileCount = GonetUtil.getUnderwayFileCount()+ GonetUtil.getBackupFileCount();
        LogUtil.e("onMessageEvent2==" + fileCount);
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
     * 取消文件操作
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CancelFileOperateEvent event) {
        if (homeFileAdapter != null) {
            if (CollectionUtil.isNotEmpty(homeFileAdapter.getData())) {
                for (FileBean fileBean : homeFileAdapter.getData()) {
                    fileBean.setSelected(false);
                }
                homeFileAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 获取文件成功
     *
     * @param fileListBean
     */
    @Override
    public void getFilesSuccess(FileListBean fileListBean) {
        if (fileListBean != null) {
            List<FileBean> files = fileListBean.getList();
            homeFileAdapter.setNewData(files);
            binding.rrv.finishRefresh(true);
            if (binding.rrv.getVisibility() != View.VISIBLE) {
                binding.rrv.setVisibility(View.VISIBLE);
            }
            binding.rrv.showEmptyView(CollectionUtil.isEmpty(files));
        }
    }

    @Override
    protected void selectedHome(AuthBackBean authBackBean) {
        super.selectedHome(authBackBean);
        binding.tvHome.setText(authBackBean.getHomeCompanyBean().getName());
        getData(true);
    }

    /**
     * 获取文件失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFilesFail(int errorCode, String msg) {

    }

    @Override
    public void showError(int errorCode, String msg) {
        super.showError(errorCode, msg);
        binding.rrv.finishRefresh(false);
    }

    /**
     * 获取数据
     *
     * @param showLoading
     */
    public void getData(boolean showLoading) {
        mPresenter.getShareFolders(Constant.scope_token, showLoading);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.flUpDownload) {
                switchToActivity(UpDownLoadActivity.class);
            } else if (viewId == R.id.tvHome) { // 我的家
                binding.tvHome.setSelected(true);
                familyPopupWindow.showAsDown(binding.tvHome);
            }
        }
    }
}
