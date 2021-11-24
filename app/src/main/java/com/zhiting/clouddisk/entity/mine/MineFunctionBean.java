package com.zhiting.clouddisk.entity.mine;

import androidx.annotation.DrawableRes;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.utils.UiUtil;

public enum  MineFunctionBean {
    STORAGE_POOL_MANAGER(R.drawable.icon_storage_mamanger, UiUtil.getString(R.string.mine_storage_manager)),
    FOLDER_MANAGER(R.drawable.icon_file_manager, UiUtil.getString(R.string.mine_file_manager)),
    ;

    @DrawableRes
    private int logo;
    private String name;
    private boolean enable;

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
