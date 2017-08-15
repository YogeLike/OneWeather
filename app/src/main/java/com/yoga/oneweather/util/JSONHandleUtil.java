package com.yoga.oneweather.util;

import com.google.gson.reflect.TypeToken;
import com.yoga.oneweather.MyApplication;
import com.yoga.oneweather.model.entity.city.City;
import com.yoga.oneweather.model.entity.city.County;
import com.yoga.oneweather.model.entity.city.Province;
import com.yoga.oneweather.model.entity.weather.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyg on 2017/7/29.
 */

public class JSONHandleUtil {
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject  = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherString = jsonArray.getJSONObject(0).toString();
            return MyApplication.getGson().fromJson(weatherString,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<County> handleCitiesJSONData(String allCities){
        try {
            List<County> countyList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(allCities);//省份组合
            List<Province> provinceList = MyApplication.getGson().fromJson(jsonArray.toString(),new TypeToken<List<Province>>(){}.getType());
            for(Province province : provinceList){
                for(City city : province.cities){
                    for(County county : city.counties){
                        countyList.add(county);
                    }
                }
            }
            return countyList;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
