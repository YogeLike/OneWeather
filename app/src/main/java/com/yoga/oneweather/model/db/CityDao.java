package com.yoga.oneweather.model.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wyg on 2017/7/21.
 */

public class CityDao extends DataSupport {
    /**
     * city_unselected : 南子岛
     * cnty : 中国
     * cityID : CN101310230
     * lat : 11.26
     * lon : 114.20
     * prov : 海南
     */
    private String cityName;
    private String cityId;
    private String pinyin;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String id) {
        this.cityId = id;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
