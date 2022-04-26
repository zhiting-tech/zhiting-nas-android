package com.zhiting.clouddisk.constant;

import static android.os.Environment.getExternalStorageDirectory;

import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.networklib.utils.AndroidUtil;

import java.util.List;

import okhttp3.Cookie;

public class Constant {
    public static AuthBackBean authBackBean;  // 授权信息
    public static List<Cookie> cookies;

    public static final int pageSize = 30;
    public static final String PAGE_KEY = "page";
    public static final String PAGE_SIZE_KEY = "page_size";

    public static String HOME_NAME = "我的家";
    public static long AREA_ID = 0;//云端id

    public static String scope_token = "";
    public static int USER_ID = 2;
    public static String userName = "";
    public static HomeCompanyBean currentHome;//当前家庭对象
    public static boolean isX5Success;//腾讯X5初始化是否成功


    public static final String PATH = "path";
    public static final String NAME = "name";
    public static final String ALL_PATH = "all_path";
    public static final String BEAN = "bean";
    public static final String FROM = "from";
    public static final String KEY_ID = "key_id";

    public static final String MOVE = "move";
    public static final String COPY = "copy";
    public static final String KEY_BEAN = "key_bean";

    public static final String SYSTEM_POOL = "_default_";

    public static final String FOLDER_UPDATING = "TaskMovingFolder_1"; // 文件夹修改中
    public static final String FOLDER_UPDATE_FAIL = "TaskMovingFolder_0"; // 文件夹修改失败
    public static final String FOLDER_DELETING = "TaskDelFolder_1"; // 文件夹删除中
    public static final String FOLDER_DELETE_FAIL = "TaskDelFolder_0"; // 文件夹删除失败

    public static final String STORAGE_POOL_DELETING = "TaskDelPool_1"; // 存储池删除中
    public static final String STORAGE_POOL_DELETE_FAIL = "TaskDelPool_0"; // 存储池删除失败
    public static final String STORAGE_POOL_ADDING = "TaskAddPool_1"; // 存储池添加中
    public static final String STORAGE_POOL_ADD_FAIL = "TaskAddPool_0"; // 存储池添加失败
    public static final String STORAGE_POOL_UPDATING = "TaskUpdatePool_1"; // 存储池修改中
    public static final String STORAGE_POOL_UPDATE_FAIL = "TaskUpdatePool_0"; // 存储池修改失败

    public static final String PARTITION_ADDING = "TaskAddPartition_1"; // 添加存储池分区中
    public static final String PARTITION_ADD_FAIL = "TaskAddPartition_0"; // 添加存储池分区失败
    public static final String PARTITION_UPDATING = "TaskUpdatePartition_1"; // 修改存储池分区中
    public static final String PARTITION_UPDATE_FAIL = "TaskUpdatePartition_0"; // 修改存储池分区失败
    public static final String PARTITION_DELETING = "TaskDelPartition_1"; // 删除存储池分区中
    public static final String PARTITION_DELETE_FAIL = "TaskDelPartition_0"; // 删除存储池分区失败

    public static final String AGREEMENT_URL = "https://gz.sc.zhitingtech.com/zt-nas/protocol/user";
    public static final String POLICY_URL = "https://gz.sc.zhitingtech.com/zt-nas/protocol/privacy";

    public static final String AGREED = "agreed"; // 同意用户协议和隐私政策

    public static final String SMART_ASSISTANT = "smart_assistant";
    public static final String HTTP = "http";
    public static final String HTTP_HEAD = HTTP + "://";

    public static final String FIND_DEVICE_URL = "255.255.255.255"; // hello 数据包地址
    public static final int FIND_DEVICE_PORT = 54321; // hello数据包端口
    // 发送hello包数据，固定
    public static final byte[] SEND_HELLO_DATA = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    public static final String ROOT_PATH = getExternalStorageDirectory().getPath().toString();
    public static final String BACKUP_CAMERA = ROOT_PATH+"/DCIM/Camera";
    public static final String BACKUP_VIDEO = ROOT_PATH+"/DCIM/Camera";
    public static final String BACKUP_FILE = ROOT_PATH+"/documents";
    public static final String BACKUP_AUDIO = ROOT_PATH+"/DCIM/Camera";

    public static final int BACK_ALBUM_TYPE = 1;
    public static final int BACK_VIDEO_TYPE = 2;
    public static final int BACK_FILE_TYPE = 3;
    public static final int BACK_AUDIO_TYPE = 4;

    public static String getRecordPath(){
        String brand = AndroidUtil.getDeviceBrand();
        String recordPath = ROOT_PATH;
        switch (brand.toLowerCase()){
            case "huawei":

            case "samsung":
                recordPath = ROOT_PATH + "/Sounds";
                break;

            case "xiaomi":
                recordPath = ROOT_PATH + "/MIUI/sound_recorder";
                break;

            case "meizu":
                recordPath = ROOT_PATH + "/Recorder";
                break;

            case "oppo":
                recordPath = ROOT_PATH + "/Recordings";
                break;

            case "vivo":
                recordPath = ROOT_PATH + "/Record";
                break;
        }
        return recordPath;
    }
}
