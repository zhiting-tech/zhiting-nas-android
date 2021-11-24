package com.zhiting.clouddisk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.rx.RxDao;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;


public class DBManager {

    private final static String dbName = "cloud_disk_db";
    private static DBManager instance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DBManager(Context context){
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 单例
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context){
        if (instance == null){
            synchronized (DBManager.class){
                if (instance == null){
                    instance = new DBManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取可读数据库
     * @return
     */
    private SQLiteDatabase getReadableDataBase(){
        if (openHelper == null){
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     * @return
     */
    private SQLiteDatabase getWriteableDataBase(){
        if (openHelper == null){
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条文件夹密码记录
     * @param folderPassword
     */
    public long insertFolderPassword(FolderPassword folderPassword){
        DaoMaster daoMaster = new DaoMaster(getWriteableDataBase());
        DaoSession daoSession = daoMaster.newSession();
        FolderPasswordDao folderPasswordDao = daoSession.getFolderPasswordDao();
        return folderPasswordDao.insert(folderPassword);
    }

    /**
     * 删除一条文件夹密码记录
     * @param folderPassword
     */
    public void deleteFolderPassword(FolderPassword folderPassword){
        DaoMaster daoMaster = new DaoMaster(getWriteableDataBase());
        DaoSession daoSession = daoMaster.newSession();;
        FolderPasswordDao folderPasswordDao = daoSession.getFolderPasswordDao();
        folderPasswordDao.delete(folderPassword);
    }

    /**
     * 更新一条文件夹密码记录
     * @param folderPassword
     */
    public void updateFolderPassword(FolderPassword folderPassword){
        DaoMaster daoMaster = new DaoMaster(getWriteableDataBase());
        DaoSession daoSession = daoMaster.newSession();;
        FolderPasswordDao folderPasswordDao = daoSession.getFolderPasswordDao();
        folderPasswordDao.update(folderPassword);

    }

    /**
     * 根据路径和scopeToken查询数据
     * @return
     */
    public FolderPassword getFolderPasswordByScopeTokenAndPath(String scopeToken, String path){
        DaoMaster daoMaster = new DaoMaster(getWriteableDataBase());
        DaoSession daoSession = daoMaster.newSession();;
        FolderPasswordDao folderPasswordDao = daoSession.getFolderPasswordDao();
        FolderPassword folderPassword = folderPasswordDao.queryBuilder().where(FolderPasswordDao.Properties.ScopeToken.eq(scopeToken), FolderPasswordDao.Properties.Path.eq(path)).unique();
        return folderPassword;
    }

    /**
     * 通过rxjava插入一条文件夹密码记录
     * @param folderPassword
     * @return
     */
    public Observable<Boolean> insertFolderPwd(FolderPassword folderPassword){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                if (folderPassword!=null){
                    long id = insertFolderPassword(folderPassword);
                    emitter.onNext(id>0 ? true : false);
                }else {
                    emitter.onNext(false);
                }
            }
        });
    }

    /**
     * 通过rxjava更新一条文件夹密码记录
     * @param folderPassword
     * @return
     */
    public Observable<Boolean> updateFolderPwd(FolderPassword folderPassword){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                if (folderPassword!=null){
                    updateFolderPassword(folderPassword);
                    emitter.onNext(true);
                }else {
                    emitter.onNext(false);
                }
            }
        });
    }


    /**
     * 通过rxjava根据路径和scopeToken查询数据
     * @param scopeToken
     * @param path
     * @return
     */
    public Observable<FolderPassword> getFolderPwdBySPOb(String scopeToken, String path){
        return Observable.create(new ObservableOnSubscribe<FolderPassword>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<FolderPassword> emitter) throws Throwable {
                FolderPassword folderPassword = getFolderPasswordByScopeTokenAndPath(scopeToken, path);
                emitter.onNext(folderPassword);
            }
        });
    }

}
