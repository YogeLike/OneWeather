package com.yoga.oneweather;

import android.app.Application;
import android.content.Context;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;
import com.google.gson.Gson;
import com.yoga.oneweather.model.db.DBManager;

import org.litepal.LitePal;

/**
 * Created by wyg on 2017/7/21.
 */

public class MyApplication extends Application {

    private static Context context;
    private static Gson mGson = new Gson();



    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(context)));
        DBManager.getInstance().copyCitysToDB();


    }

    public static Gson getGson() {
        return mGson;
    }

    public static Context getContext() {
        return context;
    }
}
