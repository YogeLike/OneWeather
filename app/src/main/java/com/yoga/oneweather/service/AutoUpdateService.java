package com.yoga.oneweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.yoga.oneweather.model.db.DBManager;
import com.yoga.oneweather.model.entity.weather.Weather;
import com.yoga.oneweather.util.Constant;
import com.yoga.oneweather.util.HttpUtil;
import com.yoga.oneweather.util.PreferencesUtil;
import com.yoga.oneweather.util.JSONHandleUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String hour = PreferencesUtil.get("auto_update_rate","6");
        int h = Integer.parseInt(hour);
        int updateRate = h * 60 * 60 * 1000 ;
        long triggerAtTime = SystemClock.elapsedRealtime() + updateRate ;

        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);//取消之前的
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather() {
        String weatherString = PreferencesUtil.get("weather",null);
        if(weatherString != null){
            Weather weather = JSONHandleUtil.handleWeatherResponse(weatherString);
            String cityId = weather.basic.id;
            String address = Constant.WEATHER_URL+"weather?CityDao="+cityId+"&key="+Constant.WEATHER_KEY;
            HttpUtil.sendOkHttpClient(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String newWeatherString = response.body().string();
                    Weather newWeather = JSONHandleUtil.handleWeatherResponse(newWeatherString);
                    if(newWeather != null && "ok".equals(newWeather.status)){
                        PreferencesUtil.put("weather",newWeatherString);
                        DBManager.getInstance().saveOrUpdateCityWeather(newWeather);//更新或保存天气
                    }


                }
            });
        }
    }
}
