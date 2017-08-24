package com.yoga.oneweather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.yoga.oneweather.R;
import com.yoga.oneweather.util.LogUtil;

/**
 * Created by wyg on 2017/8/12.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if("auto_update_rate".equals(key)){
            Preference update_rate = findPreference(key);
            update_rate.setSummary(sharedPreferences.getString(key,"6")+"小时");
            LogUtil.d("SettingFragment", "onSharedPreferenceChanged: ");
        }

    }
}
