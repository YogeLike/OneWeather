package com.yoga.oneweather.city;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoga.oneweather.R;

/**
 * Created by wyg on 2017/8/11.
 */

public class FollowedCityHolder extends RecyclerView.ViewHolder {
    ImageView weatherImage;
    TextView cityName;
    TextView cityTmp;
    ImageButton unfollowButton;

    public FollowedCityHolder(View itemView){
        super(itemView);
        weatherImage = (ImageView) itemView.findViewById(R.id.weather_image);
        cityName = (TextView)itemView.findViewById(R.id.followed_city_name);
        cityTmp  = (TextView) itemView.findViewById(R.id.followed_city_tmp);
        unfollowButton = (ImageButton) itemView.findViewById(R.id.unfollow_button);
    }

}
