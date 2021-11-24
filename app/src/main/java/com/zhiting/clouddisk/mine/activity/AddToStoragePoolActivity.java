package com.zhiting.clouddisk.mine.activity;

import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.StoragePoolMulAdapter;
import com.zhiting.clouddisk.databinding.ActivityAddToStoragePoolBinding;
import com.zhiting.clouddisk.dialog.AddStoragePoolDialog;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.AddToStoragePoolContract;
import com.zhiting.clouddisk.mine.presenter.AddToStoragePoolPresenter;
import com.zhiting.clouddisk.request.AddStoragePoolRequest;
import com.zhiting.clouddisk.request.CreateStoragePoolRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.MToast;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 添加到存储池
 */
public class AddToStoragePoolActivity extends BaseMVPDBActivity<ActivityAddToStoragePoolBinding, AddToStoragePoolContract.View, AddToStoragePoolPresenter> implements AddToStoragePoolContract.View {

    private StoragePoolMulAdapter storagePoolMulAdapter;

    private AddStoragePoolDialog addStoragePoolDialog;
    private ConfirmDialog mConfirmDialog;  // 确定弹窗

    private String diskName;
    private long capacity;
    private String createName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_to_storage_pool;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStoragePoolList();
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvPool();
        initConfirmDialog();
        initStoragePoolDialog();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        diskName = intent.getStringExtra("diskName");
        capacity = intent.getLongExtra("capacity", 0);

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
     * 初始化列表
     */
    private void initRvPool(){
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        binding.rvPool.addItemDecoration(spacesItemDecoration);
        binding.rvPool.setLayoutManager(new GridLayoutManager(this, 2));
        storagePoolMulAdapter = new StoragePoolMulAdapter(null);
        binding.rvPool.setAdapter(storagePoolMulAdapter);

        storagePoolMulAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                StoragePoolDetailBean storagePoolMulBean = storagePoolMulAdapter.getItem(position);
                if (storagePoolMulBean.getItemType() == 1){ // 列表数据
                    for (StoragePoolDetailBean storagePoolDetailBean : storagePoolMulAdapter.getData()){
                        storagePoolDetailBean.setSelected(false);
                    }
                    storagePoolMulBean.setSelected(true);
                    createName = storagePoolMulBean.getName();
                    storagePoolMulAdapter.notifyDataSetChanged();
                }else { // 添加
                    if (addStoragePoolDialog!=null && !addStoragePoolDialog.isShowing())
                    addStoragePoolDialog.show(AddToStoragePoolActivity.this);
                }
            }
        });

    }

    /**
     * 获取存储池列表
     */
    private void getStoragePoolList(){
        mPresenter.getStoragePools(Constant.scope_token);
    }

    /**
     * 初始 添加到新的存储池 弹窗
     */
    private void initStoragePoolDialog(){
        addStoragePoolDialog = AddStoragePoolDialog.getInstance(0, "");
        addStoragePoolDialog.setCompleteListener(new AddStoragePoolDialog.OnCompleteListener() {
            @Override
            public void onComplete(int type, String poolName) {
                List<StoragePoolDetailBean> storagePoolList = storagePoolMulAdapter.getData();
                createName = poolName;
                if (CollectionUtil.isNotEmpty(storagePoolList)){
                    for (StoragePoolDetailBean storagePoolDetailBean : storagePoolList){
                        String storagePoolName = storagePoolDetailBean.getName();
                        if (storagePoolName!=null && storagePoolName.equals(createName)){
                            showToast(UiUtil.getString(R.string.mine_storage_pool_name_not_repeat));
                            return;
                        }
                    }
                }
                CreateStoragePoolRequest createStoragePoolRequest = new CreateStoragePoolRequest(poolName, diskName);
                mPresenter.createStoragePool(Constant.scope_token, createStoragePoolRequest);
            }
        });

    }

    /**
     * 保存提示弹窗
     */
    private void showSaveDialog(String name){
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_add_to), name), UiUtil.getString(R.string.mine_storage_pool_save_ask),
                UiUtil.getString(R.string.mine_storage_pool_save_alert), R.color.color_ff0000,"", "");
        centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                addToStoragePool(name);
                centerAlertDialog.dismiss();
            }
        });
        centerAlertDialog.show(this);
    }

    /**
     * 获取存储池列表成功
     * @param storagePoolListBean
     */
    @Override
    public void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean) {
        if (storagePoolListBean!=null){
            List<StoragePoolDetailBean> storageList = storagePoolListBean.getList();
            List<StoragePoolDetailBean> data = new ArrayList<>();
            binding.tvSave.setVisibility(CollectionUtil.isNotEmpty(storageList) ? View.VISIBLE : View.GONE);
            for (StoragePoolDetailBean spdb : storageList){
                if (spdb.getName()!=null && !spdb.getName().equals(Constant.SYSTEM_POOL)) {  // 如果不是系统存储则添加到列表
                    spdb.setItemType(StoragePoolDetailBean.POOL);
                    data.add(spdb);
                }
            }
            data.add(new StoragePoolDetailBean(StoragePoolDetailBean.ADD));
            storagePoolMulAdapter.setNewData(data);
        }
    }

    @Override
    public void showError(int errorCode, String msg) {

    }

    /**
     * 获取存储池列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getStoragePoolsFail(int errorCode, String msg) {
        showToast(msg);
    }

    /**
     * 创建存储池成功
     */
    @Override
    public void createStoragePoolSuccess() {
        if (addStoragePoolDialog!=null && addStoragePoolDialog.isShowing()){
            addStoragePoolDialog.dismiss();
        }
        ToastUtil.show(UiUtil.getString(R.string.mine_add_success));
        finish();
    }

    /**
     * 创建存储池失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void createStoragePoolFail(int errorCode, String msg) {
        if (errorCode == 205){
            showAddFailDialog();
        }else {
            showToast(msg);
        }
    }

    /**
     * 添加到存储池成功
     */
    @Override
    public void addToStoragePoolSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_add_success));
        finish();
    }

    /**
     * 添加到存储池失败
     */
    @Override
    public void addToStoragePoolFail(int errorCode, String msg) {
        if (errorCode == 205){
            showAddFailDialog();
        }else {
            showToast(msg);
        }
    }

    private void showToast(String msg){
        MToast toast = MToast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0)
                .show();
    }

    /**
     * 添加到存储池
     */
    private void addToStoragePool(String poolName){
        AddStoragePoolRequest addStoragePoolRequest = new AddStoragePoolRequest(poolName, diskName);
        addStoragePoolRequest.setPool_name(poolName);
        mPresenter.addToStoragePool(Constant.scope_token, addStoragePoolRequest);
    }

    /**
     * 显示失败弹窗
     */
    private void showAddFailDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("content", StringUtil.getStringFormat(UiUtil.getString(R.string.mine_add_to_storage_pool_fail), FileUtil.getReadableFileSize(capacity), createName));
        mConfirmDialog.setArguments(bundle);
        mConfirmDialog.show(this);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvSave){  // 保存
                StoragePoolDetailBean storagePoolDetailBean = storagePoolMulAdapter.getSelectedData();
                if (storagePoolDetailBean == null){  // 未选择
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_choose_storage_pool));
                    return;
                }
                if (storagePoolDetailBean.getUse_capacity()>0){  // 已有数据
                    showSaveDialog(storagePoolDetailBean.getName()); // 弹窗提示
                }else { // 直接添加
                    addToStoragePool(storagePoolDetailBean.getName());
                }

            }
        }
    }
}