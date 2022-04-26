package com.zhiting.clouddisk.api;

import com.zhiting.clouddisk.entity.ExtensionTokenListBean;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.LoginBean;
import com.zhiting.clouddisk.entity.LoginEntity;
import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.entity.mine.DiskListBean;
import com.zhiting.clouddisk.entity.mine.FolderDetailBean;
import com.zhiting.clouddisk.entity.mine.FolderListBean;
import com.zhiting.clouddisk.entity.mine.FolderSettingBean;
import com.zhiting.clouddisk.entity.mine.MemberDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolListBean;
import com.zhiting.clouddisk.request.AddPartitionRequest;
import com.zhiting.clouddisk.request.AddStoragePoolRequest;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.CreateStoragePoolRequest;
import com.zhiting.clouddisk.request.LoginRequest;
import com.zhiting.clouddisk.request.ModifyNameRequest;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.MoveCopyRequest;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.clouddisk.request.UpdateFolderPwdRequest;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.entity.ChannelEntity;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiService {

    /**
     * 文件列表  需要密码
     * @param scopeToken
     * @param path
     * @param map
     * @return
     */
    @GET(HttpUrlParams.RESOURCES+"{path}")
    Observable<BaseResponseEntity<FileListBean>> getFiles(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path(HttpUrlParams.PATH) String path, @QueryMap Map<String, String> map);

    /**
     * 文件列表
     * @param scopeToken
     * @param path
     * @param map
     * @return
     */
    @GET(HttpUrlParams.RESOURCES+"{path}")
    Observable<BaseResponseEntity<FileListBean>> getFiles(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Header(HttpUrlParams.PWD) String pwd,
                                                            @Path(HttpUrlParams.PATH) String path, @QueryMap Map<String, String> map);

    /**
     * 创建文件夹
     * @param scopeToken
     * @param path
     * @return
     */
    @POST(HttpUrlParams.RESOURCES+"{path}")
    Observable<BaseResponseEntity<UploadCreateFileBean>> createFile(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Header(HttpUrlParams.PWD) String pwd, @Path(HttpUrlParams.PATH) String path);


    /**
     * 重命名
     *
     * @param scopeToken
     * @param path
     * @param name
     * @return
     */
    @PUT(HttpUrlParams.RESOURCES+"{path}")
    Observable<BaseResponseEntity<Object>> renameFile(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path(HttpUrlParams.PATH) String path, @Body NameRequest name);

    /**
     * SA成员列表
     * @param scopeToken
     * @return
     */
    @GET(HttpUrlParams.USERS)
    Observable<BaseResponseEntity<MemberBean>> getMembers(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken);

    /**
     * 共享
     * @param scopeToken
     * @param shareRequest
     * @return
     */
    @POST(HttpUrlParams.SHARES)
    Observable<BaseResponseEntity<Object>> share(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body ShareRequest shareRequest);

    /**
     * 删除文件/目录
     * @param scopeToken
     * @return
     */
    @HTTP(method = "DELETE", path = HttpUrlParams.RESOURCE, hasBody = true)
    Observable<BaseResponseEntity<Object>> removeFile(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body ShareRequest shareRequest);


    /**
     * 获取共享文件夹目录
     * @param scopeToken
     * @return
     */
    @GET(HttpUrlParams.SHARES)
    Observable<BaseResponseEntity<FileListBean>> getShareFolders(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken);

    /**
     * 移动复制文件
     * @param scopeToken
     * @return
     */
    @PATCH(HttpUrlParams.RESOURCE)
    Observable<BaseResponseEntity<Object>> moveCopyFile(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body MoveCopyRequest moveCopyRequest);

    /**
     * 存储池列表
     * @param scopeToken
     * @param map
     * @return
     */
    @GET(HttpUrlParams.POOLS)
    Observable<BaseResponseEntity<StoragePoolListBean>> getStoragePoolList(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @QueryMap Map<String, String> map);

    /**
     * 存储池详情
     * @param scopeToken
     * @param name
     * @return
     */
    @GET(HttpUrlParams.POOL_DETAIL+"{name}")
    Observable<BaseResponseEntity<StoragePoolDetailBean>> getStoragePoolDetail(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path(HttpUrlParams.NAME) String name);

    /**
     * 闲置硬盘列表
     * @param scopeToken
     * @return
     */
    @GET(HttpUrlParams.DISK)
    Observable<BaseResponseEntity<DiskListBean>> getDisks(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken);

    /**
     * 选择硬盘创建存储池
     * @param scopeToken
     * @param storagePoolRequest
     * @return
     */
    @POST(HttpUrlParams.CREATE_POOLS)
    Observable<BaseResponseEntity<Object>> createStoragePool(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body CreateStoragePoolRequest storagePoolRequest);

    /**
     * 添加硬盘到存储池
     * @param scopeToken
     * @param storagePoolRequest
     * @return
     */
    @POST(HttpUrlParams.ADD_DISKS_POOL)
    Observable<BaseResponseEntity<Object>> addStoragePool(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body AddStoragePoolRequest storagePoolRequest);

    /**
     * 编辑\重命名存储池
     * @param scopeToken
     * @param name
     * @param modifyNameRequest
     * @return
     */
    @PUT(HttpUrlParams.MODIFY_POOLS_NAME+"{name}")
    Observable<BaseResponseEntity<Object>> modifyPoolName(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("name") String name, @Body ModifyNameRequest modifyNameRequest);

    /**
     * 删除存储池
     * @param scopeToken
     * @param name
     * @return
     */
    @DELETE(HttpUrlParams.REMOVE_POOLS+"{name}")
    Observable<BaseResponseEntity<Object>> removePool(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("name") String name);

    /**
     * 添加分区
     * @param scopeToken
     * @param addPartitionRequest
     * @return
     */
    @POST(HttpUrlParams.PARTITIONS)
    Observable<BaseResponseEntity<Object>> addPartition(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body AddPartitionRequest addPartitionRequest);

    /**
     * 编辑分区
     * @param scopeToken
     * @param modifyPartitionRequest
     * @return
     */
    @PUT(HttpUrlParams.MODIFY_PARTITIONS+"{name}")
    Observable<BaseResponseEntity<Object>> modifyPartition(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("name") String name, @Body ModifyPartitionRequest modifyPartitionRequest);

    /**
     * 删除分区
     * @param scopeToken
     * @param name
     * @return
     */
    @HTTP(method = "DELETE", path = HttpUrlParams.MODIFY_PARTITIONS+"{name}", hasBody = true)
    Observable<BaseResponseEntity<Object>> removePartition(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("name") String name, @Body PoolNameRequest poolNameRequest);

    /**
     * 解密文件夹
     * @param scopeToken
     * @param path
     * @param checkPwdRequest
     * @return
     */
    @PATCH(HttpUrlParams.DECRYPT_FILE+"{path}")
    Observable<BaseResponseEntity<Object>> decryptFile(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("path") String path, @Body CheckPwdRequest checkPwdRequest);


    /**
     * 文件夹列表
     * @param scopeToken
     * @param map
     * @return
     */
    @GET(HttpUrlParams.FOLDER_LIST)
    Observable<BaseResponseEntity<FolderListBean>> getFolderList(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @QueryMap Map<String, String> map);


    /**
     * 文件夹修改密码
     * @param scopeToken
     * @param updateFolderPwdRequest
     * @return
     */
    @POST(HttpUrlParams.UPDATE_FOLDER_PWD)
    Observable<BaseResponseEntity<Object>> updateFolderPwd(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body UpdateFolderPwdRequest updateFolderPwdRequest);

    /**
     * 文件夹详情
     * @param scopeToken
     * @return
     */
    @GET(HttpUrlParams.FOLDER_DETAIL+"{id}")
    Observable<BaseResponseEntity<FolderDetailBean>> getFolderDetail(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") long id);

    /**
     * 添加文件夹
     * @param scopeToken
     * @param folder
     * @return
     */
    @POST(HttpUrlParams.ADD_FOLDER)
    Observable<BaseResponseEntity<Object>> addFolder(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body FolderDetailBean folder);

    /**
     * 删除文件夹
     * @param scopeToken
     * @param id
     * @return
     */
    @DELETE(HttpUrlParams.DEL_FOLDER+"{id}")
    Observable<BaseResponseEntity<Object>> removeFolder(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") long id);

    /**
     * 修改文件夹
     * @param scopeToken
     * @param id
     * @return
     */
    @PUT(HttpUrlParams.EDIT_FOLDER+"{id}")
    Observable<BaseResponseEntity<Object>> updateFolder(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") long id, @Body FolderDetailBean folder);

    /**
     * 获取设置
     * @param scopeToken
     * @return
     */
    @GET(HttpUrlParams.FOLDER_SETTING)
    Observable<BaseResponseEntity<FolderSettingBean>> getFolderSettingData(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken);

    /**
     * 修改文件夹设置
     * @param scopeToken
     * @param folderSettingBean
     * @return
     */
    @POST(HttpUrlParams.FOLDER_SETTING)
    Observable<BaseResponseEntity<Object>> saveFolderSetting(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Body FolderSettingBean folderSettingBean);

    /**
     * 删除任务
     * @param scopeToken
     * @param id
     * @return
     */
    @DELETE(HttpUrlParams.TASKS+"{id}")
    Observable<BaseResponseEntity<Object>> removeTask(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") String id);

    /**
     * 重新开始任务
     * @param scopeToken
     * @param id
     * @return
     */
    @PUT(HttpUrlParams.TASKS+"{id}")
    Observable<BaseResponseEntity<Object>> restartTask(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") String id);

    /**
     * 用户详情
     * @param scopeToken
     * @param id
     * @return
     */
    @GET(HttpUrlParams.USER_DETAIL+"{id}")
    Observable<BaseResponseEntity<MemberDetailBean>> getUserDetail(@Header(HttpUrlParams.SCOPE_TOKEN) String scopeToken, @Path("id") int id);

    /**
     * 获取临时通道
     * @param areaId
     * @param cookie
     * @param map
     * @return
     */
    @GET(HttpUrlParams.TEMP_CHANNEL)
    Observable<BaseResponseEntity<ChannelEntity>> getTempChannel(@Header("Area-ID") String areaId, @Header("Cookie") String cookie, @QueryMap Map<String, String> map);

    /**
     * 登录(废弃）
     * @param loginRequest
     * @return
     */
    @POST(HttpUrlParams.LOGIN2)
    Observable<BaseResponseEntity<LoginBean>> login(@Body LoginRequest loginRequest);

    /**
     * 登录
     * @param loginRequest
     * @return
     */
    @POST(HttpUrlParams.LOGIN2)
    Observable<BaseResponseEntity<LoginEntity>> login2(@Body LoginRequest loginRequest);

    /**
     * 通过sc获取所有家庭扩展应用的token
     * @param id
     * @param type
     * @return
     */
    @GET(HttpUrlParams.SC_URL + HttpUrlParams.USERS +"/" + "{id}" +HttpUrlParams.EXTENSION +"/" + "{type}" + HttpUrlParams.TOKENS)
    Observable<BaseResponseEntity<ExtensionTokenListBean>> getExtensionTokenList(@Path("id") int id, @Path("type") int type);

    @POST(HttpUrlParams.LOGOUT)
    Observable<BaseResponseEntity<Object>> logout();
}
