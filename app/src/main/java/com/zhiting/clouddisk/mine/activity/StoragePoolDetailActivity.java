package com.zhiting.clouddisk.mine.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.StoragePoolDetailAdapter;
import com.zhiting.clouddisk.databinding.ActivityStoragePoolDetailBinding;
import com.zhiting.clouddisk.dialog.AddStoragePoolDialog;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.dialog.HardDiskDialog;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.mine.contract.StoragePoolDetailContract;
import com.zhiting.clouddisk.mine.presenter.StoragePoolDetailPresenter;
import com.zhiting.clouddisk.request.ModifyNameRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * 存储池详情
 */
public class StoragePoolDetailActivity extends BaseMVPDBActivity<ActivityStoragePoolDetailBinding, StoragePoolDetailContract.View, StoragePoolDetailPresenter> implements StoragePoolDetailContract.View {

    private StoragePoolDetailAdapter storagePoolDetailAdapter; // 存储池列表

    private String name; // 原来名称
    private String updateName; // 修改后名称
    private String poolId; // 存储池id

    private AddStoragePoolDialog addStoragePoolDialog; // 修改存储存池弹窗
    private StoragePoolDetailBean mStoragePoolDetailBean; // 存储池详情
    private ConfirmDialog mConfirmDialog; // 确认弹窗
    private boolean isSystemPool;

    @Override
    public int getLayoutId() {
        return R.layout.activity_storage_pool_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRv();
        initConfirmDialog();
        initUpdateNameDialog();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        name = intent.getStringExtra("name");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getStoragePoolDetail(true);
    }

    /**
     * 初始化列表
     */
    private void initRv(){
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                getStoragePoolDetail(false);
            }
        });
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        binding.rvPool.addItemDecoration(spacesItemDecoration);
        binding.rvPool.setLayoutManager(new GridLayoutManager(this, 2));

        storagePoolDetailAdapter = new StoragePoolDetailAdapter();
        binding.rvPool.setAdapter(storagePoolDetailAdapter);
        storagePoolDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DiskBean diskBean = storagePoolDetailAdapter.getItem(position);
                if (TextUtils.isEmpty(diskBean.getStatus())) {
                    if (!isSystemPool) {  // 不是存储池才能编辑存储池分区
                        String capacity = FileUtil.getReadableFileSize(diskBean.getCapacity());
                        String defaultUnit = capacity.substring(capacity.length() - 2);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("diskBean", diskBean);
                        bundle.putString("poolName", binding.tvName.getText().toString());
                        bundle.putString("defaultUnit", defaultUnit);
                        switchToActivity(AddPartitionActivity.class, bundle);
                    }
                }
            }
        });

        storagePoolDetailAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                DiskBean diskBean = storagePoolDetailAdapter.getItem(position);
                int viewId = view.getId();
                if (viewId == R.id.tvRetry){ // 重试
                    mPresenter.restartTask(Constant.scope_token, diskBean.getTask_id());
                }else if (viewId == R.id.tvCancel){ // 取消
                    mPresenter.removeTask(Constant.scope_token, diskBean.getTask_id());
                }
            }
        });
    }

    /**
     * 显示修改名称弹窗
     */
    private void initUpdateNameDialog(){
        addStoragePoolDialog = AddStoragePoolDialog.getInstance(1, binding.tvName.getText().toString().trim());
        addStoragePoolDialog.setCompleteListener(new AddStoragePoolDialog.OnCompleteListener() {
            @Override
            public void onComplete(int type, String poolName) {
                updateName = poolName;
                ModifyNameRequest modifyNameRequest = new ModifyNameRequest(updateName);
                mPresenter.modifyPoolName(Constant.scope_token, name, modifyNameRequest);
            }
        });
    }

    /**
     * 删除确认弹窗
     */
    private void showRemoveDialog(){
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(UiUtil.getString(R.string.mine_remove_confirm), UiUtil.getString(R.string.mine_remove_content),
                UiUtil.getString(R.string.mine_remove_tips), R.color.color_ff0000, "", UiUtil.getString(R.string.mine_sure_remove));
        centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                mPresenter.removePool(Constant.scope_token, binding.tvName.getText().toString());
                centerAlertDialog.dismiss();
            }
        });
        centerAlertDialog.show(this);
    }


    /**
     * 初始化确认弹窗
     */
    private void initConfirmDialog(){
        mConfirmDialog  = ConfirmDialog.getInstance();
        mConfirmDialog.setConfirmListener(new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                finish();
            }
        });
    }

    /**
     * 访问接口，获取存储池详情
     */
    private void getStoragePoolDetail(boolean showLoading){
        mPresenter.getStoragePoolDetail(Constant.scope_token, name, showLoading);
    }

    /**
     * 获取存储池详情成功
     * @param storagePoolDetailBean
     */
    @Override
    public void getStoragePoolDetailSuccess(StoragePoolDetailBean storagePoolDetailBean) {
        binding.refreshLayout.finishRefresh();
        if (storagePoolDetailBean!=null){
            mStoragePoolDetailBean = storagePoolDetailBean;
            poolId = storagePoolDetailBean.getId();
            binding.tvName.setText(storagePoolDetailBean.getName());
            double usedCapacity = storagePoolDetailBean.getUse_capacity();
            double allCapacity = storagePoolDetailBean.getCapacity();
            double availableCapacity = allCapacity - usedCapacity;
            binding.tvSeparable.setText(FileUtil.getReadableFileSize(availableCapacity)+UiUtil.getString(R.string.mine_separable_capacity));
            binding.tvAll.setText(FileUtil.getReadableFileSize(allCapacity)+UiUtil.getString(R.string.mine_all_capacity));
            List<DiskBean> hardDisk = storagePoolDetailBean.getPv(); // 硬盘列表

            int hardDiskCount = CollectionUtil.isNotEmpty(hardDisk) ? hardDisk.size() : 0; // 硬盘个数
            binding.tvCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_hard_disk_count), hardDiskCount));
            int progress = (int) ((usedCapacity/allCapacity)*100); // 容量进度百分比
            isSystemPool = storagePoolDetailBean.getName().equals(Constant.SYSTEM_POOL);  // 是否是系统存储池
            binding.tvDel.setVisibility(isSystemPool ? View.GONE : View.VISIBLE);  // 是系统存储池可删除
            binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, isSystemPool ? 0 : R.drawable.icon_white_edit, 0);  // 是系统存储池不可编辑
            binding.tvCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, isSystemPool ? 0 : R.drawable.icon_white_right_arrow, 0);  // 是系统存储池查看磁盘不可操作
            binding.tvCount.setEnabled(!isSystemPool);// 是系统存储池查看磁盘不可操作
            binding.ivAdd.setVisibility(isSystemPool ? View.GONE : View.VISIBLE); // 是系统存储池不可添加存储分区
            binding.rb.setProgress(progress);
            storagePoolDetailAdapter.setNewData(storagePoolDetailBean.getLv());
            setNullView(CollectionUtil.isEmpty(storagePoolDetailBean.getLv()));
            binding.coordinatorLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取存储池详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getStoragePoolDetailFail(int errorCode, String msg) {

    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        binding.refreshLayout.finishRefresh();
    }

    /**
     * 修改名称成功
     */
    @Override
    public void modifyPoolNameSuccess() {
        binding.tvName.setText(updateName);
        if (addStoragePoolDialog!=null && addStoragePoolDialog.isShowing()){
            addStoragePoolDialog.dismiss();
        }
        ToastUtil.show(UiUtil.getString(R.string.mine_modify_success));
    }

    /**
     * 修改名称失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void modifyPoolNameFail(int errorCode, String msg) {

    }

    /**
     * 删除成功
     */
    @Override
    public void removePoolSuccess() {
//        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
        Bundle bundle = new Bundle();
        bundle.putString("content", UiUtil.getString(R.string.mine_storage_pool_moving));
        mConfirmDialog.setArguments(bundle);
        mConfirmDialog.show(this);
    }

    /**
     * 删除失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void removePoolFail(int errorCode, String msg) {

    }

    /**
     * 删除任务成功
     */
    @Override
    public void removeTaskSuccess() {
        getStoragePoolDetail(false);
    }

    /**
     * 删除失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void removeTaskFail(int errorCode, String msg) {

    }

    /**
     * 重新开始任务成功
     */
    @Override
    public void restartTaskSuccess() {
        getStoragePoolDetail(false);
    }

    /**
     * 重新开始任务失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void restartTaskFail(int errorCode, String msg) {

    }

    /**
     * 空视图
     * @param visible
     */
    private void setNullView(boolean visible){
        binding.rvPool.setVisibility(visible ? View.GONE : View.VISIBLE);
        binding.viewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示物理硬盘个数
     */
    private void showHardDiskDialog(List<DiskBean> diskData){
        HardDiskDialog hardDiskDialog = HardDiskDialog.getInstance(diskData);
        hardDiskDialog.show(this);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvName){  // 修改名称
               if (!mStoragePoolDetailBean.getName().equals(Constant.SYSTEM_POOL)  // 不是系统存储池
                       && addStoragePoolDialog!=null && !addStoragePoolDialog.isShowing()){
                   Bundle args = new Bundle();
                   args.putInt("type", 1);
                   args.putString("name", binding.tvName.getText().toString());
                   addStoragePoolDialog.setArguments(args);
                   addStoragePoolDialog.show(StoragePoolDetailActivity.this);
               }
            }else if (viewId == R.id.tvDel){ // 删除
                showRemoveDialog();
            }else if (viewId == R.id.ivAdd){  // 添加
                Bundle bundle = new Bundle();
                bundle.putString("poolName", name);
                bundle.putString("defaultUnit", UiUtil.getString(R.string.mine_gb));
                switchToActivity(AddPartitionActivity.class, bundle);
            }else if (viewId == R.id.tvCount){  // 硬盘个数
                if (mStoragePoolDetailBean!=null){
                    showHardDiskDialog(mStoragePoolDetailBean.getPv());
                }
            }

        }
    }
}