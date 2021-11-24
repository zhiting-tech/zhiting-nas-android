package com.zhiting.clouddisk.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.DownDetailAdapter;
import com.zhiting.clouddisk.adapter.DownDetailNavigateAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityDownDetailBinding;
import com.zhiting.clouddisk.dialog.DownDetailDialog;
import com.zhiting.clouddisk.home.contract.DownDetailContract;
import com.zhiting.clouddisk.home.presenter.DownDetailPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载详情
 */
public class DownDetailActivity extends BaseMVPDBActivity<ActivityDownDetailBinding, DownDetailContract.View, DownDetailPresenter> implements DownDetailContract.View {

    private DownDetailNavigateAdapter mDownDetailNavigateAdapter; // 头部导航
    private DownDetailAdapter mDownDetailAdapter;  // 文件列表
    private List<File> mNavFileList = new ArrayList<>(); // 头部导航
    private List<File> mFileList = new ArrayList<>(); // 文件列表
    private String mPath = "/storage/emulated/0/";

    @Override
    public int getLayoutId() {
        return R.layout.activity_down_detail;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        String path = intent.getStringExtra(Constant.PATH);
        if (!TextUtils.isEmpty(path))
            mPath = path;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());

    }

    @Override
    protected void initData() {
        super.initData();
        initRvNav();
        initRvFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (CollectionUtil.isNotEmpty(mNavFileList)) {
            int size = mNavFileList.size();
            if (size == 1) {
                finish();
            } else if (size > 1) {
                mNavFileList.remove(size - 1);
                File fileItem = mNavFileList.get(size - 2);
                binding.tvTitle.setText(fileItem.getName());
                mPath = fileItem.getAbsolutePath();
                mFileList = FileUtil.getFileList(mPath);
                mDownDetailNavigateAdapter.notifyDataSetChanged();
                setNewData();
            }
        }
    }

    /**
     * 头部导航
     */
    private void initRvNav() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvNavigation.setLayoutManager(layoutManager);
        mDownDetailNavigateAdapter = new DownDetailNavigateAdapter();
        binding.rvNavigation.setAdapter(mDownDetailNavigateAdapter);
        File file = new File(mPath);
        binding.tvTitle.setText(file.getName());
        mNavFileList.add(file);
        mDownDetailNavigateAdapter.setNewData(mNavFileList);
        mDownDetailNavigateAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position < mDownDetailNavigateAdapter.getData().size() - 1) {
                int x = 0;
                while (x < mNavFileList.size()) {
                    if (x > position) {
                        mNavFileList.remove(x);
                    } else {
                        x++;
                    }
                }
                mDownDetailNavigateAdapter.notifyDataSetChanged();

                File fileItem = mDownDetailNavigateAdapter.getItem(position);
                binding.tvTitle.setText(fileItem.getName());
                mPath = fileItem.getAbsolutePath();
                mFileList = FileUtil.getFileList(mPath);
                setNewData();
            }
        });
    }

    /**
     * 文件列表
     */
    private void initRvFile() {
        mDownDetailAdapter = new DownDetailAdapter();
        binding.rvFile.setAdapter(mDownDetailAdapter);
        mFileList = FileUtil.getFileList(mPath);
        setNewData();
        mDownDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            File fileDetailItem = mDownDetailAdapter.getItem(position);
            if (FileUtil.isFile(fileDetailItem)) {  // 是文件
                showOperateDialog(fileDetailItem);
            } else {  // 是文件夹
                binding.tvTitle.setText(fileDetailItem.getName());
                mNavFileList.add(fileDetailItem);
                binding.rvNavigation.smoothScrollToPosition(mNavFileList.size() - 1);
                mDownDetailNavigateAdapter.notifyDataSetChanged();
                mPath = fileDetailItem.getAbsolutePath();
                mFileList = FileUtil.getFileList(mPath);
                setNewData();
            }
        });
    }

    /**
     * 设置新数据
     */
    private void setNewData() {
        mDownDetailAdapter.setNewData(mFileList);
        binding.rvFile.showEmptyView(CollectionUtil.isEmpty(mFileList));
    }

    /**
     * 文件操作
     *
     * @param file
     */
    private void showOperateDialog(File file) {
        DownDetailDialog downDetailDialog = DownDetailDialog.getInstance(file);
        downDetailDialog.setOperateListener((position, file1) -> {
            Uri imageUri = FileProvider.getUriForFile(
                    DownDetailActivity.this,
                    "com.zhiting.clouddisk.provider", //(use your app signature + ".provider" )
                    file1);
            openFile(imageUri, FileTypeUtil.getFileUriType(FileTypeUtil.fileType(file1.getName())));
            downDetailDialog.dismiss();
        });
        downDetailDialog.show(this);
    }

    /**
     * 打开文件
     *
     * @param uri
     */
    private void openFile(Uri uri, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, type);
        startActivity(Intent.createChooser(intent, UiUtil.getString(R.string.home_please_select_software_to_open)));
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