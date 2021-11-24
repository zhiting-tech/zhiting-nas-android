package com.zhiting.clouddisk.home.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileNavigationAdapter;
import com.zhiting.clouddisk.adapter.HomeFileAdapter;
import com.zhiting.clouddisk.databinding.ActivityMoveCopyDetailBinding;
import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.dialog.InputPwdDialog;
import com.zhiting.clouddisk.dialog.RenameFileDialog;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.entity.mine.PagerBean;
import com.zhiting.clouddisk.event.FinishFileDetailEvent;
import com.zhiting.clouddisk.event.FinishMoveCopyDetailEvent;
import com.zhiting.clouddisk.home.contract.MoveCopyDetailContract;
import com.zhiting.clouddisk.home.fragment.HomeFragment;
import com.zhiting.clouddisk.home.presenter.MoveCopyDetailPresenter;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.MoveCopyRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.TimeUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.ErrorConstant;
import com.zhiting.networklib.utils.LibLoader;
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
 * 移动复制到
 */
public class MoveCopyDetailActivity extends BaseMVPDBActivity<ActivityMoveCopyDetailBinding, MoveCopyDetailContract.View, MoveCopyDetailPresenter> implements  MoveCopyDetailContract.View {

    /**
     * type 0 移动  1复制
     */
    private int type;
    /**
     *  0 我的文件 1 共享文件
     */
    private int pathType;
    private String pathName;
    private List<String> paths; // 要移动/复制的文件路径

    private int start;
    private boolean mRefresh;
    private HomeFileAdapter homeFileAdapter;
    private FileNavigationAdapter fileNavigationAdapter; // 头部导航
    private List<FileBean> fileData; // 头部导航数据

    private final int pageSize = 60;


    /**
     * 0 从首页来
     * 1 从共享文件夹来
     */
    private int from;
    private boolean hasLoad;  // 是否已经加载过

    private InputPwdDialog inputPwdDialog; // 密码输入弹窗
    private FileBean mFileBean; // 选择的文件
    private String parentPath=""; // 要移动/复制的父路径

    private boolean hideOperate; // 是否隐藏底部操作

    private RenameFileDialog renameFileDialog;

    /**
     * 刷新加载更多监听
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_move_copy_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasLoad){
            fileData.remove(fileData.size()-1);
            fileNavigationAdapter.notifyItemRemoved(fileData.size()-1);
        }
        hasLoad = true;

    }

    @Override
    protected void initUI() {
        super.initUI();
        LibLoader.addMoveCopyDetailAct(this);
        binding.setHandler(new OnClickHandler());
        initPwdDialog();
        initRvNavigation();
        initRv();

    }


    /**
     * 显示输入密码弹窗
     */
    private void initPwdDialog() {
        inputPwdDialog = InputPwdDialog.getInstance();
        inputPwdDialog.setConfirmListener(pwd -> {
            if (mFileBean != null) {
                HomeFragment.filePwd = pwd;
                checkFilePwd();
            }
        });
    }

    /**
     * 校验文件夹密码是否正确
     */
    private void checkFilePwd() {
        if (mFileBean != null) {
            if (TextUtils.isEmpty( HomeFragment.filePwd)){ // 如果密码为空，则输入
                inputPwdDialog.show(this);
            }else { // 密码不为空，校验密码
                CheckPwdRequest checkPwdRequest = new CheckPwdRequest( HomeFragment.filePwd);
                mPresenter.decryptFile(Constant.scope_token, mFileBean.getPath(), checkPwdRequest);
            }
        }
    }

    /**
     * 初始化列表
     */
    private void initRv(){
        homeFileAdapter = new HomeFileAdapter(1, false);
        ((SimpleItemAnimator)binding.rvNavigation.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rrv.setAdapter(homeFileAdapter)
                .setOnRefreshAndLoadMoreListener(refreshLoadMoreListener);
        homeFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mFileBean = homeFileAdapter.getItem(position);
                if (mFileBean.getRead() == 1){  // 有读权限
                    if (mFileBean.getIs_encrypt()== 1){ // 加密文件
//                        inputPwdDialog.show(MoveCopyDetailActivity.this);
                        if (TextUtils.isEmpty(HomeFragment.filePwd)){  // 如果还没有密码，要输入密码
//                            inputPwdDialog.show(MoveCopyDetailActivity.this);
                            mPresenter.getFolderPwdByScopeTokenAndPath(Constant.scope_token, mFileBean.getPath());
                        }else {
                            toNext(true);
                        }
                    }else {  // 普通文件
                        toNext(false);
                    }
                }else {  // 没有读权限
                    ToastUtil.show(UiUtil.getString(R.string.mine_without_read_permission));
                }

//                finish();
            }
        });

    }

    /**
     * 下一级目录
     */
    private void toNext(boolean hasEncrypted){
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("pathType", 0);
        bundle.putString("pathName", pathName + " > " + mFileBean.getName());
        bundle.putString("sonPath", mFileBean.getPath());
        bundle.putSerializable("paths", (Serializable) paths);
        fileData.add(mFileBean);
        bundle.putSerializable("navigation", (Serializable) fileData);
        bundle.putString("filePwd", hasEncrypted ? HomeFragment.filePwd : "");
        bundle.putString("parentPath", parentPath);
        switchToActivity(MoveCopyDetailActivity.class, bundle);
    }

    /**
     * 初始化头部导航
     */
    private void initRvNavigation(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvNavigation.setLayoutManager(layoutManager);
        fileNavigationAdapter = new FileNavigationAdapter();
        binding.rvNavigation.setAdapter(fileNavigationAdapter);
        fileNavigationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position < fileNavigationAdapter.getData().size() - 1) {
                        FileBean fileBean = fileNavigationAdapter.getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", type);
                        bundle.putSerializable("paths", (Serializable) paths);
                        if (!TextUtils.isEmpty(fileBean.getPath())) {
                            int size = fileData.size();
                            for (int i=1; i<size; i++){
                                if (i>position) {
                                    LibLoader.finishMoveCopyDetailAct();
                                }
                            }
                        } else {
                            switchToActivity(MoveCopyFileActivity.class, bundle);
                            finish();
                        }
                    }
            }
        });
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        type = intent.getIntExtra("type", 0);
        pathType = intent.getIntExtra("pathType", 0);
        pathName = intent.getStringExtra("pathName");
        from = intent.getIntExtra(Constant.FROM, 0);
        paths = (List<String>) intent.getSerializableExtra("paths");
        binding.tvTitle.setText(type == 0 ? UiUtil.getString(R.string.home_move_to) : UiUtil.getString(R.string.home_copy_to));
        binding.tvMove.setText(type == 0 ? UiUtil.getString(R.string.home_move_to_here) : UiUtil.getString(R.string.home_copy_to_here));
        binding.tvCategory.setText(pathName);
        binding.rrv.setEnableLoadable(pathType == 0);
        hideOperate = intent.getBooleanExtra("hideOperate", false);

        HomeFragment.filePwd = intent.getStringExtra("filePwd");
        parentPath = intent.getStringExtra("parentPath");
        List<FileBean> fd = (List<FileBean>) intent.getSerializableExtra("navigation");

        fileData = new ArrayList<>();
        if (from == 0){ // 首页文件
            fileData.addAll(fd);
            homeFileAdapter.setShowEncryptStyle(fileData.get(fileData.size() - 1).getPath().equals("/"));
        }else { // 共享文件
            fileData.add(fd.get(1));
            homeFileAdapter.setShowEncryptStyle(false);
        }
        if (hideOperate) { // 隐藏底部操作
            binding.slBottom.setVisibility(View.GONE);
        } else{
            binding.slBottom.setVisibility(pathType == 0 ? View.VISIBLE : View.GONE);
        }
        fileNavigationAdapter.setNewData(fileData);
        getData(true, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishMoveCopyDetailEvent event) {
        finish();
    }


    /**
     * 获取文件列表成功
     * @param fileListBean
     */
    @Override
    public void getFilesSuccess(FileListBean fileListBean) {
        if (fileListBean!=null){
            List<FileBean> files = fileListBean.getList();
            List<FileBean> data = new ArrayList<>();
            for (FileBean fileBean : files){ // 筛选是文件夹的数据
                if (fileBean.getType() == 0){
                    fileBean.setEnabled(!paths.contains(fileBean.getPath()));
                    data.add(fileBean);
                }
            }
            if (mRefresh){
                homeFileAdapter.setNewData(data);
                binding.rrv.showEmptyView(CollectionUtil.isEmpty(data));
            }else {
                homeFileAdapter.addData(data);
            }
            boolean hasMore = false;
            PagerBean pagerBean = fileListBean.getPager();
            if (pagerBean!=null){
                hasMore = pagerBean.isHas_more();
            }
            binding.rrv.finishRefresh(!hasMore);
        }

    }

    /**
     * 获取文件列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFilesFail(int errorCode, String msg) {

    }

    /**
     * 密码错误
     */
    private void pwdError(){
        // 不要删，不确定需求会不会改
//            if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
//                HomeFragment.filePwd="";
//                updateFolderPwd();
//                inputPwdDialog.show(this);
//            }
        HomeFragment.filePwd = "";
        updateFolderPwd();
        EventBus.getDefault().post(new FinishMoveCopyDetailEvent());
    }


    /**
     * 更新文件夹密码
     */
    private void updateFolderPwd() {
        HomeFragment.mFolderPwd.setPassword(HomeFragment.filePwd);
        HomeFragment.mFolderPwd.setModifyTime(TimeUtil.getCurrentTimeMillis());
        mPresenter.updateFolderPwd(HomeFragment.mFolderPwd);
    }

    /**
     * 新建文件夹成功
     * @param uploadCreateFileBean
     */
    @Override
    public void createFileSuccess(UploadCreateFileBean uploadCreateFileBean) {
        ToastUtil.show(UiUtil.getString(R.string.home_create_new_file_success));
        if (renameFileDialog!=null && renameFileDialog.isShowing()){
            renameFileDialog.dismiss();
        }
        getData(true, true);
    }

    /**
     * 新建文件夹失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void createFileFail(int errorCode, String msg) {

    }

    /**
     * 移动复制成功
     */
    @Override
    public void moveCopySuccess() {
        ToastUtil.show(type == 0 ? UiUtil.getString(R.string.home_move_success) : UiUtil.getString(R.string.home_copy_success));
        EventBus.getDefault().post(new FinishMoveCopyDetailEvent());
        finish();
    }

    /**
     * 移动复制失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void moveCopyFail(int errorCode, String msg) {

    }


    /**
     * 解密文件成功
     */
    @Override
    public void decryptPwdSuccess() {
        if (inputPwdDialog != null && inputPwdDialog.isShowing()) {
            inputPwdDialog.dismiss();
        }
        toNext(true);
    }

    /**
     * 解密文件失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void decryptPwdFail(int errorCode, String msg) {
        if (errorCode == ErrorConstant.PWD_ERROR) { // 文件夹密码错误
            if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
                HomeFragment.filePwd = "";
                updateFolderPwd();
                inputPwdDialog.show(this);
            }
        }
    }

    /**
     * 显示输入密码弹窗
     */
    private void showInputPwdDialog() {
        if (inputPwdDialog != null && !inputPwdDialog.isShowing()) {
            inputPwdDialog.show(MoveCopyDetailActivity.this);
        }
    }

    /**
     * 获取密码成功
     *
     * @param folderPassword
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathSuccess(FolderPassword folderPassword) {
        LogUtil.e("查询文件夹密码成功");
        if (folderPassword != null) {
            HomeFragment.filePwd = folderPassword.getPassword();
            long modifyTime = folderPassword.getModifyTime();
            long distinct = TimeUtil.getCurrentTimeMillis() - modifyTime;
            HomeFragment.mFolderPwd = folderPassword;
            if (TimeUtil.over72hour(distinct) || TextUtils.isEmpty(HomeFragment.filePwd)) {  // 超过72小时
                showInputPwdDialog();
            } else {
                checkFilePwd();
            }

        } else {
            showInputPwdDialog();
        }
    }

    /**
     * 获取密码失败
     */
    @Override
    public void getFolderPwdByScopeTokenAndPathFail() {
        LogUtil.e("查询文件夹密码失败");
        showInputPwdDialog();
    }

    /**
     * 插入密码成功
     *
     * @param result
     */
    @Override
    public void insertFolderPwdSuccess(boolean result) {
        LogUtil.e("插入文件夹密码成功");
    }

    /**
     * 插入密码失败
     */
    @Override
    public void insertFolderFail() {
        LogUtil.e("插入文件夹密码失败");
    }

    /**
     * 修改文件夹密码成功
     */
    @Override
    public void updateFolderPwdSuccess() {
        LogUtil.e("修改文件夹密码成功");
    }

    /**
     * 修改文件夹密码失败
     */
    @Override
    public void updateFolderPwdFail() {
        LogUtil.e("修改文件夹密码失败");
    }

    /**
     * 获取文件列表数据
     * @param showLoading
     */
    private void getData(boolean refresh, boolean showLoading){
        mRefresh = refresh;
        map.clear();
        start = refresh ? 1 : start+1;
        if (pathType == 0){ // 我的文件
            map.put(HttpUrlParams.PAGE, String.valueOf(start));
            map.put(HttpUrlParams.PAGE_SIZE, String.valueOf(pageSize));
            mPresenter.getFiles(Constant.scope_token, HomeFragment.filePwd, fileData.get(fileData.size()-1).getPath() + "/", map, showLoading);
        }else { // 共享文件
            mPresenter.getShareFolders(Constant.scope_token, showLoading);
        }
    }

    /**
     * 创建 文件夹
     */
    private void showCreateFileDialog(){
         renameFileDialog = RenameFileDialog.getInstance(0, R.drawable.icon_file_big, "");
         renameFileDialog.setCompleteListener(new RenameFileDialog.OnCompleteListener() {
            @Override
            public void onComplete(int type, String fileName) {
                mPresenter.createFile(Constant.scope_token, HomeFragment.filePwd, fileData.get(fileData.size()-1).getPath()+"/" +fileName+"/");
            }

            @Override
            public void onDismissListener(DialogInterface dialog) {

            }
        });
//        renameFileDialog.setCompleteListener(new RenameFileDialog.OnCompleteListener() {
//            @Override
//            public void onComplete(int type, String fileName) {
//                mPresenter.createFile(Constant.scope_token, filePwd, fileData.get(fileData.size()-1)+"/" +fileName+"/");
//                renameFileDialog.dismiss();
//            }
//        });
        renameFileDialog.show(this);
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvCancel) { // 返回
                EventBus.getDefault().post(new FinishMoveCopyDetailEvent());
            }else if (viewId == R.id.tvCreate){ // 新建文件夹
                showCreateFileDialog();
            }else if (viewId == R.id.tvMove){ // 移到此处
                MoveCopyRequest moveCopyRequest = new MoveCopyRequest(type == 0 ? Constant.MOVE : Constant.COPY, fileData.get(fileData.size()-1).getPath(), paths);
                moveCopyRequest.setDestination_pwd(HomeFragment.filePwd);
                mPresenter.moveCopyFile(Constant.scope_token, moveCopyRequest);
            }
        }
    }
}