
package com.zhiting.clouddisk.mine.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FolderMemberAdapter;
import com.zhiting.clouddisk.databinding.ActivityCreateFolderBinding;
import com.zhiting.clouddisk.dialog.CenterAlertDialog;
import com.zhiting.clouddisk.dialog.ChoosePoolPartitionDialog;
import com.zhiting.clouddisk.dialog.ConfirmDialog;
import com.zhiting.clouddisk.dialog.OperatePermissionDialog;
import com.zhiting.clouddisk.entity.PermissionUserBean;
import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.entity.mine.FolderBean;
import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.mine.contract.CreateFolderContract;
import com.zhiting.clouddisk.mine.presenter.CreateFolderPresenter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增/修改文件夹
 */
public class CreateFolderActivity extends BaseMVPDBActivity<ActivityCreateFolderBinding, CreateFolderContract.View, CreateFolderPresenter> implements CreateFolderContract.View {

    private FolderMemberAdapter folderMemberAdapter;
    private ChoosePoolPartitionDialog choosePoolPartitionDialog; // 选择存储分区弹窗
    private OperatePermissionDialog operatePermissionDialog; // 编辑权限弹窗
    private CenterAlertDialog partitionChangeTipsDialog; // 分区改变提示弹窗
    private ConfirmDialog partitionTransferDialog;  // 存储分区真正转移弹窗
    private ConfirmDialog insufficientCapacityDialog; // 存储分区容量不足弹窗
    private ConfirmDialog removeSuccessDialog; // 删除成功
    private CenterAlertDialog removeAlertDialog; // 删除询问弹窗

    private String mPoolName; // 存储池名称
    private String mPartitionName;  // 存储池分区名称
    private int is_encrypt = 0; // 是否加密 1加密0不加密
    private int mode=0; // 文件夹类型：1私人文件夹 2共享文件夹

    private FolderBean folderBean; // 修改的话不为空
    private List<StoragePoolDetailBean> storagePoolsData;
    private List<AccessibleMemberBean> members; // 成员

    private int memberPos; // 点击成员的位置

    private FolderDetailBean folderDetail; // 文件夹详情
    private String originalPoolName;  // 原来存储池名称
    private String originalPartitionName;  // 原来存储池分区名称
    private List<StoragePoolDetailBean> mPoolData;

    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_folder;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initOperatePermissionDialog();
        initPartitionChangeTipsDialog();
        initPartitionTransferDialog();
        initInsufficientCapacityDialog();
        initRemoveAlertDialog();
        initRemoveSuccessDialog();
        initRv();
        setEditListener(binding.etName);
        setEditListener(binding.etPwd);
        setEditListener(binding.etConfirmPwd);
        binding.tvSelPartition.setMaxWidth(UiUtil.getScreenWidth()/27*20-UiUtil.dip2px(20));
        binding.tvNo.setSelected(true);


        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;

        binding.clParent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {//认为软键盘将Activity向上推的高度超过了屏幕高度的1/3，就是软键盘弹起了，这个时候隐藏底部的提交按钮
                    //延迟100ms设置不可见性是避免view还没计算好自己的宽高，设置可见不可见性失效。
                    binding.tvSave.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvSave.setVisibility(View.GONE);
                        }
                    }, 100);

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {//认为软键盘将Activity向下推的高度超过了屏幕高度的1/3，就是软键盘隐藏了，这个时候显示底部的提交按钮

                    binding.tvSave.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvSave.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                }
            }
        });
    }

    /**
     * 设置编辑文本框变化监听事件
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
                checkSaveEnabled();
            }
        });
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        folderBean = (FolderBean) intent.getSerializableExtra("folder");
        binding.tvTitle.setText(folderBean == null ? UiUtil.getString(R.string.mine_add_new_folder) : UiUtil.getString(R.string.mine_edit_folder));
        setSaveEnabled(false);
        if (folderBean == null){ // 添加文件夹
            binding.nsv.setVisibility(View.VISIBLE);
            setNoAvailableMember();

        }else { // 修改文件夹
            mPresenter.getFolderDetail(Constant.scope_token, folderBean.getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 选择成员回调
        if (resultCode == RESULT_OK && requestCode == 100){
            List<AccessibleMemberBean> selectedMembers = (List<AccessibleMemberBean>) data.getSerializableExtra("selectedMember");
            if (members == null){
                members = new ArrayList<>();
                folderMemberAdapter.setNewData(members);
            }
            members.addAll(selectedMembers);
            folderMemberAdapter.notifyDataSetChanged();
            setHasMulMember();
            checkSaveEnabled();
        }
    }

    /**
     * 初始化删除弹窗
     */
    private void initRemoveAlertDialog(){
        removeAlertDialog = CenterAlertDialog.getInstance(UiUtil.getString(R.string.mine_remove_confirm), UiUtil.getString(R.string.mine_remove_folder_content),
                UiUtil.getString(R.string.mine_remove_tips), R.color.color_ff0000, "", UiUtil.getString(R.string.mine_sure_remove));
        removeAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                mPresenter.removeFolder(Constant.scope_token, folderBean.getId());
                removeAlertDialog.dismiss();
            }
        });
    }

    /**
     * 选择分区弹窗
     */
    private void initChoosePartitionDialog(List<StoragePoolDetailBean> data){
        if (CollectionUtil.isNotEmpty(data) && !TextUtils.isEmpty(mPoolName)){
            for (StoragePoolDetailBean spdb : data){
                String spdbName = spdb.getName();
                if (spdbName!=null && spdbName.equals(mPoolName)){
                    spdb.setSelected(true);
                    List<DiskBean> disks = spdb.getLv();
                    if (CollectionUtil.isNotEmpty(disks) && !TextUtils.isEmpty(mPartitionName)){
                        for (DiskBean diskBean : disks){
                            String diskName = diskBean.getName();
                            if (diskName!=null && diskName.equals(mPartitionName)){
                                diskBean.setSelected(true);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        choosePoolPartitionDialog = ChoosePoolPartitionDialog.getInstance(data);
        choosePoolPartitionDialog.setPoolsPartitionListener(new ChoosePoolPartitionDialog.OnPoolsPartitionListener() {
            @Override
            public void onSelected(String poolName, String partitionName) {
                mPoolName = poolName;
                mPartitionName = partitionName;
                setPoolName();
            }
        });
        choosePoolPartitionDialog.show(this);
    }


    /**
     * 编辑成员弹窗
     */
    private void initOperatePermissionDialog(){
        operatePermissionDialog = OperatePermissionDialog.getInstance();
        operatePermissionDialog.setConfirmListener(new OperatePermissionDialog.OnConfirmListener() {
            @Override
            public void onConfirm(int read, int write, int del) {
                AccessibleMemberBean accessibleMemberBean = folderMemberAdapter.getItem(memberPos);
                accessibleMemberBean.setRead(read);
                accessibleMemberBean.setWrite(write);
                accessibleMemberBean.setDeleted(del);
                folderMemberAdapter.notifyItemChanged(memberPos);
                operatePermissionDialog.dismiss();
            }
        });
    }

    /**
     * 初始化 分区改变提示弹窗
     */
    private void initPartitionChangeTipsDialog(){
        partitionChangeTipsDialog = CenterAlertDialog.getInstance(UiUtil.getString(R.string.mine_partition_transfer), UiUtil.getString(R.string.mine_partition_change), UiUtil.getString(R.string.mine_partition_change_time),
                R.color.color_ff0000, UiUtil.getString(R.string.common_cancel),  UiUtil.getString(R.string.common_confirm));
        partitionChangeTipsDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                updateFolder();
                partitionChangeTipsDialog.dismiss();
            }
        });
    }

    /**
     * 初始化 存储分区正在转移弹窗
     */
    private void initPartitionTransferDialog(){
        partitionTransferDialog = ConfirmDialog.getInstance();
        partitionTransferDialog.setConfirmListener(new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                partitionTransferDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    /**
     * 初始化 存储分区容量不足弹窗
     */
    private void initInsufficientCapacityDialog(){
        insufficientCapacityDialog = ConfirmDialog.getInstance();
        insufficientCapacityDialog.setConfirmListener(new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                insufficientCapacityDialog.dismiss();
            }
        });
    }

    /**
     * 删除成功弹窗
     */
    private void initRemoveSuccessDialog(){
        removeSuccessDialog = ConfirmDialog.getInstance();
        removeSuccessDialog.setConfirmListener(new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                removeSuccessDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * 初始化成员列表
     */
    private void initRv(){
        binding.rvMember.setLayoutManager(new LinearLayoutManager(this));
        folderMemberAdapter = new FolderMemberAdapter();
        binding.rvMember.setAdapter(folderMemberAdapter);
        folderMemberAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                memberPos = position;
                AccessibleMemberBean accessibleMemberBean = folderMemberAdapter.getItem(position);
                int viewId = view.getId();
                if (viewId == R.id.ivEdit){ // 编辑
                    Bundle bundle  = new Bundle();
                    bundle.putInt("read", accessibleMemberBean.getRead());
                    bundle.putInt("write", accessibleMemberBean.getWrite());
                    bundle.putInt("del", accessibleMemberBean.getDeleted());
                    PermissionUserBean permissionUserBean = new PermissionUserBean(accessibleMemberBean.getNickname(), accessibleMemberBean.getFace());
                    List<PermissionUserBean> users = new ArrayList<>();
                    users.add(permissionUserBean);
                    bundle.putSerializable("users", (Serializable) users);
                    bundle.putBoolean("originalWrite", true);
                    bundle.putBoolean("originalDel", true);
                    operatePermissionDialog.setArguments(bundle);
                    operatePermissionDialog.show(CreateFolderActivity.this);
                }else if (viewId == R.id.ivDel){ // 删除
                    members.remove(position);
                    folderMemberAdapter.notifyItemRemoved(position);
                    if (CollectionUtil.isNotEmpty(members)){
                        binding.tvMember.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_accessible_member_count),  members.size()));
                        binding.tvPrivate.setEnabled(members.size()<2);
                    }else {
                        setNoAvailableMember();
                    }
                    checkSaveEnabled();
                }
            }
        });
    }

    /**
     * 设置私人文件夹还是共享文件夹
     * @param isPrivate
     */
    private void privateFolder(boolean isPrivate){
        binding.tvPrivate.setSelected(isPrivate);
        binding.tvShare.setSelected(!isPrivate);
        if (folderBean == null) {
            binding.viewLineType.setVisibility(isPrivate ? View.VISIBLE : View.INVISIBLE);
            binding.clEncrypt.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            if (isPrivate) {  // 私人文件
                if (is_encrypt == 1) {  // 加密
                    binding.clPwd.setVisibility(View.VISIBLE);
                    binding.clConfirmPwd.setVisibility(View.VISIBLE);
                }
            } else { // 共享文件
                binding.clPwd.setVisibility(View.GONE);
                binding.clConfirmPwd.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 是否加密
     */
    private void whetherEncrypt(boolean encrypt){
        binding.tvYes.setSelected(encrypt);
        binding.tvNo.setSelected(!encrypt);
        binding.viewLineEncrypt.setVisibility(encrypt ? View.VISIBLE : View.INVISIBLE);
        binding.clPwd.setVisibility(encrypt ? View.VISIBLE : View.GONE);
        binding.clConfirmPwd.setVisibility(encrypt ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置成员空视图
     */
    private void setNoAvailableMember(){
        binding.tvMember.setText(UiUtil.getString(R.string.mine_accessible_member));
        binding.ivAdd.setVisibility(View.GONE);
        binding.rvMember.setVisibility(View.GONE);
        binding.llNoMember.setVisibility(View.VISIBLE);
    }

    /**
     * 设置文件类型和是否加密状态
     */
    private void setTypeStatus(){
        binding.tvPrivate.setSelected(mode == 1);
        binding.tvShare.setSelected(mode == 2);
        binding.clEncrypt.setVisibility(View.VISIBLE);
        binding.tvYes.setSelected(is_encrypt == 1);
        binding.tvNo.setSelected(is_encrypt == 0);
        if (mode == 1){  // 私人文件夹
            if (is_encrypt == 1){  // 是加密
                setPrivateDisabled();
            }

        }
        setEncryptDisabled();

    }

    /**
     * 设置私人文件夹不可操作
     */
    private void setPrivateDisabled(){
        binding.tvPrivate.setEnabled(false);
        binding.tvPrivate.setAlpha(0.5f);
        binding.tvShare.setVisibility(View.GONE);
    }

    /**
     * 设置加密类型不可操作
     */
    private void setEncryptDisabled(){
        binding.tvYes.setEnabled(false);
        binding.tvYes.setAlpha(0.5f);
        binding.tvNo.setEnabled(false);
        binding.tvNo.setAlpha(0.5f);
        binding.tvYes.setVisibility(is_encrypt == 1 ? View.VISIBLE : View.GONE);
        binding.tvNo.setVisibility(is_encrypt == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示成员
     */
    private void setHasMulMember(){
        binding.tvMember.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_accessible_member_count), members.size()));
        binding.ivAdd.setVisibility(View.VISIBLE);
        binding.rvMember.setVisibility(View.VISIBLE);
        binding.llNoMember.setVisibility(View.GONE);
    }

    /**
     * 设置存储池名称
     */
    private void setPoolName(){
        binding.tvSelPartition.setText(mPoolName + "-" + mPartitionName);
        binding.ivSel.setSelected(true);
        checkSaveEnabled();
    }

    /**
     * 文件夹详情成功
     * @param folderDetailBean
     */
    @Override
    public void getFolderDetailSuccess(FolderDetailBean folderDetailBean) {
        if (folderDetailBean!=null){
            folderDetail = folderDetailBean;
            binding.tvDel.setVisibility(View.VISIBLE);
            originalPoolName = folderDetailBean.getPool_name();
            mPoolName = folderDetailBean.getPool_name();
            originalPartitionName = folderDetailBean.getPartition_name();
            mPartitionName = folderDetailBean.getPartition_name();
            binding.etName.setText(folderDetailBean.getName() );
            setPoolName();
            is_encrypt = folderDetailBean.getIs_encrypt();
            mode = folderDetailBean.getMode();
            setTypeStatus();
            members = folderDetailBean.getAuth();
            if (CollectionUtil.isEmpty(members)){
                setNoAvailableMember();
            }else {
                setHasMulMember();
            }
            folderMemberAdapter.setNewData(members);
            binding.nsv.setVisibility(View.VISIBLE);
            setSaveEnabled(true);
        }
    }

    /**
     * 文件夹详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFolderDetailFail(int errorCode, String msg) {

    }

    /**
     * 获取存储池列表成功
     * @param storagePoolListBean
     */
    @Override
    public void getStoragePoolsSuccess(StoragePoolListBean storagePoolListBean) {
        if (storagePoolListBean!=null){
            mPoolData = storagePoolListBean.getList();
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
     * 添加文件夹成功
     */
    @Override
    public void addFolderSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_save_success));
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 添加文件夹失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void addFolderFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }


    /**
     * 删除文件夹成功
     */
    @Override
    public void removeFolderSuccess() {
//        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
//        finish();
        if (removeSuccessDialog!=null&&!removeSuccessDialog.isShowing()){
            Bundle bundle = new Bundle();
            bundle.putString("content", UiUtil.getString(R.string.mine_folder_deleting));
            removeSuccessDialog.setArguments(bundle);
            removeSuccessDialog.show(this);
        }
    }

    /**
     * 删除文件夹失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void removeFolderFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 修改文件夹成功
     */
    @Override
    public void updateFolderSuccess() {
        if (!poolPartitionChanged()) {
            ToastUtil.show(UiUtil.getString(R.string.home_save_success));
            setResult(RESULT_OK);
            finish();
        }else {
             Bundle bundle = new Bundle();
             bundle.putString("title", UiUtil.getString(R.string.mine_partition_transfer));
             bundle.putString("content", StringUtil.getStringFormat(UiUtil.getString(R.string.mine_partition_change_backup_refresh), folderDetail.getName(),
                     originalPoolName + "-" + originalPartitionName, mPoolName + "-" + mPartitionName));
             partitionTransferDialog.setArguments(bundle);
             partitionTransferDialog.show(CreateFolderActivity.this);
        }
    }

    /**
     * 修改文件夹失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateFolderFail(int errorCode, String msg) {
        if (errorCode == 20019) {
            Bundle bundle = new Bundle();
            bundle.putString("title", UiUtil.getString(R.string.mine_update_folder_fail_title));
            bundle.putString("content", UiUtil.getString(R.string.mine_update_folder_fail_reason));
            insufficientCapacityDialog.setArguments(bundle);
            insufficientCapacityDialog.show(CreateFolderActivity.this);
        }else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void showError(String msg) {

    }

    /**
     * 保存
     */
    private void save(){
        String folderName = binding.etName.getText().toString().trim();
        if (TextUtils.isEmpty(folderName)){
            ToastUtil.show(UiUtil.getString(R.string.mine_please_input_folder_name));
            return;
        }
        if (TextUtils.isEmpty(binding.tvSelPartition.getText().toString().trim())){
            ToastUtil.show(UiUtil.getString(R.string.mine_please_choose_save_partition));
            return;
        }
        //  如果是添加文件
        String pwd = binding.etPwd.getText().toString().trim();
        String confirmPwd = binding.etConfirmPwd.getText().toString().trim();
        if (folderBean == null){

            // 如果没有选择文件类型
            if (mode == 0){
                ToastUtil.show(UiUtil.getString(R.string.mine_please_choose_type));
                return;
            }

            // 如果是加密文件
            if (is_encrypt == 1){
                // 判断密码是为空
                if (TextUtils.isEmpty(pwd)){
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_input_pwd));
                    return;
                }
                // 判断确认密码是为空
                if (TextUtils.isEmpty(confirmPwd)){
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_input_confirm_pwd));
                    return;
                }

                // 密码是否一致
                if (!pwd.equals(confirmPwd)){
                    ToastUtil.show(UiUtil.getString(R.string.mine_pwd_is_inconsistent_with));
                    return;
                }
            }
        }
        if (CollectionUtil.isEmpty(members)){
            ToastUtil.show(UiUtil.getString(R.string.mine_at_least_choose_one_member));
            return;
        }
        if (folderBean == null){  // 如果是添加文件夹
            FolderDetailBean folderPost = new FolderDetailBean();
            folderPost.setName(folderName);
            folderPost.setPool_name(mPoolName);
            folderPost.setPartition_name(mPartitionName);
            folderPost.setMode(mode);
            folderPost.setIs_encrypt(mode == 1 ? is_encrypt : 0);
            folderPost.setAuth(members);
            // 如果是加密文件
            if (is_encrypt == 1) {
                // 设置密码
                folderPost.setPwd(pwd);
                folderPost.setConfirm_pwd(confirmPwd);
            }
            mPresenter.addFolder(Constant.scope_token, folderPost);
        }else {  // 编辑文件夹
            if (poolPartitionChanged()) {  // 存储池更改
                Bundle bundle = new Bundle();
                bundle.putString("title", UiUtil.getString(R.string.mine_partition_transfer));
                bundle.putString("content", StringUtil.getStringFormat(UiUtil.getString(R.string.mine_partition_change), folderDetail.getName(),
                        originalPoolName + "-" + originalPartitionName, mPoolName + "-" + mPartitionName));
                bundle.putString("tips", UiUtil.getString(R.string.mine_partition_change_time));
                bundle.putString("leftText", UiUtil.getString(R.string.common_cancel));
                bundle.putString("rightText", UiUtil.getString(R.string.common_confirm));
                bundle.putInt("tipsTextColor", R.color.color_ff0000);
                partitionChangeTipsDialog.setArguments(bundle);
                partitionChangeTipsDialog.show(CreateFolderActivity.this);
            }else {  // 存储池没更改
                updateFolder();
            }
        }
    }

    /**
     * 判断存储分配是否修改过
     * @return
     */
    private boolean poolPartitionChanged(){
        return !originalPoolName.equals(mPoolName) || !originalPartitionName.equals(mPartitionName);
    }

    /**
     * 更新文件夹
     */
    private void updateFolder(){
        folderDetail.setName(binding.etName.getText().toString().trim());
        folderDetail.setPool_name(mPoolName);
        folderDetail.setPartition_name(mPartitionName);
        folderDetail.setMode(mode);
        folderDetail.setAuth(members);
        mPresenter.updateFolder(Constant.scope_token, folderBean.getId(), folderDetail);
    }

    /**
     * 校验保存是否可点击
     */
    private void checkSaveEnabled(){
        boolean enabled = !TextUtils.isEmpty(binding.etName.getText().toString().trim()) && !TextUtils.isEmpty(binding.tvSelPartition.getText().toString().trim())
                && mode != 0 && CollectionUtil.isNotEmpty(folderMemberAdapter.getData());;
        if (folderBean == null){  // 新增文件夹
            if (mode == 1){  // 私人文件夹,需选择加密类型
                enabled = enabled && is_encrypt!=-1;
                if (is_encrypt == 1){  // 加密文件夹， 需输入密码和确认密码
                    String etPwd = binding.etPwd.getText().toString().trim();
                    String etConfirmPwd = binding.etConfirmPwd.getText().toString().trim();
                    enabled = enabled && !TextUtils.isEmpty(etPwd) && !TextUtils.isEmpty(etConfirmPwd)
                    && etPwd.length()>5 && etConfirmPwd.length()>5;
                }
            }
        }else { // 编辑文件夹
            enabled = !TextUtils.isEmpty(binding.etName.getText().toString().trim()) && CollectionUtil.isNotEmpty(folderMemberAdapter.getData());
        }
        setSaveEnabled(enabled);
    }

    /**
     * 保存按钮状态
     * @param enabled
     */
    private void setSaveEnabled(boolean enabled){
        binding.tvSave.setEnabled(enabled);
        binding.tvSave.setAlpha(enabled ? 1 : 0.5f);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvPrivate){ // 私有文件
                if (CollectionUtil.isNotEmpty(members)){
                    if (members.size()>1){
                        ToastUtil.show(UiUtil.getString(R.string.mine_only_one));
                        return;
                    }
                }
                mode = 1;
               privateFolder(true);
                checkSaveEnabled();
            }else if (viewId == R.id.tvShare){ // 共享文件
                mode = 2;
                privateFolder(false);
                checkSaveEnabled();
            }else if (viewId == R.id.tvSelPartition || viewId == R.id.ivSel){ // 选择分区
                if (choosePoolPartitionDialog == null){ // 如果选择存储池-存储分区弹窗为空
                    mPresenter.getStoragePools(Constant.scope_token);
                }else {
                    if (!choosePoolPartitionDialog.isShowing())
                    choosePoolPartitionDialog.show(CreateFolderActivity.this);
                }

            }else if (viewId == R.id.ivAdd || viewId == R.id.tvAdd){  // 添加成员
                if (mode == 1 && CollectionUtil.isNotEmpty(members)){  // 如果时私人文件夹且已有成员
                    ToastUtil.show(UiUtil.getString(R.string.mine_only_one));
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("mode", mode);
                bundle.putSerializable("members", (Serializable) members);
                switchToActivityForResult(AddMemberActivity.class, bundle, 100);
            }else if (viewId == R.id.tvYes){  // 是加密
                is_encrypt = 1;
                whetherEncrypt(true);
                checkSaveEnabled();
            }else if (viewId == R.id.tvNo){  // 否加密
                is_encrypt = 0;
                whetherEncrypt(false);
                checkSaveEnabled();
            }else if (viewId == R.id.tvSave){  // 保存
                save();
            }else if (viewId == R.id.tvDel){  // 删除
                if (removeAlertDialog!=null && !removeAlertDialog.isShowing()){
                    removeAlertDialog.show(CreateFolderActivity.this);
                }
            }
        }
    }
}