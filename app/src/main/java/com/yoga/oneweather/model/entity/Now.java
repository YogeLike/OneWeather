package com.yoga.oneweather.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyg on 2017/7/29.
 */

public class Now {

    @SerializedName("cond")
    public NowCondition now_cond;
    public class NowCondition {
        public String code;
        @SerializedName("txt")
        public String cond_text;
    }

    @SerializedName("fl")
    public String feel_tmp;

    @SerializedName("hum")
    public String humidity;

    public String tmp;

    public Wind wind;
    public class Wind {
        public String dir;
        @SerializedName("sc")
        public String scale;
        public String spd;

    }


}
