package com.zhiting.clouddisk.util;

public interface HttpUrlParams {

//    String SC_URL = "https://gz.sc.zhitingtech.com";//测试环境
    String SC_URL = "https://gz.sc.zhitingtech.com";//正式环境
    String API = "/api";
    String START = "start"; // 第几页
    String SIZE = "size"; // 每页大小
    String SCOPE_TOKEN = "scope-token";
    String PWD = "pwd";
    String PATH = "path";
    String NAME = "name";
    String PAGE = "page";
    String PAGE_SIZE = "page_size";

//    String PARAMS_HEAD = "api/plugin/wangpan/";
    String PARAMS_HEAD = "wangpan/api/";

    String RESOURCES = PARAMS_HEAD + "resources/";  // 目录下的文件/子目录列表
    String RESOURCE = PARAMS_HEAD + "resources";
    String USERS = "/api/users"; // 用户
    String USER_DETAIL = "/api/users/"; // 用户
    String SHARES = PARAMS_HEAD + "shares"; // 共享
    String POOLS = PARAMS_HEAD + "pools"; // 存储池列表
    String POOL_DETAIL = PARAMS_HEAD + "pools/"; // 存储池列表
    String DISK = PARAMS_HEAD + "disks"; // 硬盘
    String CREATE_POOLS = PARAMS_HEAD + "pools"; // 选择硬盘创建存储池
    String ADD_DISKS_POOL = PARAMS_HEAD + "disks"; // 添加硬盘到存储池
    String MODIFY_POOLS_NAME = PARAMS_HEAD + "pools/"; // 编辑\重命名存储池
    String REMOVE_POOLS = PARAMS_HEAD + "pools/"; // 删除
    String PARTITIONS = PARAMS_HEAD + "partitions"; // 添加
    String MODIFY_PARTITIONS = PARAMS_HEAD + "partitions/"; // 编辑
    String DECRYPT_FILE = PARAMS_HEAD + "folders/"; // 解密文件
    String FOLDER_LIST = PARAMS_HEAD + "folders"; // 文件列表
    String UPDATE_FOLDER_PWD = PARAMS_HEAD + "updateFolderPwd"; // 修改密码
    String FOLDER_DETAIL = PARAMS_HEAD + "folders/"; // 文件夹详情
    String ADD_FOLDER = PARAMS_HEAD + "folders"; // 添加文件夹
    String DEL_FOLDER = PARAMS_HEAD + "folders/"; // 删除文件夹
    String EDIT_FOLDER = PARAMS_HEAD + "folders/"; // 编辑文件夹
    String FOLDER_SETTING = PARAMS_HEAD + "settings"; // 获取设置
    String TASKS = PARAMS_HEAD + "tasks/"; // 异步任务管理
    String TEMP_CHANNEL = SC_URL + "/api/datatunnel"; //临时通道
    String LOGIN = "http://gz.sc.zhitingtech.com/api/plugin/wangpan/users/login"; //登录(废弃）
    String LOGIN2 = SC_URL + "/api/sessions/login"; //登录
    String LOGOUT = SC_URL + "/api/sessions/logout"; //登录
    String EXTENSION = "/extension";
    String TOKENS = "/tokens";
}
