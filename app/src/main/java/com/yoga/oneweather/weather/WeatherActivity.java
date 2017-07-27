package com.yoga.oneweather.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yoga.oneweather.R;

public class WeatherActivity extends AppCompatActivity {


    public static void actionStart(Context context,String cityId){
        Intent intent = new Intent(context,WeatherActivity.class);
        intent.putExtra("CityId",cityId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.d("WeatherActivity:","CITYID:"+getIntent().getStringExtra("CityId"));
    }
}
