package com.zhiting.clouddisk.mine.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.MineFolderAdapter;
import com.zhiting.clouddisk.databinding.ActivityFolderBinding;
import com.zhiting.clouddisk.dialog.ChangeFolderPwdDialog;
import com.zhiting.clouddisk.entity.mine.FolderBean;
import com.zhiting.clouddisk.entity.mine.FolderListBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;
import com.zhiting.clouddisk.entity.mine.SettingBean;
import com.zhiting.clouddisk.mine.contract.FolderContract;
import com.zhiting.clouddisk.mine.presenter.FolderPresenter;
import com.zhiting.clouddisk.popup_window.SettingPopupWindow;
import com.zhiting.clouddisk.request.UpdateFolderPwdRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件夹
 */
public class FolderActivity extends BaseMVPDBActivity<ActivityFolderBinding, FolderContract.View, FolderPresenter> implements FolderContract.View {

    private MineFolderAdapter mineFolderAdapter;
    private SettingPopupWindow settingPopupWindow;
    private ChangeFolderPwdDialog changeFolderPwdDialog;

    private boolean mRefresh; // 是否刷新
    private int page; // 当前页码，不填默认全部数据

    private long folderId; // 文件夹id，用于修改密码

    @Override
    public int getLayoutId() {
        return R.layout.activity_folder;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initSettingView();
        initChangeFolderPwdDialog();
        initRv();
        getFoldersData(true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100){
            binding.rrv.resetNoMoreData();
            getFoldersData(true, true);
        }
    }

    /**
     * 初始化列表
     */
    private void initRv(){
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);

        mineFolderAdapter = new MineFolderAdapter();
        binding.rrv.setItemDecoration(spacesItemDecoration)
                .setAdapter(mineFolderAdapter)
                .setOnRefreshAndLoadMoreListener(new OnRefreshLoadMoreListener() {
                    @Override
                    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                        getFoldersData(false, false);
                    }

                    @Override
                    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                        getFoldersData(true, false);
                    }
                });

        mineFolderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FolderBean folderBean = mineFolderAdapter.getItem(position);
                if (TextUtils.isEmpty(folderBean.getStatus())) {  // 没有任务状态时，才能进入文件夹详情
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("folder", folderBean);
                    switchToActivityForResult(CreateFolderActivity.class, bundle, 100);
                }
            }
        });

        mineFolderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                FolderBean folderBean = mineFolderAdapter.getItem(position);
                folderId = folderBean.getId();
                if (viewId == R.id.ivThreeDot){  // 三个点(修改密码弹窗)
                    if (folderBean.getIs_encrypt() == 1) {  // 加密情况下才弹窗
                        settingPopupWindow.showAsDropDown(view, -settingPopupWindow.getWidth() + view.getWidth() * 2 - UiUtil.getDimens(R.dimen.dp_10), 0);
                    }
                }else if (viewId == R.id.tvOperate){ // 重试/确定
                    String status = folderBean.getStatus();
                    if (status!=null) {
                        if (status.equals(Constant.FOLDER_UPDATE_FAIL)) {  // 修改失败时确定
                            mPresenter.removeTask(Constant.scope_token, folderBean.getTask_id());
                        } else if (status.equals(Constant.FOLDER_DELETE_FAIL)) {  // 删除失败时重试
                            mPresenter.restartTask(Constant.scope_token, folderBean.getTask_id());
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置弹窗的数据
     */
    private void initSettingView(){
        settingPopupWindow = new SettingPopupWindow(this);
        List<SettingBean> settingBeans = new ArrayList<>();
        settingBeans.add(new SettingBean(0, UiUtil.getString(R.string.mine_modify_pwd), R.drawable.icon_encrypt));
        settingPopupWindow.setSelectedSettingListener(new SettingPopupWindow.OnSelectedSettingListener() {
            @Override
            public void selectedSetting(SettingBean settingBean) {
                changeFolderPwdDialog.show(FolderActivity.this);
                settingPopupWindow.dismiss();
            }
        });
        settingPopupWindow.setSettingData(settingBeans);
    }

    /**
     * 初始 修改密码弹窗
     */
    private void initChangeFolderPwdDialog(){
        changeFolderPwdDialog = ChangeFolderPwdDialog.getInstance();
        changeFolderPwdDialog.setConfirmListener(new ChangeFolderPwdDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String oldPwd, String newPwd, String confirmPwd) {
                UpdateFolderPwdRequest updateFolderPwdRequest = new UpdateFolderPwdRequest(folderId, oldPwd, newPwd, confirmPwd);
                mPresenter.updateFolderPwd(Constant.scope_token, updateFolderPwdRequest);
            }
        });
    }

    /**
     * 访问接口 获取数据
     */
    private void getFoldersData(boolean refresh, boolean showLoading){
        mRefresh = refresh;
        page = refresh ? 1 : page+1;
        map.clear();
        map.put(Constant.PAGE_KEY, String.valueOf(page));
        map.put(Constant.PAGE_SIZE_KEY, String.valueOf(Constant.pageSize));
        mPresenter.getFolderList(Constant.scope_token, map, showLoading);
    }

    /**
     * 文件夹列表成功
     * @param folderListBean
     */
    @Override
    public void getFolderListSuccess(FolderListBean folderListBean) {
        if (folderListBean!=null){
            List<FolderBean> folderList = folderListBean.getList();
            if (mRefresh){
                mineFolderAdapter.setNewData(folderList);
                binding.rrv.showEmptyView(CollectionUtil.isEmpty(folderList));
            }else {
                mineFolderAdapter.addData(folderList);
            }
            binding.tvSetting.setVisibility(View.VISIBLE);
            binding.tvAdd.setVisibility(View.VISIBLE);
        }
        boolean noMore = true;
        PagerBean pagerBean = folderListBean.getPager();
        if (pagerBean!=null){
            noMore = !pagerBean.isHas_more();
        }
        binding.rrv.finishRefresh(noMore);
    }

    /**
     * 文件夹列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFolderFail(int errorCode, String msg) {
        binding.rrv.finishRefresh(true);
    }

    /**
     * 修改文件夹密码成功
     */
    @Override
    public void updateFolderPwdSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_update_pwd_success));
        if (changeFolderPwdDialog!=null && changeFolderPwdDialog.isShowing()){
            changeFolderPwdDialog.dismiss();
        }
    }

    /**
     * 修改文件夹密码失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateFolderPwdFail(int errorCode, String msg) {

    }

    /**
     * 删除任务成功
     */
    @Override
    public void removeTaskSuccess() {
        getFoldersData(true, false);
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
        getFoldersData(true, false);
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
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvAdd){ // 新增
                switchToActivityForResult(CreateFolderActivity.class, 100);
            }else if (viewId == R.id.tvSetting){  // 设置
                switchToActivity(FolderSettingActivity.class);
            }

        }
    }
}