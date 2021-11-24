package com.zhiting.clouddisk.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityAddPartitionBinding;
import com.zhiting.clouddisk.dialog.BottomListDialog;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.entity.BottomListBean;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.mine.contract.AddPartitionContract;
import com.zhiting.clouddisk.mine.presenter.AddPartitionPresenter;
import com.zhiting.clouddisk.request.AddPartitionRequest;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加/编辑分区
 */
public class AddPartitionActivity extends BaseMVPDBActivity<ActivityAddPartitionBinding, AddPartitionContract.View, AddPartitionPresenter> implements  AddPartitionContract.View{

    /**
     * 为空 添加
     * 不为空 是编辑
     */
   private DiskBean diskBean;
   private String poolName;

    private BottomListDialog bottomListDialog;
    private String defaultUnit = UiUtil.getString(R.string.mine_gb); // 默认单位

    private ConfirmDialog mConfirmDialog;  // 确定弹窗
    private long originalCapacity; // 初始大小
    private String capacityStr;


    @Override
    public int getLayoutId() {
        return R.layout.activity_add_partition;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initConfirmDialog();
        setTvSaveStatus();
        setEditListener(binding.etName);
        setEditListener(binding.etCapacity);
        setEditListener(binding.etMBCapacity);

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        diskBean = (DiskBean) intent.getSerializableExtra("diskBean");
        binding.tvTitle.setText(diskBean == null ? UiUtil.getString(R.string.mine_add_partition) : UiUtil.getString(R.string.mine_edit_partition));
        binding.tvDel.setVisibility(diskBean != null ? View.VISIBLE : View.GONE);
        poolName = intent.getStringExtra("poolName");
        defaultUnit = intent.getStringExtra("defaultUnit");
        if (diskBean!=null) {
            binding.etName.setText(diskBean.getName());
            originalCapacity = diskBean.getCapacity();
            String capacity = FileUtil.getReadableFileSize(diskBean.getCapacity());
            binding.etCapacity.setText(capacity.substring(0, capacity.length() - 2));
            if (unitIsMB()){
                long mbCapacity = Long.parseLong(FileUtil.getReadableFileSizeWithoutUnit(diskBean.getCapacity()));
                binding.etMBCapacity.setText((mbCapacity/4)+"");
            }
        }
        binding.tvUnit.setText(defaultUnit);
        checkUnit();
        initBottomListDialog();
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
     * 设置文本监听
     * @param editText
     */
    private void setEditListener(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText == binding.etCapacity || editText == binding.etMBCapacity){
                    String text = s.toString();
                    int len = s.toString().length();
                    if (len > 0 && (text.startsWith("0")||text.startsWith("."))) {
                        editText.setText("");
                        if (editText == binding.etMBCapacity){ // 单位是mb的输入框
                            binding.tvResult.setText("");
                        }
                        return;
                    }
                    if (editText == binding.etMBCapacity){  // 单位是mb的输入框
                        String mbText = binding.etMBCapacity.getText().toString().trim();
                        if (TextUtils.isEmpty(mbText)){
                            binding.tvResult.setText("");
                        }else {
                            long result = Long.parseLong(mbText) * 4;
                            binding.tvResult.setText(String.valueOf(result));
                        }
                    }
                }
                setTvSaveStatus();
            }
        });
    }

    /**
     * 设置保存按钮状态
     */
    private void setTvSaveStatus(){
        String strName = binding.etName.getText().toString().trim();
        String strCapacity = binding.etCapacity.getText().toString().trim();
        String strMBCapacity = binding.etMBCapacity.getText().toString().trim();
        boolean enabled = !TextUtils.isEmpty(strName) && (unitIsMB() ? !TextUtils.isEmpty(strMBCapacity) : !TextUtils.isEmpty(strCapacity));
        binding.tvSave.setAlpha(enabled ? 1 : 0.5f);
        binding.tvSave.setEnabled(enabled);
    }

    /**
     * 删除确认弹窗
     */
    private void showRemoveDialog(){
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.getInstance(UiUtil.getString(R.string.mine_remove_confirm), UiUtil.getString(R.string.mine_remove_partition_content),
                UiUtil.getString(R.string.mine_remove_tips), R.color.color_ff0000, "", UiUtil.getString(R.string.mine_sure_remove));
        centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                PoolNameRequest poolNameRequest = new PoolNameRequest(poolName);
                mPresenter.removePartition(Constant.scope_token, diskBean.getName(), poolNameRequest);
                centerAlertDialog.dismiss();
            }
        });
        centerAlertDialog.show(this);
    }

    /**
     * 初始化底部单位选择弹窗
     */
    private void initBottomListDialog(){
        List<BottomListBean> bottomListData = new ArrayList<>();
        bottomListData.add(new BottomListBean(UiUtil.getString(R.string.mine_mb), defaultUnit.equals(UiUtil.getString(R.string.mine_mb))));
        bottomListData.add(new BottomListBean(UiUtil.getString(R.string.mine_gb), defaultUnit.equals(UiUtil.getString(R.string.mine_gb))));
        bottomListData.add(new BottomListBean(UiUtil.getString(R.string.mine_tb), defaultUnit.equals(UiUtil.getString(R.string.mine_tb))));
        bottomListDialog = BottomListDialog.getInstance(bottomListData);
        bottomListDialog.setItemSelectedListener(new BottomListDialog.OnItemSelectedListener() {
            @Override
            public void onSelected(BottomListBean bottomListBean, int position) {
                defaultUnit = bottomListBean.getName();
                binding.tvUnit.setText(bottomListBean.getName());
                checkUnit();
                setTvSaveStatus();
            }
        });
    }

    /**
     * 根据单位显示不同的输入
     */
    private void checkUnit(){
        if (unitIsMB()){ // 如果单位是mb
            binding.llMB.setVisibility(View.VISIBLE);
            binding.tvTips.setVisibility(View.VISIBLE);
            binding.etCapacity.setVisibility(View.GONE);
        }else {
            binding.etCapacity.setVisibility(View.VISIBLE);
            binding.llMB.setVisibility(View.GONE);
            binding.tvTips.setVisibility(View.GONE);
        }
    }

    /**
     * 判断单位是否 MB
     * @return
     */
    private boolean unitIsMB(){
        if (defaultUnit.equals(UiUtil.getString(R.string.mine_mb))){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 添加分区成功
     */
    @Override
    public void addPartitionSuccess() {
//        ToastUtil.show(UiUtil.getString(R.string.mine_add_success));
        Bundle bundle = new Bundle();
        bundle.putString("content", UiUtil.getString(R.string.mine_saving_partition));
        mConfirmDialog.setArguments(bundle);
        mConfirmDialog.show(this);

    }

    /**
     * 添加分区失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void addPartitionFail(int errorCode, String msg) {

    }

    /**
     * 修改成功
     */
    @Override
    public void modifyPartitionSuccess() {
        long afterModifyCapacity = FileUtil.convert2B(Long.parseLong(capacityStr), binding.tvUnit.getText().toString());
        if (afterModifyCapacity == originalCapacity){ // 容量没有修改
            ToastUtil.show(UiUtil.getString(R.string.mine_modify_success));
            finish();
        }else {
            Bundle bundle = new Bundle();
            bundle.putString("content", UiUtil.getString(R.string.mine_saving_partition));
            mConfirmDialog.setArguments(bundle);
            mConfirmDialog.show(this);
        }
//        Bundle bundle = new Bundle();
//        bundle.putString("content", UiUtil.getString(R.string.mine_saving_partition));
//        mConfirmDialog.setArguments(bundle);
//        mConfirmDialog.show(this);
//        ToastUtil.show(UiUtil.getString(R.string.mine_modify_success));
//        finish();
    }

    /**
     * 修改失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void modifyPartitionFail(int errorCode, String msg) {

    }

    /**
     * 删除成功
     */
    @Override
    public void removePartitionSuccess() {
//        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
//        finish();
        Bundle bundle = new Bundle();
        bundle.putString("content", UiUtil.getString(R.string.mine_deleting_partition));
        mConfirmDialog.setArguments(bundle);
        mConfirmDialog.show(this);
    }

    /**
     * 删除失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void removePartitionFail(int errorCode, String msg) {

    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvDel){ // 删除
                showRemoveDialog();
            }else if (viewId == R.id.tvUnit){ // 单位
                if (bottomListDialog!=null && !bottomListDialog.isShowing()){
                    bottomListDialog.show(AddPartitionActivity.this);
                }
            }else if (viewId == R.id.tvSave){ // 保存
                 capacityStr = unitIsMB() ? binding.tvResult.getText().toString().trim() : binding.etCapacity.getText().toString().trim();
                long capacity = Long.parseLong(capacityStr);
                if (diskBean == null) {  // 添加
                    AddPartitionRequest addPartitionRequest = new AddPartitionRequest(binding.etName.getText().toString(), capacity, defaultUnit, poolName);
                    mPresenter.addPartition(Constant.scope_token, addPartitionRequest);
                }else {  // 更新
                    ModifyPartitionRequest modifyPartitionRequest = new ModifyPartitionRequest(binding.etName.getText().toString(), capacity, defaultUnit, poolName);
                    mPresenter.modifyPartition(Constant.scope_token, diskBean.getName(), modifyPartitionRequest);
                }
            }
        }
    }
}