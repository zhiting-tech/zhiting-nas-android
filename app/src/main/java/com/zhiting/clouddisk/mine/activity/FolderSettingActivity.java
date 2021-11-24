package com.zhiting.clouddisk.mine.activity;


import android.text.TextUtils;
import android.view.View;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityFolderSettingBinding;
import com.zhiting.clouddisk.dialog.ChoosePoolPartitionDialog;
import com.zhiting.clouddisk.entity.mine.FolderSettingBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.FolderSettingContract;
import com.zhiting.clouddisk.mine.presenter.FolderSettingPresenter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.util.List;

/**
 * 文件夹设置
 */
public class FolderSettingActivity extends BaseMVPDBActivity<ActivityFolderSettingBinding, FolderSettingContract.View, FolderSettingPresenter> implements FolderSettingContract.View {

    private ChoosePoolPartitionDialog choosePoolPartitionDialog;
    private String mPoolName; // 存储池名称
    private String mPartitionName;  // 存储池分区名称

    @Override
    public int getLayoutId() {
        return R.layout.activity_folder_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        mPresenter.getSettingData(Constant.scope_token);
    }


    /**
     * 选择分区弹窗
     */
    private void initChoosePartitionDialog(List<StoragePoolDetailBean> data){
        choosePoolPartitionDialog = ChoosePoolPartitionDialog.getInstance(data);
        choosePoolPartitionDialog.setPoolsPartitionListener(new ChoosePoolPartitionDialog.OnPoolsPartitionListener() {
            @Override
            public void onSelected(String poolName, String partitionName) {
                mPoolName = poolName;
                mPartitionName = partitionName;
                binding.tvPartition.setText(mPoolName+"-"+mPartitionName);
            }
        });
        choosePoolPartitionDialog.show(this);
    }

    /**
     * 获取设置数据成功
     * @param folderSettingBean
     */
    @Override
    public void getSettingDataSuccess(FolderSettingBean folderSettingBean) {
        if (folderSettingBean!=null){
            mPoolName = folderSettingBean.getPool_name();
            mPartitionName = folderSettingBean.getPartition_name();
            binding.tvPartition.setText(mPoolName+"-"+mPartitionName);
            binding.sw.setChecked(folderSettingBean.getIs_auto_del() == 1);
        }
    }

    /**
     * 获取设置数据失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSettingDataFail(int errorCode, String msg) {

    }

    /**
     * 获取存储池列表成功
     * @param storagePoolListBean
     */
    @Override
    public void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean) {
        if (storagePoolListBean!=null){
            initChoosePartitionDialog(storagePoolListBean.getList());
        }
    }

    /**
     * 获取存储池列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getStoragePoolsFail(int errorCode, String msg) {

    }

    /**
     * 保存文件夹设置成功
     */
    @Override
    public void saveFolderSettingDataSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_save_success));
        finish();
    }

    /**
     * 保存文件夹设置失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void saveFolderSettingDataFail(int errorCode, String msg) {

    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvSave){ // 保存
                if (TextUtils.isEmpty(mPoolName) || TextUtils.isEmpty(mPartitionName)){
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_choose_save_partition));
                    return;
                }
                FolderSettingBean folderSettingPost = new FolderSettingBean(mPoolName, mPartitionName, binding.sw.isChecked() ? 1 : 0);
                mPresenter.saveFolderSettingData(Constant.scope_token, folderSettingPost);
            }else if (viewId == R.id.tvPartition){ // 存储分区
                if (choosePoolPartitionDialog == null){
                    mPresenter.getStoragePools(Constant.scope_token);
                }else {
                    if (!choosePoolPartitionDialog.isShowing())
                        choosePoolPartitionDialog.show(FolderSettingActivity.this);
                }
            }
        }
    }
}