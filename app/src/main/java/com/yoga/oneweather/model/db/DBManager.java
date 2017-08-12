package com.yoga.oneweather.model.db;

import android.os.Environment;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.reflect.TypeToken;
import com.yoga.oneweather.MyApplication;
import com.yoga.oneweather.city.CityInfoData;
import com.yoga.oneweather.model.entity.City;
import com.yoga.oneweather.model.entity.Weather;
import com.yoga.oneweather.util.FileUtil;
import com.yoga.oneweather.util.PreferencesUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wyg on 2017/7/21.
 */

public class DBManager {
    private static DBManager dbManager;
    private String CITY_INITED = "CITY_INITED";
    private String DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + MyApplication.getContext().getPackageName() + File.separator + "databases";
    private String DB_NAME = "one_weather";
    private DBManager(){

    }

    public static DBManager getInstance(){
        if(dbManager == null){
            synchronized (DBManager.class){
                if(dbManager==null){
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }

    public void copyCitysToDB(){//将所有城市保存到数据库

        boolean cityInited = PreferencesUtil.get(CITY_INITED,false);

        if(!cityInited){
            File file = new File(DB_PATH,DB_NAME);
            if(file.exists()){//未初始化却存在
                file.delete();
            }



            String citys = FileUtil.assertFile2String("cityList.json", MyApplication.getContext());
            List<City> cityList = MyApplication.getGson().fromJson(citys, new TypeToken<List<City>>() {
            }.getType());

            //对数据进行排序后存入数据库
            Collections.sort(cityList, new CityComparator());

            for (City city : cityList) {
                String pinyin = Pinyin.toPinyin(city.getCityName(), "");

                CityDao cityDao = new CityDao();
                cityDao.setCityName(city.getCityName());

                cityDao.setCityId(city.getId());
                cityDao.setPinyin(pinyin);
                cityDao.save();
            }
            PreferencesUtil.put(CITY_INITED, true);
        }



    }

    private List<CityInfoData> getCities(final String keyword){
        List<CityDao> cityDaoList;
        if(keyword == null){
            cityDaoList = DataSupport.findAll(CityDao.class);
        }else {
            cityDaoList = DataSupport.where("cityName like ? or pinyin like ?", "%"+keyword+"%", "%"+keyword+"%").order("pinyin").find(CityDao.class);
        }


        String lastInital = "";
        String currentInital;
        List<CityInfoData> result = new ArrayList<>();

        for(CityDao cityDao : cityDaoList){
            CityInfoData city = new CityInfoData(cityDao.getCityName(),cityDao.getPinyin(),cityDao.getCityId());
            currentInital = cityDao.getPinyin().substring(0,1);
            if((!(currentInital.equals(lastInital))) && keyword == null){
                Log.d("getCities:", "last:"+lastInital+" current:"+currentInital);
                lastInital = currentInital;
                city.setmInitial(currentInital);

            }
            result.add(city);
        }
        return result;
    }

    public CityDao findCity(String city){
        List<CityDao> cityId = DataSupport.where("cityname = ?", city).limit(1).find(CityDao.class);
        if(cityId.size()!=0){
            return cityId.get(0);
        }else {
            return null;
        }
    }

    public List<CityInfoData> getAllCities(){
        return getCities(null);
    }

    public List<CityInfoData> getSearchCities(String keyword){
        return getCities(keyword);
    }


    public List<FollowedCityWeather> getCitiesWeather(){
        return DataSupport.findAll(FollowedCityWeather.class);
    }


    public void saveOrUpdateCityWeather(Weather weather){
        FollowedCityWeather cityWeather = new FollowedCityWeather();
        cityWeather.setCityName(weather.basic.city);
        cityWeather.setCondition(weather.now.now_cond.cond_text);
        cityWeather.setMaxTmp(weather.forecastList.get(0).daily_tmp.max_tmp);
        cityWeather.setMinTmp(weather.forecastList.get(0).daily_tmp.min_tmp);
        cityWeather.setCondition_code(weather.now.now_cond.code);
        cityWeather.setCityId(weather.basic.id);
        cityWeather.saveOrUpdate("cityName = ?",cityWeather.getCityName());//保存或者更新
    }

    public void deleteFollowedCity(String cityName){
        DataSupport.deleteAll(FollowedCityWeather.class,"cityName = ?",cityName);
    }
    public List<FollowedCityWeather> findAllFollowedCities(){
        return DataSupport.findAll(FollowedCityWeather.class);
    }

    /**
     * a-z 排序,比较器
     */
    private class CityComparator implements Comparator<City>{

        @Override
        public int compare(City c1, City c2) {
            String a = Pinyin.toPinyin(c1.getCityName(),"");
            String b = Pinyin.toPinyin(c2.getCityName(),"");

            return a.compareTo(b);
        }
    }
}
