package com.zhiting.clouddisk.home.model;

import com.zhiting.clouddisk.CDApplication;
import com.zhiting.clouddisk.api.ApiService;
import com.zhiting.clouddisk.db.DBManager;
import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.home.contract.HomeContract;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.http.RetrofitManager;

import org.greenrobot.greendao.rx.RxDao;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class HomeModel extends HomeContract.Model {

    public HomeModel(){ }

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
