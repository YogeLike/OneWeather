package com.yoga.oneweather.city;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yoga.oneweather.R;

import java.util.List;

/**
 * Created by wyg on 2017/7/24.
 */

public class HotCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Pair<String,String>> hotCities;


    private OnCityClickListener onHotCityClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView hotCityText;
        public ViewHolder(View itemView) {
            super(itemView);
            hotCityText = (TextView) itemView.findViewById(R.id.hot_city_name);
        }
    }

    public HotCityAdapter(List<Pair<String,String>> cities){
        this.hotCities = cities;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_city,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.hotCityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHotCityClickListener.onCityClick(hotCities.get(holder.getLayoutPosition()).second);//在外层实现

            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).hotCityText.setText(hotCities.get(position).first);

    }

    @Override
    public int getItemCount() {
        return hotCities.size();
    }



    public void setOnHotCityClickListener(OnCityClickListener onHotCityClickListener) {
        this.onHotCityClickListener = onHotCityClickListener;
    }
}
