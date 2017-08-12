package com.yoga.oneweather.city;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yoga.oneweather.MyApplication;
import com.yoga.oneweather.R;
import com.yoga.oneweather.model.db.DBManager;
import com.yoga.oneweather.model.db.FollowedCityWeather;
import com.yoga.oneweather.util.Constant;

import java.util.List;

/**
 * Created by wyg on 2017/8/11.
 */

public class FollowedCityAdapter extends RecyclerView.Adapter {


    private boolean isDeleteing = false;
    private List<FollowedCityWeather> cities;



    private OnCityClickListener onCityClickListener;


    private OnCityLongClickListener onCityLongClickListener;

    public FollowedCityAdapter(List<FollowedCityWeather> cities){
        this.cities = cities;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_followed_city,parent,false);
        final FollowedCityHolder holder = new FollowedCityHolder(item);
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onCityLongClickListener.onCityLongClick();
                return true;
            }
        });
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDeleteing){
                    String cityId = cities.get(holder.getLayoutPosition()).getCityId();
                    onCityClickListener.onCityClick(cityId);//在activity里实现
                }

            }
        });


        holder.unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cities.remove(holder.getLayoutPosition());
                DBManager.getInstance().deleteFollowedCity((String) holder.cityName.getText());
                FollowedCityAdapter.this.notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        List<FollowedCityWeather> cityWeathers = DBManager.getInstance().findAllFollowedCities();
        FollowedCityWeather cityWeather = cityWeathers.get(position);
        ((FollowedCityHolder)holder).cityName.setText(cityWeather.getCityName());
        ((FollowedCityHolder)holder).cityTmp.setText(cityWeather.getMinTmp()+"° /"+cityWeather.getMaxTmp()+"°");
        Glide.with(MyApplication.getContext()).load(Constant.WEATHER_ICON_URL + cityWeather.getCondition_code() +".png").into(((FollowedCityHolder) holder).weatherImage);

        ((FollowedCityHolder)holder).unfollowButton.setVisibility(isDeleteing ? View.VISIBLE : View.GONE);
        ((FollowedCityHolder)holder).cityTmp.setVisibility(isDeleteing ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public boolean isDeleteing() {
        return isDeleteing;
    }

    public void setDeleteing(boolean deleteing) {
        isDeleteing = deleteing;
    }


    public OnCityClickListener getOnCityClickListener() {
        return onCityClickListener;
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        this.onCityClickListener = onCityClickListener;
    }

    public OnCityLongClickListener getOnCityLongClickListener() {
        return onCityLongClickListener;
    }

    public void setOnCityLongClickListener(OnCityLongClickListener onCityLongClickListener) {
        this.onCityLongClickListener = onCityLongClickListener;
    }

    public interface OnCityLongClickListener {
        void onCityLongClick();
    }
}
