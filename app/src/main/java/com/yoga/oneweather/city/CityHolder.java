package com.yoga.oneweather.city;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yoga.oneweather.R;

/**
 * Created by wyg on 2017/7/24.
 */

public class CityHolder extends RecyclerView.ViewHolder {
    TextView itemCityName;
    TextView itemLetter;


    public CityHolder(View itemView) {
        super(itemView);
        itemCityName = (TextView) itemView.findViewById(R.id.item_city_name);
        itemLetter = (TextView) itemView.findViewById(R.id.item_city_letter);
    }




}
