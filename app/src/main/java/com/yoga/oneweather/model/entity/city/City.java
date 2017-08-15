package com.yoga.oneweather.model.entity.city;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wyg on 2017/8/15.
 */

public class City {
    public String cityName;
    @SerializedName("county")
    public List<County> counties;
}
