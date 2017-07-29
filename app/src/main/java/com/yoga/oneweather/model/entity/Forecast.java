package com.yoga.oneweather.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyg on 2017/7/29.
 */

public class Forecast {
    public Astro astro;
    public class Astro{
        @SerializedName("sr")
        public String sunrise;
        @SerializedName("ss")
        public String sunset;
    }
    @SerializedName("cond")
    public DailyCondition daily_cond;
    public class DailyCondition{

        public String code_d;
        public String txt_d;

        public String code_n;
        public String txt_n;
    }
    public String date;
    public String uv;

    @SerializedName("tmp")
    public Temperature daily_tmp;
    public class Temperature {
        @SerializedName("max")
        public String max_tmp;
        @SerializedName("min")
        public String min_tmp;
    }
    public Wind wind;
    public class Wind {
        public String dir;
        @SerializedName("sc")
        public String scale;
        public String spd;

    }





}
