package com.zhiting.clouddisk.home.contract;

import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BaseModel;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;
import com.zhiting.networklib.base.mvp.IPresenter;
import com.zhiting.networklib.base.mvp.IView;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;

public interface FileDetailContract {
    abstract class Model extends BaseModel {
        public abstract Observable<BaseResponseEntity<FileListBean>> getFiles(String scopeToken, String path, Map<String, String> map); // 文件列表，不用密码
        public abstract Observable<BaseResponseEntity<FileListBean>> getFiles(String scopeToken, String pwd, String path, Map<String, String> map);  // 文件列表，要密码
        public abstract Observable<BaseResponseEntity<UploadCreateFileBean>> createFile(String scopeToken, String pwd, String path);
        public abstract Observable<BaseResponseEntity<Object>> renameFile(String scopeToken, String path, NameRequest name);
        public abstract Observable<BaseResponseEntity<Object>> removeFile(String scopeToken, ShareRequest shareRequest);
        public abstract Observable<BaseResponseEntity<Object>> decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest);
        public abstract Observable<Boolean> updateFolderPwd(FolderPassword folderPassword);

    }

    interface Presenter extends IPresenter<View> {
        void getFiles(String scopeToken, String path, Map<String, String> map, boolean showLoading); // 文件列表，不用密码
        void getFiles(String scopeToken, String pwd, String path, Map<String, String> map, boolean showLoading); // 文件列表，要密码
        void createFile(String scopeToken, String pwd, String path);
        void renameFile(String scopeToken, String path, NameRequest name);
        void removeFile(String scopeToken, ShareRequest shareRequest);
        void decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest);
        void updateFolderPwd(FolderPassword folderPassword);
    }

    interface View extends IView {
        void getFilesSuccess(FileListBean files);
        void getFilesFail(int errorCode, String msg);
        void createFileSuccess(UploadCreateFileBean uploadCreateFileBean);
        void createFileFail(int errorCode, String msg);
        void renameSuccess();
        void renameFail(int errorCode, String msg);
        void removeFileSuccess();
        void removeFileFail(int errorCode, String msg);
        void decryptPwdSuccess();
        void decryptPwdFail(int errorCode, String msg);
        void updateFolderPwdSuccess();
        void updateFolderPwdFail();
    }
}
