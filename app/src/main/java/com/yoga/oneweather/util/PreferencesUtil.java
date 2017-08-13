package com.yoga.oneweather.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yoga.oneweather.MyApplication;

/**
 * Created by wyg on 2017/7/26.
 */

public class PreferencesUtil {

    public static SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
    }

    public static void put(String key,String value){
        getPreferences().edit().putString(key,value).apply();

    }
    public static String get(String key,String defValue){
        return getPreferences().getString(key,defValue);
    }
    public static void put(String key,boolean value){
        getPreferences().edit().putBoolean(key,value).apply();

    }
    public static Boolean get(String key,boolean defValue){
        return getPreferences().getBoolean(key,defValue);
    }
    public static void put(String key,int value){
        getPreferences().edit().putInt(key,value).apply();

    }
    public static int get(String key,int defValue){
        return getPreferences().getInt(key,defValue);
    }





}
