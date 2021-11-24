package com.zhiting.clouddisk.home.model;

import com.zhiting.clouddisk.CDApplication;
import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.home.contract.MoveCopyDetailContract;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.MoveCopyRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


public class MoveCopyDetailModel extends MoveCopyDetailContract.Model {


    /**
     * 文件
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
     * 共享文件夹
     * @param scopeToken
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<FileListBean>> getShareFolders(String scopeToken) {
        return ApiServiceFactory.getApiService().getShareFolders(scopeToken);
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
     * 移动复制文件夹
     * @param scopeToken
     * @param moveCopyRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> moveCopyFile(String scopeToken, MoveCopyRequest moveCopyRequest) {
        return ApiServiceFactory.getApiService().moveCopyFile(scopeToken, moveCopyRequest);
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
     * 查询本地保存的文件夹密码
     * @param scopeToken
     * @param path
     * @return
     */
    @Override
    public Observable<FolderPassword> getFolderPwdByScopeTokenAndPath(String scopeToken, String path) {
        return CDApplication.getDBManager().getFolderPwdBySPOb(scopeToken, path);
    }

    /**
     * 插入文件夹密码
     * @param folderPassword
     * @return
     */
    @Override
    public Observable<Boolean> insertFolderPwd(FolderPassword folderPassword) {
        return CDApplication.getDBManager().insertFolderPwd(folderPassword);
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
