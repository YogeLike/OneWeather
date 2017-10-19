package com.yoga.oneweather.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.yoga.oneweather.R;

/**
 * Created by wyg on 2017/8/12.
 */

public class SettingActivity extends AppCompatActivity{
    private PreferenceFragment pf;

    public static void actionStart(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("    设置");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <23) {
            getWindow().setStatusBarColor(0x22000000);//浅黑
        } else if (Build.VERSION.SDK_INT >= 23) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
