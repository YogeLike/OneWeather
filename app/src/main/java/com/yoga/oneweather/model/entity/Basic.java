package com.yoga.oneweather.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyg on 2017/7/29.
 */

public class Basic {
    public String city;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
