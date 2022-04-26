package com.zhiting.clouddisk.mine.activity;

import static android.os.Environment.getExternalStorageDirectory;

import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.luck.picture.lib.entity.LocalMedia;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.BackupSettingAdapter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.databinding.ActivityBackupSettingBinding;
import com.zhiting.clouddisk.databinding.ActivityDownloadSettingBinding;
import com.zhiting.clouddisk.dialog.TrafficTipsDialog;
import com.zhiting.clouddisk.entity.BackupSettingBean;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.mine.contract.BackupSettingContract;
import com.zhiting.clouddisk.mine.contract.DownloadSettingContract;
import com.zhiting.clouddisk.mine.presenter.BackupSettingPresenter;
import com.zhiting.clouddisk.mine.presenter.DownloadSettingPresenter;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.utils.AndroidUtil;
import com.zhiting.networklib.utils.NetworkUtil;
import com.zhiting.networklib.utils.SpUtil;

/**
 * 备份设置
 */
public class BackupSettingActivity extends BaseMVPDBActivity<ActivityBackupSettingBinding, BackupSettingContract.View, BackupSettingPresenter> implements BackupSettingContract.View {

    private BackupSettingAdapter mSystemSettingAdapter;
    private BackupSettingAdapter mAutoAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvSystem();
        initRvAuto();
    }

    /**
     * 系统设置
     */
    private void initRvSystem() {
        binding.rvSystem.setLayoutManager(new LinearLayoutManager(this));
        mSystemSettingAdapter = new BackupSettingAdapter();
        binding.rvSystem.setAdapter(mSystemSettingAdapter);
        BackupSettingBean.BACKUP_SETTING_AUTO.setOpen(SpUtil.getBoolean(SpConstant.BACKUP_AUTO));
        mSystemSettingAdapter.setNewData(BackupSettingBean.getSystemData());
        mSystemSettingAdapter.setSwitchListener(new BackupSettingAdapter.OnSwitchListener() {
            @Override
            public void onSwitch(BackupSettingBean backupSettingBean, boolean open) {
                switch (backupSettingBean) {
                    case BACKUP_SETTING_AUTO: // 后台备份

                        break;
                }
            }
        });
    }

    /**
     * 自动备份
     */
    private void initRvAuto() {
        binding.rvAuto.setLayoutManager(new LinearLayoutManager(this));
        mAutoAdapter = new BackupSettingAdapter();
        binding.rvAuto.setAdapter(mAutoAdapter);
        BackupSettingBean.BACKUP_ALBUM_AUTO.setOpen(SpUtil.getBoolean(SpConstant.ALBUM_AUTO+Constant.AREA_ID+Constant.USER_ID));
        BackupSettingBean.BACKUP_VIDEO_AUTO.setOpen(SpUtil.getBoolean(SpConstant.VIDEO_AUTO+Constant.AREA_ID+Constant.USER_ID));
        BackupSettingBean.BACKUP_FILE_AUTO.setOpen(SpUtil.getBoolean(SpConstant.FILE_AUTO+Constant.AREA_ID+Constant.USER_ID));
        BackupSettingBean.BACKUP_AUDIO_AUTO.setOpen(SpUtil.getBoolean(SpConstant.AUDIO_AUTO+Constant.AREA_ID+Constant.USER_ID));
        mAutoAdapter.setNewData(BackupSettingBean.getAutoData());

        mAutoAdapter.setSwitchListener(new BackupSettingAdapter.OnSwitchListener() {
            @Override
            public void onSwitch(BackupSettingBean backupSettingBean, boolean open) {
                int type = NetworkUtil.getNetworkerStatus();
                if (type >= 2 && open){
                    showTrafficTipsDialog(backupSettingBean, open);
                } else {
                    handleSwitch(backupSettingBean, open);
                }
            }
        });
    }

    /**
     * 显示流量弹窗
     * @param backupSettingBean
     * @param open
     */
    private void showTrafficTipsDialog(BackupSettingBean backupSettingBean, boolean open){
        TrafficTipsDialog mTrafficTipsDialog = TrafficTipsDialog.getInstance();
        mTrafficTipsDialog.setConfirmListener(new TrafficTipsDialog.OnConfirmListener() {
            @Override
            public void onResume() {
                handleSwitch(backupSettingBean, open);
            }

            @Override
            public void onPause() {
                backupSettingBean.setOpen(false);
                SpUtil.put(backupSettingBean.getKey(), false);
                mAutoAdapter.notifyDataSetChanged();
            }
        });
        mTrafficTipsDialog.show(this);
    }

    /**
     * 处理开关
     */
    private void handleSwitch(BackupSettingBean backupSettingBean, boolean open){
        switch (backupSettingBean) {
            case BACKUP_ALBUM_AUTO:  // 相册
                if (open) {
                    GonetUtil.addUploadFile(Constant.BACKUP_CAMERA, Constant.BACK_ALBUM_TYPE);
                } else {
                    GonetUtil.closeFileBackup(Constant.BACK_ALBUM_TYPE);
                }
                break;

            case BACKUP_VIDEO_AUTO:  // 视频
                if (open) {
                    GonetUtil.addUploadFile(Constant.BACKUP_VIDEO, Constant.BACK_VIDEO_TYPE);
                } else {
                    GonetUtil.closeFileBackup(Constant.BACK_VIDEO_TYPE);
                }
                break;

            case BACKUP_FILE_AUTO:  // 文档
                if (open) {
                    GonetUtil.addUploadFile(Constant.BACKUP_FILE, Constant.BACK_FILE_TYPE);
                }else {
                    GonetUtil.closeFileBackup(Constant.BACK_FILE_TYPE);
                }
                break;

            case BACKUP_AUDIO_AUTO: // 音频
                if (open) {
                    GonetUtil.addUploadFile(Constant.getRecordPath(), Constant.BACK_AUDIO_TYPE);
                } else {
                    GonetUtil.closeFileBackup(Constant.BACK_AUDIO_TYPE);
                }
                break;
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
            }
        }
    }
}