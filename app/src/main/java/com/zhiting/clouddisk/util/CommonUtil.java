package com.zhiting.clouddisk.util;

import com.luck.picture.lib.tools.SPUtils;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.networklib.utils.NetworkUtil;

public class CommonUtil {

    public static boolean isShowTraffic(){
        int type = NetworkUtil.getNetworkerStatus();
        boolean isOnlyWifi = SPUtils.getInstance().getBoolean(Config.KEY_ONLY_WIFI, true);
        return (type >= 2 && isOnlyWifi);
    }
}
