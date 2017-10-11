package com.yoga.oneweather.model.db;

import android.os.Environment;
import android.widget.Toast;

import com.yoga.oneweather.MyApplication;
import com.yoga.oneweather.model.entity.city.County;
import com.yoga.oneweather.model.entity.weather.Weather;
import com.yoga.oneweather.ui.WeatherActivity;
import com.yoga.oneweather.util.FileUtil;
import com.yoga.oneweather.util.JSONHandleUtil;
import com.yoga.oneweather.util.LogUtil;
import com.yoga.oneweather.util.PreferencesUtil;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

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
    private static final String TAG = "DBManager";
    private String DB_NAME = "one_weather";

    private DBManager() {

    }

    public static DBManager getInstance() {
        if (dbManager == null) {
            synchronized (DBManager.class) {
                if (dbManager == null) {
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }

    public void copyCitysToDB() {//将所有城市保存到数据库

        boolean cityInited = PreferencesUtil.get(CITY_INITED, false);

        if (!cityInited) {
            File file = new File(DB_PATH, DB_NAME);
            if (file.exists()) {//未初始化却存在
                file.delete();
            }


            String allcities = FileUtil.assertFile2String("ChinaCityList.json", MyApplication.getContext());
            List<County> countyList = JSONHandleUtil.handleCitiesJSONData(allcities);

            //对数据进行排序后存入数据库
            Collections.sort(countyList, new CountyComparator());
            List<CityDao> cityDaoList = new ArrayList<>();
            for (County county : countyList) {

                CityDao cityDao = new CityDao();
                cityDao.setCityName(county.countyName);

                cityDao.setCityId(county.countyNo);
                cityDao.setPinyin(county.countyPY);
                cityDaoList.add(cityDao);

            }

            DataSupport.saveAllAsync(cityDaoList).listen(new SaveCallback() {
                @Override
                public void onFinish(boolean success) {
                    if (success) {
                        LogUtil.d("SaveCityDao", "onFinish: " + "Success");
                        PreferencesUtil.put(CITY_INITED, true);

                    } else {
                        LogUtil.d("SaveCityDao", "Failed");
                        Toast.makeText(MyApplication.getContext(), "城市数据初始化失败,跳转至默认城市,\n重启可重新初始化", Toast.LENGTH_LONG).show();
                        WeatherActivity.actionStart(MyApplication.getContext(), "CN101280101");

                    }
                }
            });

        }


    }

    private List<CityDao> getCities(final String keyword) {
        List<CityDao> cityDaoList;
        //long timeMills = System.currentTimeMillis();
        if (keyword == null) {
            cityDaoList = DataSupport.findAll(CityDao.class);
        } else {
            cityDaoList = DataSupport.where("cityName like ? or pinyin like ?", "%" + keyword + "%", "%" + keyword + "%").order("pinyin").find(CityDao.class);
        }
        // LogUtil.d(TAG, "getCities: "+(System.currentTimeMillis()-timeMills)+"ms");


        if (keyword == null) {
            String lastInital = "";
            String currentInital;
            for (CityDao city : cityDaoList) {
                currentInital = city.getPinyin().substring(0, 1);
                if (!currentInital.equals(lastInital)) {
                    LogUtil.d("getCities:", "last:" + lastInital + " current:" + currentInital);
                    lastInital = currentInital;
                    city.setmInitial(currentInital.toUpperCase());
                }
            }
        }
        return cityDaoList;
    }

    public CityDao findCity(String city) {
        List<CityDao> cityId = DataSupport.where("cityname = ?", city).limit(1).find(CityDao.class);
        if (cityId.size() != 0) {
            return cityId.get(0);
        } else {
            return null;
        }
    }

    public List<CityDao> getAllCities() {
        return getCities(null);
    }

    public List<CityDao> getSearchCities(String keyword) {
        return getCities(keyword);
    }


    public List<FollowedCityWeather> getCitiesWeather() {

        return DataSupport.findAll(FollowedCityWeather.class);
    }


    public void saveOrUpdateCityWeather(Weather weather) {
        FollowedCityWeather cityWeather = new FollowedCityWeather();
        cityWeather.setCityName(weather.basic.city);
        cityWeather.setCondition(weather.now.now_cond.cond_text);
        cityWeather.setMaxTmp(weather.forecastList.get(0).daily_tmp.max_tmp);
        cityWeather.setMinTmp(weather.forecastList.get(0).daily_tmp.min_tmp);
        cityWeather.setCondition_code(weather.now.now_cond.code);
        cityWeather.setCityId(weather.basic.id);
        cityWeather.saveOrUpdate("cityName = ?", cityWeather.getCityName());//保存或者更新
    }

    public void deleteFollowedCity(String cityName) {
        DataSupport.deleteAll(FollowedCityWeather.class, "cityName = ?", cityName);
    }

    public List<FollowedCityWeather> findAllFollowedCities() {
        return DataSupport.findAll(FollowedCityWeather.class);
    }

    /**
     * a-z 排序,比较器
     */
    private class CountyComparator implements Comparator<County> {

        @Override
        public int compare(County c1, County c2) {
            int l1 = c1.countyPY.length();
            int l2 = c2.countyPY.length();
            int shortLength = l1 > l2 ? l2 : l1;
            return c1.countyPY.substring(0, shortLength).compareTo(c2.countyPY.substring(0, shortLength));
        }
    }
}
