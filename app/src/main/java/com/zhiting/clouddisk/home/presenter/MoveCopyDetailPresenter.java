package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.UploadCreateFileBean;
import com.zhiting.clouddisk.home.contract.MoveCopyDetailContract;
import com.zhiting.clouddisk.home.contract.MoveCopyFileContract;
import com.zhiting.clouddisk.home.model.MoveCopyDetailModel;
import com.zhiting.clouddisk.home.model.MoveCopyFileModel;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.clouddisk.request.MoveCopyRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.List;
import java.util.Map;

public class MoveCopyDetailPresenter extends BasePresenter<MoveCopyDetailModel, MoveCopyDetailContract.View> implements MoveCopyDetailContract.Presenter {

    @Override
    public MoveCopyDetailModel createModel() {
        return new MoveCopyDetailModel();
    }

    /**
     * 文件
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
                        mView.getFilesSuccess(response);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                    }
                });
            }
        });
    }

    /**
     * 文件列表 有密码
     *
     * @param scopeToken
     * @param pwd
     * @param path
     * @param map
     * @param showLoading
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
     * 共享文件夹列表
     *
     * @param scopeToken
     * @param showLoading
     */
    @Override
    public void getShareFolders(String scopeToken, boolean showLoading) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.getShareFolders(scopeToken), new RequestDataCallback<FileListBean>(showLoading) {
                    @Override
                    public void onSuccess(FileListBean response) {
                        super.onSuccess(response);
                        mView.getFilesSuccess(response);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
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
     * 移动复制
     *
     * @param scopeToken
     * @param moveCopyRequest
     */
    @Override
    public void moveCopyFile(String scopeToken, MoveCopyRequest moveCopyRequest) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeObservable(mModel.moveCopyFile(scopeToken, moveCopyRequest), new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.moveCopySuccess();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (mView != null) {
                            mView.moveCopyFail(errorCode, errorMessage);
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
                executeObservable(mModel.decryptFile(scopeToken, path, checkPwdRequest), new RequestDataCallback<Object>() {
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
     * 获取本地保存的文件夹密码
     *
     * @param scopeToken
     * @param path
     */
    @Override
    public void getFolderPwdByScopeTokenAndPath(String scopeToken, String path) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeDBObservable(mModel.getFolderPwdByScopeTokenAndPath(scopeToken, path), new RequestDataCallback<FolderPassword>() {
                    @Override
                    public void onSuccess(FolderPassword response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            mView.getFolderPwdByScopeTokenAndPathSuccess(response);
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        if (mView != null) {
                            mView.getFolderPwdByScopeTokenAndPathFail();
                        }
                    }
                });
            }
        });
    }

    /**
     * 插入文件夹密码
     *
     * @param folderPassword
     */
    @Override
    public void insertFolderPwd(FolderPassword folderPassword) {
        checkTempChannel(new OnTempChannelListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                executeDBObservable(mModel.insertFolderPwd(folderPassword), new RequestDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        super.onSuccess(response);
                        if (mView != null) {
                            if (response) {
                                mView.insertFolderPwdSuccess(true);
                            } else {
                                mView.insertFolderFail();
                            }
                        }

                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        if (mView != null) {
                            mView.insertFolderFail();
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
