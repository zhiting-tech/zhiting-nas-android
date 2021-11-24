package com.zhiting.clouddisk.home.contract;

import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;

public interface HomeContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FileListBean>> getFiles(String scopeToken, String path, Map<String, String> map);
        public abstract Observable<BaseResponseEntity<Object>> decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest);
        public abstract Observable<FolderPassword> getFolderPwdByScopeTokenAndPath(String scopeToken, String path);
        public abstract Observable<Boolean> insertFolderPwd(FolderPassword folderPassword);
        public abstract Observable<Boolean> updateFolderPwd(FolderPassword folderPassword);
    }

    interface Presenter extends IPresenter<View> {
        void getFiles(String scopeToken, String path, Map<String, String> map, boolean showLoading);
        void decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest);
        void getFolderPwdByScopeTokenAndPath(String scopeToken, String path);
        void insertFolderPwd(FolderPassword folderPassword);
        void updateFolderPwd(FolderPassword folderPassword);
    }

    interface View extends IView {
        void getFilesSuccess(FileListBean files);
        void getFilesFail(int errorCode, String msg);
        void decryptPwdSuccess();
        void decryptPwdFail(int errorCode, String msg);
        void getFolderPwdByScopeTokenAndPathSuccess(FolderPassword folderPassword);
        void getFolderPwdByScopeTokenAndPathFail();
        void insertFolderPwdSuccess(boolean result);
        void insertFolderFail();
        void updateFolderPwdSuccess();
        void updateFolderPwdFail();
    }
}
