package com.zhiting.networklib.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//类作用描述：1.接收主module传递的上下文，提供给lib内部需要context的工具类使用
//            2.提供activity堆栈管理的方法
//            3.提供一个获取主线程id的方法以及mMainThreadHandler，可以处理一些延时操作等
public class LibLoader {
    private static Stack<Activity> activityStack;    //Activity的栈
    private static List<Activity> fileDetailActStack;    //文件详情Activity的栈
    private static List<Activity> moveCopyDetailActList;    //移动复制文件详情Activity的栈

    private static int mMainThreadId = -1;      //主线程ID

    private static Handler mMainThreadHandler;  //主线程Handler  //后面替换为evenbus

    private static Application mContext;    //应用全局的上下文

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }


    public LibLoader() {

    }

    /**
     * 在自定义的Application初始化时调用, 初始化一些变量
     *
     * @param context 应用全局的上下文
     */
    public static void init(Application context) {
        mContext = context;
        init();

    }

    private static void init() {
        mMainThreadId = Process.myTid();
        mMainThreadHandler = new Handler();
    }

    public static Application getApplication() {
        return mContext;
    }

    /**
     * 获取主线程ID
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }


    /**
     * 把Activity添加到栈
     */
    public static void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 移除Activity
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前的Activity,栈中最后一个压入, 有可能为null
     *
     * @return
     */
    public static Activity getCurrentActivity() {
        if (activityStack != null && !activityStack.isEmpty()) {
            Activity activity = activityStack.lastElement();
            return activity;
        }
        return null;
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {    //判断传入的Activity是否为空
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    /**
     * 判断指定类名的Activity是否已经存在
     *
     * @param cls
     * @return
     */
    public static boolean isExist(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 除某个activity之外，全部结束
     * @param cls
     */
    public static void finishAllActivityExcludeCertain(Class<?> cls){
        for (Activity activity : activityStack) {
            if (!activity.getClass().equals(cls)) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有的Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {    //栈的每一项不为空
                activityStack.get(i).finish();//结束每一项
            }
        }
        activityStack.clear();
    }

    public static void finishByPosition(int pos){
        Activity activity = activityStack.get(activityStack.size()-pos);
        if (activity!=null){
            activity.finish();
        }
    }

    /**
     * 文件详情
     * @param activity
     */
    public static void addFileDetailAct(Activity activity){
        if (fileDetailActStack == null){
            fileDetailActStack = new ArrayList<>();
        }
        fileDetailActStack.add(activity);
    }

    /**
     * 移除文件详情
     */
    public static void finishFileDetailAct(){
        if (fileDetailActStack!=null){
            Activity activity = fileDetailActStack.get(fileDetailActStack.size()-1);
            fileDetailActStack.remove(fileDetailActStack.size()-1);
            activity.finish();
        }
    }


    /**
     * 文件详情
     * @param activity
     */
    public static void addMoveCopyDetailAct(Activity activity){
        if (moveCopyDetailActList == null){
            moveCopyDetailActList = new ArrayList<>();
        }
        moveCopyDetailActList.add(activity);
    }

    /**
     * 移除文件详情
     */
    public static void finishMoveCopyDetailAct(){
        if (moveCopyDetailActList!=null){
            Activity activity = moveCopyDetailActList.get(moveCopyDetailActList.size()-1);
            moveCopyDetailActList.remove(moveCopyDetailActList.size()-1);
            activity.finish();
        }
    }

    /**
     * 关闭所有移动复制
     */
    public static void finishAllCopyDetailAct(){
        if (CollectionUtil.isNotEmpty(moveCopyDetailActList)){
            for (int i=0; i<moveCopyDetailActList.size(); i++){
                Activity activity = moveCopyDetailActList.get(i);
                moveCopyDetailActList.remove(i);
                activity.finish();
            }

        }
    }

    public static void exitAPP(){
        finishAllActivity();
        System.exit(0);
        Process.killProcess(Process.myPid());
    }
}
