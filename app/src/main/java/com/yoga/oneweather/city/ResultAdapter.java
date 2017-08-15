package com.yoga.oneweather.city;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yoga.oneweather.R;
import com.yoga.oneweather.model.db.CityDao;

import java.util.List;

/**
 * Created by wyg on 2017/7/25.
 */

public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<CityDao> resultList;


    private OnCityClickListener onCityClickListener;
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView cityName;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = (TextView) itemView.findViewById(R.id.item_city_name);
        }
    }

    public ResultAdapter(List<CityDao> resultCities){
        this.resultList = resultCities;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCityClickListener.onCityClick(resultList.get(holder.getLayoutPosition()).getCityId());
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).cityName.setText(resultList.get(position).getCityName());

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        this.onCityClickListener = onCityClickListener;
    }

}
