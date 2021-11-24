package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.home.contract.FileDetailContract;
import com.zhiting.clouddisk.home.model.FileDetailModel;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.NameRequest;
import com.zhiting.clouddisk.request.ShareRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

public class FileDetailPresenter extends BasePresenter<FileDetailModel, FileDetailContract.View> implements FileDetailContract.Presenter {

    @Override
    public FileDetailModel createModel() {
        return new FileDetailModel();
    }


    /**
     * 文件列表
     *
     * @param scopeToken
     * @param path
     * @param map
     */
    @Override
    public void getFiles(String scopeToken, String path, Map<String, String> map, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getFiles(scopeToken, path, map), new RequestDataCallback<FileListBean>(showLoading) {
                    @Override
                    public void onSuccess(FileListBean response) {
                        super.onSuccess(response);
                        if (mView != null)
                            mView.getFilesSuccess(response);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null)
                            mView.getFilesFail(errorCode, errorMessage);
                    }
                });
            }
        });
    }

    /**
     * 文件列表 有密码参数
     *
     * @param scopeToken  凭证
     * @param pwd         密码
     * @param path        路径
     * @param map         分页参数
     * @param showLoading 是否显示加载弹窗
     */
    @Override
    public void getFiles(String scopeToken, String pwd, String path, Map<String, String> map, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getFiles(scopeToken, pwd, path, map), new RequestDataCallback<FileListBean>(showLoading) {
                    @Override
                    public void onSuccess(FileListBean response) {
                        super.onSuccess(response);
                        if (mView != null)
                            mView.getFilesSuccess(response);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null)
                            mView.getFilesFail(errorCode, errorMessage);
                    }
                });
            }
        });
    }

    /**
     * 创建文件夹
     *
     * @param scopeToken
     * @param path
     */
    @Override
    public void createFile(String scopeToken, String pwd, String path) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.createFile(scopeToken, pwd, path), new RequestDataCallback<UploadCreateFileBean>() {
                    @Override
                    public void onSuccess(UploadCreateFileBean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.createFileSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.createFileFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 重命名
     *
     * @param scopeToken
     * @param path
     * @param name
     */
    @Override
    public void renameFile(String scopeToken, String path, NameRequest name) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.renameFile(scopeToken, path, name), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.renameSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.renameFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除文件/文件夹
     *
     * @param scopeToken
     */
    @Override
    public void removeFile(String scopeToken, ShareRequest shareRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.removeFile(scopeToken, shareRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.removeFileSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.removeFileFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }


    /**
     * 解密文件夹
     *
     * @param scopeToken
     * @param path
     * @param checkPwdRequest
     */
    @Override
    public void decryptFile(String scopeToken, String path, CheckPwdRequest checkPwdRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.decryptFile(scopeToken, path, checkPwdRequest), new RequestDataCallback<Object>(false) {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.decryptPwdSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.decryptPwdFail(errorCode, errorMessage);
                        }
                    }
                });
            }
        });
    }


    /**
     * 修改文件夹密码
     *
     * @param folderPassword
     */
    @Override
    public void updateFolderPwd(FolderPassword folderPassword) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeDBObservable(mModel.updateFolderPwd(folderPassword), new RequestDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            if (response) {
                                mView.updateFolderPwdSuccess();
                            } else {
                                mView.updateFolderPwdFail();
                            }
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        if (mView != null) {
                            mView.updateFolderPwdFail();
                        }
                    }
                });
            }
        });
    }
}
