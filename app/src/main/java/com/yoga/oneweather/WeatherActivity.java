package com.yoga.oneweather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yoga.oneweather.model.entity.AQI;
import com.yoga.oneweather.model.entity.Forecast;
import com.yoga.oneweather.model.entity.Weather;
import com.yoga.oneweather.service.AutoUpdateService;
import com.yoga.oneweather.util.Constant;
import com.yoga.oneweather.util.HttpUtil;
import com.yoga.oneweather.util.PreferencesUtil;
import com.yoga.oneweather.util.VisibilityCheckUtil;
import com.yoga.oneweather.util.WeatherHandleUtil;
import com.yoga.oneweather.widget.CircleProgress;
import com.yoga.oneweather.widget.SunriseSunset;
import com.yoga.oneweather.widget.Windmill;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.yoga.oneweather.util.VisibilityCheckUtil.ALL_INVISIBLE;
import static com.yoga.oneweather.util.VisibilityCheckUtil.ALL_VISIBLE;

public class WeatherActivity extends AppCompatActivity {


    private Weather mWeather;
    private ImageView bg_image;
    private static final float BITMAP_SCALE = 0.3f;
    private static final float BLUR_RADIUS = 6.5f;

    private SwipeRefreshLayout swipeRefresh;
    private Button choose_city;
    private TextView city_name;
    private Button settings;
    private TextView update_time;
    private TextView now_degree;
    private TextView now_air;
    private TextView now_condition;
    private TextView now_today_tmp;
    private LinearLayout forcastLayout;
    private CircleProgress aqi_circle;
    private TextView aqi_pm25;
    private TextView aqi_pm10;
    private TextView aqi_no2;
    private TextView aqi_so2;
    private TextView aqi_o3;
    private TextView aqi_co;
    private CircleProgress comf_circle;
    private TextView comf_feel_tmp;
    private TextView comf_uv;
    private Windmill windmill_big;
    private Windmill windmill_small;
    private TextView wind_dir;
    private TextView wind_sc;
    private TextView wind_spd;
    private SunriseSunset sunriseSunset;
    private ScrollView scrollView;
    private static final String TAG = "WeatherActivity";
    public static void actionStart(Context context,String cityId){
        Intent intent = new Intent(context,WeatherActivity.class);
        intent.putExtra("CityId",cityId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        bg_image = (ImageView) findViewById(R.id.bg_image);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);


        choose_city = (Button) findViewById(R.id.choose_city);
        settings = (Button) findViewById(R.id.settings);
        city_name = (TextView) findViewById(R.id.city_name);

        update_time = (TextView) findViewById(R.id.update_time);
        now_degree = (TextView) findViewById(R.id.now_centi_degree_text);
        now_air = (TextView) findViewById(R.id.now_air);
        now_condition = (TextView)findViewById(R.id.now_cond_text);
        now_today_tmp = (TextView) findViewById(R.id.now_today_tmp);
        forcastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqi_circle = (CircleProgress) findViewById(R.id.aqi_circle);
        aqi_pm25 = (TextView) findViewById(R.id.aqi_pm25);
        aqi_so2 = (TextView) findViewById(R.id.aqi_so2);
        aqi_no2 = (TextView) findViewById(R.id.aqi_no2);
        aqi_o3 = (TextView) findViewById(R.id.aqi_o3);
        aqi_co = (TextView) findViewById(R.id.aqi_co);
        aqi_pm10 = (TextView) findViewById(R.id.aqi_pm10);
        comf_circle = (CircleProgress) findViewById(R.id.comf_circle);
        comf_feel_tmp = (TextView) findViewById(R.id.comf_feel_tmp);
        comf_uv = (TextView) findViewById(R.id.comf_uv_index);
        windmill_big = (Windmill) findViewById(R.id.windmill_big);
        windmill_small = (Windmill) findViewById(R.id.windmill_small);
        wind_dir = (TextView) findViewById(R.id.wind_dir);
        wind_sc = (TextView) findViewById(R.id.wind_sc);
        wind_spd = (TextView) findViewById(R.id.wind_spd);
        sunriseSunset = (SunriseSunset) findViewById(R.id.sunmove);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        scrollView. getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            boolean[] flags = {false,false,false,false,false};
            View[] views = {aqi_circle,comf_circle,windmill_big,windmill_small,sunriseSunset};
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onScrollChanged() {
               // Rect scrollBounds = new Rect();
               // scrollView.getHitRect(scrollBounds);
                //Log.d(TAG, "onScrollChanged: HitRect:"+scrollBounds.toString());

                for(int i = 0 ;i< views.length;i++){
                    View nowView = views[i];
                    int visibility = VisibilityCheckUtil.checkVisibility(nowView);
                    if(!flags[i] && visibility==ALL_VISIBLE ){
                        try {

                            Class<?> cls = nowView.getClass();
                            Method method = cls.getDeclaredMethod("startAnimation", (Class<?>[]) null);//new Class<?>[0]
                            method.invoke(nowView);
                            flags[i]=true;
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else if(visibility==ALL_INVISIBLE) {
                        nowView.clearAnimation();
                        flags[i]=false;
                    }
                }

            }
        });

        String cityId = getIntent().getStringExtra("CityId");
        String weatherString = PreferencesUtil.get("weather",null);
        if(cityId != null){//由intent启动
            swipeRefresh.setRefreshing(true);
            requestWeather(cityId);

        }else {//不由intent启动
            if(weatherString == null){//没缓存(第一次启动)
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                finish();
            }else {//有缓存
                mWeather = WeatherHandleUtil.handleWeatherResponse(weatherString);
                showWeatherInfo(mWeather);
                cityId = mWeather.basic.id;
            }
        }

        final String finalCityId = cityId;
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(finalCityId);
            }
        });



        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.clouds);
        bitmap = blur(bitmap);
        bg_image.setImageBitmap(bitmap);


    }

    private Bitmap blur(Bitmap bitmap) {//对背景图进行高斯模糊

        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*BITMAP_SCALE), (int) (bitmap.getHeight()*BITMAP_SCALE),true);

        RenderScript rs = RenderScript.create(this);
        Allocation allocation = Allocation.createFromBitmap(rs,bitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs,allocation.getElement());
        blur.setInput(allocation);
        blur.setRadius(BLUR_RADIUS);
        blur.forEach(allocation);
        allocation.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }

    private void requestWeather(String cityId) {
        //https://free-api.heweather.com/v5/weather?city= &key=1c8bebce635648809e98babb820856c9
        String address = Constant.WEATHER_URL+"weather?city="+cityId+"&key="+Constant.WEATHER_KEY;
        HttpUtil.sendOkHttpClient(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String respon = response.body().string();
                Log.d(TAG, "onResponse: "+respon);
                mWeather = WeatherHandleUtil.handleWeatherResponse(respon);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mWeather != null && "ok".equals(mWeather.status)) {
                            PreferencesUtil.put("weather", respon);
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);

                            showWeatherInfo(mWeather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败,", Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {

        boolean flag = false;
        String uv_index = "";
        String sunriseTime = "";
        String sunsetTime = "";
        String nowTodayTemp = "";
        city_name.setText(weather.basic.city);

        String updateTime = weather.basic.update.updateTime.split(" ")[1] + "发布";
        String nowAir = "空气  "+weather.aqi.city.qlty;
        String now_cond_code = weather.now.now_cond.code;
        String now_tmp = weather.now.tmp+"°";

        update_time.setText(updateTime);
        now_degree.setText(now_tmp);
        now_condition.setText(weather.now.now_cond.cond_text);
        now_air.setText(nowAir);
        forcastLayout.removeAllViews();//清空，不然会越来越多
        for(Forecast forecast : weather.forecastList){
            Log.d(TAG, "showWeatherInfo: "+forecast.toString());
            if(flag == false){
                uv_index = forecast.uv;
                sunriseTime = forecast.astro.sunrise;
                sunsetTime = forecast.astro.sunset;
                nowTodayTemp = forecast.daily_tmp.min_tmp+"° / "+forecast.daily_tmp.max_tmp+"°";
                now_today_tmp.setText(nowTodayTemp);
                flag = true;
            }
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false);
            TextView date = (TextView) view.findViewById(R.id.date_text);
            TextView con_text = (TextView) view.findViewById(R.id.condition_text);
            TextView daily_tmp = (TextView) view.findViewById(R.id.daily_tmp);
            ImageView con_img = (ImageView) view.findViewById(R.id.condition_image);

            String[] s = forecast.date.split("-");
            String dailyTemp = forecast.daily_tmp.min_tmp+"° ~ "+forecast.daily_tmp.max_tmp+"°";
            date.setText(s[1]+"月"+s[2]+"日");
            con_text.setText(forecast.daily_cond.txt_d);
            daily_tmp.setText(dailyTemp);
            Glide.with(this).load(Constant.WEATHER_ICON_URL + forecast.daily_cond.code_d +".png").into(con_img);
            forcastLayout.addView(view);
        }

        AQI.AQICity city = weather.aqi.city;
        String pm10 = "PM10    "+city.pm10;
        String pm25 = "PM2.5   "+city.pm25;
        String co = "CO          "+city.co;
        String no2 = "NO₂        "+city.no2;
        String o3 = "O₃           "+city.o3;
        String so2 = "SO₂        "+city.so2;
        aqi_circle.setValue(Integer.parseInt(city.aqi));
        aqi_circle.setHint(city.qlty);
        aqi_pm10.setText(pm10);
        aqi_pm25.setText(pm25);
        aqi_co.setText(co);
        aqi_no2.setText(no2);
        aqi_o3.setText(o3);
        aqi_so2.setText(so2);

        comf_circle.setValue(Integer.parseInt(weather.now.humidity));
        String feel_tmp = "体感温度"+"      "+weather.now.feel_tmp+"  ℃";
        String uv_text = "紫外线指数"+"  "+uv_index+"  "+weather.suggestion.uv.brf;
        comf_feel_tmp.setText(feel_tmp);
        comf_uv.setText(uv_text);

        windmill_big.setWindSpeed(Integer.parseInt(weather.now.wind.spd));
        windmill_small.setWindSpeed(Integer.parseInt(weather.now.wind.spd));
        String spd = "风速    "+weather.now.wind.spd+"km/h";
        String scale = "风力    "+weather.now.wind.scale;
        String dir = "风向    "+weather.now.wind.dir;
        wind_spd.setText(spd);
        wind_sc.setText(scale);
        wind_dir.setText(dir);

        SimpleDateFormat formater = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        String nowTime = formater.format(curDate);
        Log.d(TAG, "showWeatherInfo: "+sunriseTime + "  "+ sunsetTime+"  "+ nowTime);
        sunriseSunset.setTime(sunriseTime,sunsetTime,nowTime);

    }

}
