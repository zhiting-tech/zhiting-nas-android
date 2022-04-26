package com.zhiting.networklib.http;

public class HttpConfig {
    public static String baseUrl = "http://192.168.0.84:8088/";  // 测试服
    public static String baseZHUrl = "http://192.168.0.217:8089/";  // 泽浩
    public static String baseCBUrl = "http://192.168.0.180:8089/";  // 创彬
    public static String baseTestUrl = "http://192.168.22.245:9020/";  // 测试
    public static String baseSCUrl = "http://gz.sc.zhitingtech.com/";  // 测试
    public static String baseSCHost = "gz.sc.zhitingtech.com";//SC域名
    public static String baseSAHost = "192.168.22.245";//SA域名
    public static String areaId = "1";  // 测试
    public static String uploadFileUrl = "/wangpan/api/resources";//文件上传地址
    public static String downLoadFileUrl = "/wangpan/api/download/";//文件下载地址
    public static String downLoadFileUrl1 = "/wangpan/api/download";//文件下载地址
    public static String downLoadFolderUrl1 = "/wangpan/api/resources/*path";//文件夹下载地址
    public static String downLoadFolderUrl2 = "/wangpan/api/download/*path";//文件夹下载地址

    public static final int connectTimeout = 30;
    public static final int writeTimeOut = 30;
    public static final int readTimeOut = 30;
    public static final long MAX_CACHE_SIZE = 1024 * 1024 * 50; // 50M 的缓存大小
}
