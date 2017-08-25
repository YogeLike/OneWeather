package com.yoga.oneweather.model.entity.city;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wyg on 2017/8/15.
 */

public class Province {
    public String provinceName;
    @SerializedName("city")
    public List<City> cities;


}
