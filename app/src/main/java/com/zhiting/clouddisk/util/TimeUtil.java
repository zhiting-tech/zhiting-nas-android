package com.zhiting.clouddisk.util;

public class TimeUtil {

    /**
     * 获取当前系统时间戳
     * @return
     */
    public static long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }

    /**
     * 时间戳转秒
     * @param time
     * @return
     */
    public static long currentTimeMills2Second(long time){
        return time/1000;
    }

    /**
     * 时间戳转分钟
     * @param time
     * @return
     */
    public static long currentTimeTills2Minute(long time){
        return time/1000/60;
    }

    /**
     * 时间戳转时
     * @param time
     * @return
     */
    public static long currentTime2Hour(long time){
        return time/1000/(60*60);
    }

    /**
     * 时间戳转天
     * @param time
     * @return
     */
    public static long currentTime2Day(long time){
        return time/1000/(60*60*24);
    }

    /**
     * 是否超过72小时
     * @param time
     * @return
     */
    public static boolean over72hour(long time){
        return time>(60*60*1000*72);
    }
}
