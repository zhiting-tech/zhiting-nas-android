package com.zhiting.clouddisk.entity;

import androidx.annotation.DrawableRes;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

public enum BackupSettingBean {
    BACKUP_SETTING_AUTO(R.drawable.icon_system_setting, UiUtil.getString(R.string.mine_permit_auto_backup), SpConstant.BACKUP_AUTO+ Constant.AREA_ID+Constant.USER_ID),
    BACKUP_ALBUM_AUTO(R.drawable.icon_albumn_backup, UiUtil.getString(R.string.mine_picture_auto_backup), SpConstant.ALBUM_AUTO+Constant.AREA_ID+Constant.USER_ID),
    BACKUP_VIDEO_AUTO(R.drawable.icon_video_backup, UiUtil.getString(R.string.mine_video_auto_backup), SpConstant.VIDEO_AUTO+Constant.AREA_ID+Constant.USER_ID),
    BACKUP_FILE_AUTO(R.drawable.icon_file_backup, UiUtil.getString(R.string.mine_file_auto_backup), SpConstant.FILE_AUTO+Constant.AREA_ID+Constant.USER_ID),
    BACKUP_AUDIO_AUTO(R.drawable.icon_audio_backup, UiUtil.getString(R.string.mine_audio_auto_backup), SpConstant.AUDIO_AUTO+Constant.AREA_ID+Constant.USER_ID),
    ;
    @DrawableRes
    private int logo;
    private String name;
    private String key;
    private boolean open;

    BackupSettingBean(int logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    BackupSettingBean(int logo, String name, String key) {
        this.logo = logo;
        this.name = name;
        this.key = key;
    }

    BackupSettingBean(int logo, String name, boolean open) {
        this.logo = logo;
        this.name = name;
        this.open = open;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 系统设置数据
     *
     * @return
     */
    public static List<BackupSettingBean> getSystemData() {
        List<BackupSettingBean> systemData = new ArrayList<>();
        systemData.add(BACKUP_SETTING_AUTO);
        return systemData;
    }

    /**
     * 自动备份数据
     * @return
     */
    public static List<BackupSettingBean> getAutoData() {
        List<BackupSettingBean> autoData = new ArrayList<>();
        BACKUP_ALBUM_AUTO.setKey(SpConstant.ALBUM_AUTO+Constant.AREA_ID+Constant.USER_ID);
        autoData.add(BACKUP_ALBUM_AUTO);
        BACKUP_VIDEO_AUTO.setKey(SpConstant.VIDEO_AUTO+Constant.AREA_ID+Constant.USER_ID);
        autoData.add(BACKUP_VIDEO_AUTO);
        BACKUP_FILE_AUTO.setKey(SpConstant.FILE_AUTO+Constant.AREA_ID+Constant.USER_ID);
        autoData.add(BACKUP_FILE_AUTO);
        BACKUP_AUDIO_AUTO.setKey(SpConstant.AUDIO_AUTO+Constant.AREA_ID+Constant.USER_ID);
        autoData.add(BACKUP_AUDIO_AUTO);
        return autoData;
    }
}
