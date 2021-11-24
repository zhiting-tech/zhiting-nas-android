package com.zhiting.clouddisk.home.model;

import com.zhiting.clouddisk.CDApplication;
import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.home.contract.FileDetailContract;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;

/**
 * 文件夹详情
 */
public class FileDetailModel extends FileDetailContract.Model {

    public FileDetailModel(){ }

    /**
     * 文件列表，没密码
     * @param scopeToken
     * @param path
     * @param map
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FileListBean>> getFiles(String scopeToken, String path, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getFiles(scopeToken, path, map);
    }

    /**
     * 文件列表，有密码
     * @param scopeToken
     * @param pwd
     * @param path
     * @param map
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FileListBean>> getFiles(String scopeToken, String pwd, String path, Map<String, String> map) {
        return ApiServiceFactory.getApiService().getFiles(scopeToken, pwd, path, map);
    }

    /**
     * 创建文件夹
     * @param scopeToken
     * @param path
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<UploadCreateFileBean>> createFile(String scopeToken, String pwd, String path) {
        return ApiServiceFactory.getApiService().createFile(scopeToken, pwd, path);
    }

    /**
     * 重命名
     * @param scopeToken
     * @param path
     * @param name
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> renameFile(String scopeToken, String path, NameRequest name) {
        return ApiServiceFactory.getApiService().renameFile(scopeToken, path, name);
    }

    /**
     * 删除文件/目录
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removeFile(String scopeToken, ShareRequest shareRequest) {
        return ApiServiceFactory.getApiService().removeFile(scopeToken, shareRequest);
    }

    /**
     * 解密文件夹
     * @param scopeToken
     * @param path
     * @param checkPwdRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest) {
        return ApiServiceFactory.getApiService().decryptFile(scopeToken, path, checkPwdRequest);
    }

    /**
     * 修改文件夹密码
     * @param folderPassword
     * @return
     */
    @Override
    public Observable<Boolean> updateFolderPwd(FolderPassword folderPassword) {
        return CDApplication.getDBManager().updateFolderPwd(folderPassword);
    }
}
