package com.zhiting.clouddisk.mine.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.DiskAdapter;
import com.zhiting.clouddisk.adapter.StoragePoolAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityStoragePoolListBinding;
import com.zhiting.clouddisk.dialog.HardDiskDialog;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.entity.mine.DiskListBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.mine.contract.StoragePoolListContract;
import com.zhiting.clouddisk.mine.presenter.StoragePoolListPresenter;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * 存储管理（存储池列表）
 */
public class StoragePoolListActivity extends BaseMVPDBActivity<ActivityStoragePoolListBinding, StoragePoolListContract.View, StoragePoolListPresenter> implements StoragePoolListContract.View {

    private DiskAdapter diskAdapter; // 闲置硬盘
    private StoragePoolAdapter storagePoolAdapter; // 存储池列表
    private boolean mRefresh; // 是否刷新
    private int page; // 当前页码，不填默认全部数据

    @Override
    public int getLayoutId() {
        return R.layout.activity_storage_pool_list;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvDisk(); // 闲置硬盘
        initRvPool(); // 存储池
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                getStoragePoolData(false, false);
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                getStoragePoolData(true, false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStoragePoolData(true, true);
    }

    /**
     * 闲置硬盘
     */
    private void initRvDisk() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvDisk.setLayoutManager(layoutManager);
        diskAdapter = new DiskAdapter();
        binding.rvDisk.setAdapter(diskAdapter);

        diskAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DiskBean diskBean = diskAdapter.getItem(position);
            if (view.getId() == R.id.tvAdd) {
                Bundle bundle = new Bundle();
                bundle.putString("diskName", diskBean.getName());
                bundle.putLong("capacity", diskBean.getCapacity());
                switchToActivity(AddToStoragePoolActivity.class, bundle);
            }
        });
    }

    /**
     * 存储池
     */
    private void initRvPool() {
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        binding.rvPool.addItemDecoration(spacesItemDecoration);
        binding.rvPool.setLayoutManager(new GridLayoutManager(this, 2));
        storagePoolAdapter = new StoragePoolAdapter();
        binding.rvPool.setAdapter(storagePoolAdapter);

        storagePoolAdapter.setOnItemClickListener((adapter, view, position) -> {
            StoragePoolDetailBean storagePoolDetailBean = storagePoolAdapter.getItem(position);
            if (TextUtils.isEmpty(storagePoolDetailBean.getStatus())) {  // 没有异步状态才可跳转
                Bundle bundle = new Bundle();
                bundle.putString("name", storagePoolDetailBean.getName());
                switchToActivity(StoragePoolDetailActivity.class, bundle);
            }
        });

        storagePoolAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            StoragePoolDetailBean storagePoolDetailBean = storagePoolAdapter.getItem(position);
            int viewId = view.getId();
            if (viewId == R.id.ivDot) {  // 显示物理硬盘个数
                showHardDiskDialog(storagePoolAdapter.getItem(position).getPv());
            } else if (viewId == R.id.tvRetry) {  // 重试
                String status = storagePoolDetailBean.getStatus();
                if (status != null) {
                    if (status.equals(Constant.STORAGE_POOL_DELETE_FAIL)) { // 删除失败
                        mPresenter.restartTask(Constant.scope_token, storagePoolDetailBean.getTask_id());
                    }
                }
            }
        });
    }

    /**
     * 访问接口，获取数据
     *
     * @param showLoading
     */
    private void getStoragePoolData(boolean refresh, boolean showLoading) {
        mRefresh = refresh;
        page = refresh ? 0 : page + 1;
        map.clear();
        map.put(Constant.PAGE_KEY, String.valueOf(page * Constant.pageSize));
        map.put(Constant.PAGE_SIZE_KEY, String.valueOf(Constant.pageSize));
        if (refresh) {
            mPresenter.getDisks(Constant.scope_token);
        }
        mPresenter.getStoragePools(Constant.scope_token, map, showLoading);
    }

    /**
     * 显示物理硬盘个数
     */
    private void showHardDiskDialog(List<DiskBean> diskData) {
        HardDiskDialog hardDiskDialog = HardDiskDialog.getInstance(diskData);
        hardDiskDialog.show(this);
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        binding.refreshLayout.finishRefresh();
        binding.refreshLayout.finishLoadMore();
    }

    /**
     * 获取存储池列表成功
     *
     * @param storagePoolListBean
     */
    @Override
    public void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean) {
        setFinishRefreshLoad();
        if (storagePoolListBean != null) {
            List<StoragePoolDetailBean> storagePoolsData = storagePoolListBean.getList();
            if (mRefresh) { // 刷新
                binding.tvPoolTitle.setVisibility(CollectionUtil.isNotEmpty(storagePoolsData) ? View.VISIBLE : View.GONE);
                setPoolDataNull(CollectionUtil.isEmpty(storagePoolsData));
                storagePoolAdapter.setNewData(storagePoolsData);
            } else { // 加载更多
                storagePoolAdapter.addData(storagePoolsData);
            }
            PagerBean pagerBean = storagePoolListBean.getPager();
            if (pagerBean != null) {
                if (!pagerBean.isHas_more())
                    binding.refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    /**
     * 获取存储池列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getStoragePoolsFail(int errorCode, String msg) {
        setFinishRefreshLoad();
    }

    /**
     * 闲置硬盘列表成功
     *
     * @param diskListBean
     */
    @Override
    public void getDisksSuccess(DiskListBean diskListBean) {
        if (diskListBean != null) {
            List<DiskBean> diskList = diskListBean.getList();
            if (CollectionUtil.isNotEmpty(diskList)) {
                binding.tvDiskCount.setVisibility(View.VISIBLE);
                binding.tvDiskCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_find_usable_disk), diskList.size()));
                binding.rvDisk.setVisibility(View.VISIBLE);
            } else {
                binding.tvDiskCount.setVisibility(View.GONE);
                binding.rvDisk.setVisibility(View.GONE);
            }
            diskAdapter.setNewData(diskList);
        }
    }

    /**
     * 闲置硬盘列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDisksFail(int errorCode, String msg) {

    }

    /**
     * 重试成功
     */
    @Override
    public void restartTaskSuccess() {
        getStoragePoolData(true, false);
    }

    /**
     * 重试失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void restartTaskFail(int errorCode, String msg) {

    }

    /**
     * @param visible
     */
    private void setPoolDataNull(boolean visible) {
        binding.tvTitle.setVisibility(!visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 完成刷新和加载更多
     */
    private void setFinishRefreshLoad() {
        binding.refreshLayout.finishRefresh();
        binding.refreshLayout.finishLoadMore();
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }
        }
    }
}