package com.zhiting.networklib.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

    private static final String DEFAULT_SHARE_NAME = "clouddisk";
    private static SharedPreferences sharedPreferences;
    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences(DEFAULT_SHARE_NAME,Context.MODE_PRIVATE);
    }

    public static void put(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public static void put(String key, int value){
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static void put(String key, boolean value){
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public static int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }
}
