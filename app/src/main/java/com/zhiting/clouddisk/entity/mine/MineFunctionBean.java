package com.zhiting.clouddisk.entity.mine;

import androidx.annotation.DrawableRes;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.utils.UiUtil;

public enum  MineFunctionBean {
    STORAGE_POOL_MANAGER(R.drawable.icon_storage_mamanger, UiUtil.getString(R.string.mine_storage_manager)),
    FOLDER_MANAGER(R.drawable.icon_file_manager, UiUtil.getString(R.string.mine_file_manager)),
    BACKUP_MANAGE(R.drawable.icon_backup_manager, UiUtil.getString(R.string.mine_backup_manager)),
    DOWNLOAD_SETTINGS(R.drawable.icon_download_setting, UiUtil.getString(R.string.mine_download_setting)),
    CLEAR_CACHE(R.drawable.icon_clear_cache, UiUtil.getString(R.string.mine_clear_cache));

    @DrawableRes
    private int logo;
    private String name;
    private boolean enable;
    private String size;

    public String getSize() {
        return size;
    }

    public MineFunctionBean setSize(String size) {
        this.size = size;
        return this;
    }

    MineFunctionBean(int logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    MineFunctionBean(int logo, String name, boolean enable) {
        this.logo = logo;
        this.name = name;
        this.enable = enable;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
