package com.zhiting.clouddisk.home.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.HomeFileAdapter;
import com.zhiting.clouddisk.databinding.ActivityMoveCopyFileBinding;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.event.FinishMoveCopyDetailEvent;
import com.zhiting.clouddisk.home.contract.MoveCopyFileContract;
import com.zhiting.clouddisk.home.presenter.MoveCopyFilePresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.UiUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 移动复制  选择我的文件夹还是共享文件夹界面
 */
public class MoveCopyFileActivity extends BaseMVPDBActivity<ActivityMoveCopyFileBinding, MoveCopyFileContract.View, MoveCopyFilePresenter> implements MoveCopyFileContract.View {

    private HomeFileAdapter homeFileAdapter;

    /**
     * type 0 移动  1复制
     */
    private int type;
    private List<String> paths; // 要移动/复制的文件路径

    @Override
    public int getLayoutId() {
        return R.layout.activity_move_copy_file;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvRoot();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        type = intent.getIntExtra("type",0);
        paths = (List<String>) intent.getSerializableExtra("paths");
        binding.tvTitle.setText(type == 0 ? UiUtil.getString(R.string.home_move_to) : UiUtil.getString(R.string.home_copy_to));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishMoveCopyDetailEvent event) {
        finish();
    }

    private void initRvRoot(){
        binding.rvRoot.setLayoutManager(new LinearLayoutManager(this));
        homeFileAdapter = new HomeFileAdapter(1, false);
        binding.rvRoot.setAdapter(homeFileAdapter);
        List<FileBean> data = new ArrayList<>();
        FileBean myFile = new FileBean(UiUtil.getString(R.string.home_file), 0);
        FileBean shareFile = new FileBean(UiUtil.getString(R.string.home_share_folder), 0);
        data.add(myFile);
        data.add(shareFile);
        homeFileAdapter.setNewData(data);
        homeFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putInt("pathType", position);
                bundle.putBoolean("hideOperate", position == 0);
                bundle.putString("pathName", UiUtil.getString(R.string.home_root_category) + " > " + homeFileAdapter.getItem(position).getName());
                List<FileBean> fileData = new ArrayList<>();
                fileData.add(new FileBean(UiUtil.getString(R.string.home_root_category), 0));
                fileData.add(new FileBean(homeFileAdapter.getItem(position).getName(), 0, "/"));
                bundle.putSerializable("paths", (Serializable) paths);
                bundle.putSerializable("navigation", (Serializable) fileData);
                switchToActivity(MoveCopyDetailActivity.class, bundle);
                finish();
            }
        });

    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvCancel) { // 返回
                finish();
            }
        }
    }
}