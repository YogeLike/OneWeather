package com.yoga.oneweather.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyg on 2017/7/21.
 */

public class City {
    /**
     * city: 南子岛
     * cnty : 中国
     * id : CN101310230
     * lat : 11.26
     * lon : 114.20
     * prov : 海南
     */
    @SerializedName("city")
    private String cityName;
    private String cnty;
    private String id;
    private String lat;
    private String lon;
    private String prov;

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }



    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String pro) {
        this.prov = pro;
    }
}