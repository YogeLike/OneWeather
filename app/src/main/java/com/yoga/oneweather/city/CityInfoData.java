package com.yoga.oneweather.city;

/**
 * Created by wyg on 2017/7/25.
 */

public class CityInfoData {
    private String mInitial = "#";
    private String mCityName;
    private String mCityPinyin;
    private String mCityId;

    public CityInfoData(String cityName,String cityNamePinyin,String cityId){
        this.mCityName = cityName;
        this.mCityPinyin = cityNamePinyin;
        this.mCityId = cityId;
    }

    public String getmInitial() {
        return mInitial;
    }

    public String getmCityName() {
        return mCityName;
    }

    public String getmCityPinyin() {
        return mCityPinyin;
    }

    public String getmCityId() {
        return mCityId;
    }

    public void setmInitial(String mInitial) {
        this.mInitial = mInitial;
    }
}
