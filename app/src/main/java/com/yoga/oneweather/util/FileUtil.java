package com.yoga.oneweather.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wyg on 2017/7/21.
 */

public class FileUtil {
    private FileUtil(){

    }

    public static File getExternalCacheDir(Context context){
        return context.getExternalCacheDir();
    }

    public static String assertFile2String(String fileName,Context context){
        InputStream is = null;
        try{
            is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
