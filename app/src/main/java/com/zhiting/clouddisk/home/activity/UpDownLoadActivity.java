package com.zhiting.clouddisk.home.activity;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityUpDownLoadBinding;
import com.zhiting.clouddisk.home.contract.UpDownLoadContract;
import com.zhiting.clouddisk.home.fragment.BackupFragment;
import com.zhiting.clouddisk.home.fragment.DownLoadFragment;
import com.zhiting.clouddisk.home.fragment.UploadFragment;
import com.zhiting.clouddisk.home.presenter.UpDownLoadPresenter;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传下载
 */
public class UpDownLoadActivity extends BaseMVPDBActivity<ActivityUpDownLoadBinding, UpDownLoadContract.View, UpDownLoadPresenter> implements UpDownLoadContract.View {

    private List<TextView> tvList = new ArrayList<>();

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_up_down_load;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initViewPager();
//        binding.tvUploadList.setSelected(true);
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new UploadFragment());
        fragments.add(new DownLoadFragment());
        fragments.add(new BackupFragment());
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        binding.nsv.setOffscreenPageLimit(fragments.size());
        binding.nsv.setAdapter(fragmentPagerAdapter);
        tvList.add(binding.tvUploadList);
        tvList.add(binding.tvDownloadList);
        tvList.add(binding.tvBackupList);
//        binding.nsv.setCurrentItem(0);
        resetViewPager(0);
    }

    private void resetViewPager(int pos) {
        binding.nsv.setCurrentItem(pos);
        for (int i=0; i<tvList.size(); i++){
            TextView textView = tvList.get(i);
            textView.setSelected(i == pos);
        }
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            } else if (viewId == R.id.tvUploadList) { // 上传列表
//                binding.nsv.setCurrentItem(0);
//                binding.tvUploadList.setSelected(true);
//                binding.tvDownloadList.setSelected(false);
                resetViewPager(0);
            } else if (viewId == R.id.tvDownloadList) { // 下载列表
//                binding.nsv.setCurrentItem(1);
//                binding.tvDownloadList.setSelected(true);
//                binding.tvUploadList.setSelected(false);
                resetViewPager(1);
            } else if (viewId == R.id.tvBackupList) {
                resetViewPager(2);
            }
        }
    }
}