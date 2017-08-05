package com.yoga.oneweather.util;

import com.google.gson.Gson;
import com.yoga.oneweather.model.entity.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wyg on 2017/7/29.
 */

public class WeatherHandleUtil {
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject  = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherString = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherString,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
