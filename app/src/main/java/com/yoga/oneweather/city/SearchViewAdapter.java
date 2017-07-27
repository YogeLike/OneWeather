package com.yoga.oneweather.city;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yoga.oneweather.R;
import com.yoga.oneweather.model.db.CityDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyg on 2017/7/24.
 */

public class SearchViewAdapter extends RecyclerView.Adapter {
    private final int ITEM_HEADER = 0;
    private final int ITEM_CITY = 1;
    public final static int LOCATE_SUCCESS = 111;
    public final static int LOCATE_ING  = 222;
    public final static int LOCATE_FAILED = 333;


    private int locateState = LOCATE_ING;

    private List<CityInfoData> cityInfoDataList;//所有城市
    private ArrayList<Pair<String,String>> hotCities ;//热门城市
    private CityDao locateCity;//定位城市

    private OnCityClickListener onCityClickListener;
    private OnLocateClickListener onLocateClickListener;

    public SearchViewAdapter(List<CityInfoData> cityInfoDataList){
        this.cityInfoDataList = cityInfoDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder viewHolder;
        if(viewType == ITEM_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_header, parent, false);
            viewHolder = new HeaderHolder(view);

            //初始化hotCityRecyclerView
            init_hotCities();
            HotCityAdapter hotCityAdapter = new HotCityAdapter(hotCities);
            hotCityAdapter.setOnHotCityClickListener(new OnCityClickListener() {
                @Override
                public void onCityClick(String cityId) {
                    onCityClickListener.onCityClick(cityId);
                }

            });
            ((HeaderHolder)viewHolder).header_recyclerView.setAdapter(hotCityAdapter);


            //在这里注册定位文本框的点击事件
            ((HeaderHolder)viewHolder).locateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocateClickListener.onLocateClick();
                }
            });

        }else {//所有城市列表
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city,parent,false);
            viewHolder = new CityHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCityClickListener.onCityClick((cityInfoDataList.get(viewHolder.getLayoutPosition()-ITEM_CITY).getmCityId()));
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            //更新定位文本信息
            if(locateState == LOCATE_FAILED){
                ((HeaderHolder)holder).locateText.setText(R.string.located_failed);
            }else if(locateState == LOCATE_ING){
                ((HeaderHolder)holder).locateText.setText(R.string.locating);
            }else {
                ((HeaderHolder)holder).locateText.setText(locateCity.getCityName());
            }
        }else if(holder instanceof CityHolder) {
            CityInfoData city = cityInfoDataList.get(position-ITEM_CITY);
            ((CityHolder)holder).itemCityName.setText(city.getmCityName());
            if(!(city.getmInitial().equals("#"))){
                ((CityHolder) holder).itemLetter.setText(city.getmInitial());
                ((CityHolder) holder).itemLetter.setVisibility(View.VISIBLE);//设置字母可见
            }else {
                ((CityHolder) holder).itemLetter.setVisibility(View.GONE);//记得加这句，唉。不然会，导致有字母的部位乱飞,holder可能有共用内存的情况
            }
        }

    }

    @Override
    public int getItemCount() {
        return cityInfoDataList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return position==0 ? ITEM_HEADER : ITEM_CITY;

    }

    public void updateLocateState(int state ,CityDao city){
        this.locateState = state;
        this.locateCity = city;
    }

    private void init_hotCities(){
        hotCities = new ArrayList<>();
        hotCities.add(new Pair<>("北京", "CN101010100"));
        hotCities.add(new Pair<>("上海", "CN101020100"));
        hotCities.add(new Pair<>("广州", "CN101280101"));
        hotCities.add(new Pair<>("深圳", "CN101280601"));
        hotCities.add(new Pair<>("杭州", "CN101210101"));
        hotCities.add(new Pair<>("南京", "CN101190101"));
        hotCities.add(new Pair<>("武汉", "CN101200101"));
        hotCities.add(new Pair<>("重庆", "CN101040100"));

    }


    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        this.onCityClickListener = onCityClickListener;
    }
    public void setOnLocateClickListener(OnLocateClickListener onLocateClickListener) {
        this.onLocateClickListener = onLocateClickListener;
    }
    public int getLocateState() {
        return locateState;
    }

    public CityDao getLocateCity() {
        return locateCity;
    }

    public interface OnLocateClickListener{
        void onLocateClick();
    }


}
