package com.zhiting.clouddisk.home.presenter;

import com.zhiting.clouddisk.db.FolderPassword;
import com.zhiting.clouddisk.entity.BannerBean;
import com.zhiting.clouddisk.entity.FileListBean;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.home.contract.HomeContract;
import com.zhiting.clouddisk.home.model.HomeModel;
import com.zhiting.clouddisk.main.contract.MainContract;
import com.zhiting.clouddisk.request.CheckPwdRequest;
import com.zhiting.networklib.base.mvp.BasePresenter;
import com.zhiting.networklib.base.mvp.RequestDataCallback;

import java.util.List;
import java.util.Map;

/**
 * 文件的presenter层
 */
public class HomePresenter extends BasePresenter<HomeModel, HomeContract.View> implements HomeContract.Presenter {

    private int failCount;//失败次数

    @Override
    public HomeModel createModel() {
        return new HomeModel();
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
                        failCount = 0;
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        failCount++;
                        if (failCount <= 3) {
                            getFiles(scopeToken, path, map, false);
                        } else {
                            mView.getFilesFail(errorCode, errorMessage);
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
     * 获取本地保存的文件夹密码
     *
     * @param scopeToken
     * @param path
     */
    @Override
    public void getFolderPwdByScopeTokenAndPath(String scopeToken, String path) {
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

    /**
     * 插入文件夹密码
     *
     * @param folderPassword
     */
    @Override
    public void insertFolderPwd(FolderPassword folderPassword) {
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

    /**
     * 修改文件夹密码
     *
     * @param folderPassword
     */
    @Override
    public void updateFolderPwd(FolderPassword folderPassword) {
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
}
