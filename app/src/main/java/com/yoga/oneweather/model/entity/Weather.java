package com.yoga.oneweather.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wyg on 2017/7/29.
 */

public class Weather {
    public String status;
    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    public Now now;
    public Suggestion suggestion;
}
