package com.zhiting.clouddisk.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.MemberAdapter;
import com.zhiting.clouddisk.adapter.ShareFolderAdapter;
import com.zhiting.clouddisk.databinding.ActivityShareFolderBinding;
import com.zhiting.clouddisk.dialog.OperatePermissionDialog;
import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.PermissionUserBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.home.contract.ShareFolderContract;
import com.zhiting.clouddisk.home.presenter.ShareFolderPresenter;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 共享文件夹
 */
public class ShareFolderActivity extends BaseMVPDBActivity<ActivityShareFolderBinding, ShareFolderContract.View, ShareFolderPresenter> implements ShareFolderContract.View {

    private ShareFolderAdapter shareFolderAdapter;
    private MemberAdapter memberAdapter;
    private List<FileBean> folders;
    private OperatePermissionDialog operatePermissionDialog; // 编辑权限弹窗

    private boolean originalWrite; // 共享者的写权限
    private boolean originalDel; // 共享者的删权限

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_folder;
    }


    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvFolder(); // 文件夹
        initRvMember(); // 成员
        initOperatePermissionDialog();
        mPresenter.getMember(Constant.scope_token);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        folders = (List<FileBean>) intent.getSerializableExtra("folder");
        shareFolderAdapter.setNewData(folders);
        originalWrite = intent.getBooleanExtra("originalWrite", false);
        originalDel = intent.getBooleanExtra("originalDel", false);
        binding.tvCount.setText(String.format(UiUtil.getString(R.string.home_share_folder_all), folders.size()));
    }


    /**
     * 文件夹列表
     */
    private void initRvFolder(){
        shareFolderAdapter = new ShareFolderAdapter();
        binding.rvFolder.setAdapter(shareFolderAdapter);
    }

    /**
     * 成员列表
     */
    private void initRvMember(){
        memberAdapter = new MemberAdapter();
        binding.rvMember.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MemberBean.UsersBean usersBean = memberAdapter.getItem(position);
                usersBean.setSelected(!usersBean.isSelected());
                memberAdapter.notifyItemChanged(position);
                binding.tvAll.setSelected(memberAdapter.isAllSelected());
                binding.tvConfirm.setEnabled(!memberAdapter.isAllUnselected());
            }
        });
    }

    /**
     * 编辑成员弹窗
     */
    private void initOperatePermissionDialog(){
        operatePermissionDialog = OperatePermissionDialog.getInstance();
        operatePermissionDialog.setConfirmListener(new OperatePermissionDialog.OnConfirmListener() {
            @Override
            public void onConfirm(int read, int write, int del) {
                List<String> paths = new ArrayList<>();
                for (FileBean fileBean : folders){ // 要共享的文件夹
                    paths.add(fileBean.getPath());
                }
                List<Integer> to_users = new ArrayList<>();
                for (MemberBean.UsersBean usersBean : memberAdapter.getSelectedUsers()){ // 选择的成员
                    to_users.add(usersBean.getUser_id());
                }
                ShareRequest shareRequest = new ShareRequest(to_users, paths, read, write, del, Constant.userName);
                mPresenter.share(Constant.scope_token, shareRequest);
            }
        });
    }

    /**
     * 获取成员列表成功
     * @param memberBean
     */
    @Override
    public void getMemberSuccess(MemberBean memberBean) {
        if (memberBean!=null){
            List<MemberBean.UsersBean> users = memberBean.getUsers();
            for (MemberBean.UsersBean usersBean : users){
                if (usersBean.getUser_id() == Constant.USER_ID){
                    users.remove(usersBean);
                    break;
                }
            }
            memberAdapter.setNewData(memberBean.getUsers());
        }
    }

    /**
     * 获取成员列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getMemberFail(int errorCode, String msg) {

    }

    /**
     * 共享成功
     */
    @Override
    public void shareSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_share_success));
        finish();
    }

    /**
     * 共享失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void shareFail(int errorCode, String msg) {

    }

    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack){ // 返回
                finish();
            }else if (viewId == R.id.tvAll){ // 全选
                binding.tvAll.setSelected(!binding.tvAll.isSelected());
                memberAdapter.setAllSelected(binding.tvAll.isSelected());
                binding.tvConfirm.setEnabled(!memberAdapter.isAllUnselected());
            }else if (viewId == R.id.tvConfirm){  // 确定
                Bundle bundle  = new Bundle();
                bundle.putInt("read", 1); // 读权限
                bundle.putBoolean("originalWrite", originalWrite); // 写权限
                bundle.putBoolean("originalDel", originalDel);  // 删权限
                bundle.putBoolean("checkSaveEnabled", true);
                List<PermissionUserBean> users = new ArrayList<>();
                for (MemberBean.UsersBean usersBean : memberAdapter.getSelectedUsers()) {  // 成员
                    PermissionUserBean permissionUserBean = new PermissionUserBean(usersBean.getNickname(), "");
                    users.add(permissionUserBean);
                }
                bundle.putSerializable("users", (Serializable) users);
                operatePermissionDialog.setArguments(bundle);
                operatePermissionDialog.show(ShareFolderActivity.this);
            }
        }
    }
}