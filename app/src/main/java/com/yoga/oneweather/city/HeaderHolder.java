package com.yoga.oneweather.city;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yoga.oneweather.MyApplication;
import com.yoga.oneweather.R;

/**
 * Created by wyg on 2017/7/24.
 */

public class HeaderHolder extends RecyclerView.ViewHolder {
    RecyclerView header_recyclerView;
    TextView locateText;



    public HeaderHolder(View itemView) {
        super(itemView);
        header_recyclerView = (RecyclerView) itemView.findViewById(R.id.header_recyclerView);
        locateText = (TextView) itemView.findViewById(R.id.located_city);

        header_recyclerView.setLayoutManager(new GridLayoutManager(MyApplication.getContext(),3));
        //setAdapter 在SearchViewAdapter 中createviewholder中完成
    }



}
