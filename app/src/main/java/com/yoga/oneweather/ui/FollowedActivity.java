package com.yoga.oneweather.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.yoga.oneweather.R;
import com.yoga.oneweather.city.FollowedCityAdapter;
import com.yoga.oneweather.city.OnCityClickListener;
import com.yoga.oneweather.model.db.DBManager;

public class FollowedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FollowedCityAdapter adapter;
    private LinearLayout linearLayout;
    private Toolbar followed_cities_toolbar;
    private  ActionBar actionBar;
    private ImageButton save_button;
    private ImageButton edit_button;
    private ImageButton add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed);
        followed_cities_toolbar = (Toolbar) findViewById(R.id.followed_city_toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.followed_city_layout);
        setSupportActionBar(followed_cities_toolbar);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        if(Build.VERSION.SDK_INT >= 21){
            linearLayout.setFitsSystemWindows(true);//5.0以上的显示状态栏
        }
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <23) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(0x22000000);//浅黑
        } else if (Build.VERSION.SDK_INT >= 23) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        recyclerView = (RecyclerView) findViewById(R.id.followed_city_recyclerView);
        save_button = (ImageButton) findViewById(R.id.save_city_change);
        edit_button = (ImageButton) findViewById(R.id.edit_city);
        add_button = (ImageButton)findViewById(R.id.add_city);

        init();
    }

    private void init() {

        adapter = new FollowedCityAdapter(DBManager.getInstance().getCitiesWeather());
        adapter.setOnCityClickListener(new OnCityClickListener() {
            @Override
            public void onCityClick(String cityId) {
                WeatherActivity.actionStart(FollowedActivity.this,cityId);
            }
        });
        adapter.setOnCityLongClickListener(new FollowedCityAdapter.OnCityLongClickListener() {
            @Override
            public void onCityLongClick() {
                goToDeleteingStatus();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//vertical
        recyclerView.setAdapter(adapter);


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToNotDeleteingStatus();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowedActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDeleteingStatus();
            }
        });
    }

    private void goToDeleteingStatus() {
        adapter.setDeleteing(true);
        adapter.notifyDataSetChanged();
        save_button.setVisibility(View.VISIBLE);
        edit_button.setVisibility(View.GONE);
        add_button.setVisibility(View.INVISIBLE);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void backToNotDeleteingStatus(){
        adapter.setDeleteing(false);
        adapter.notifyDataSetChanged();
        save_button.setVisibility(View.GONE);
        edit_button.setVisibility(View.VISIBLE);
        add_button.setVisibility(View.VISIBLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if(adapter.isDeleteing()){
            backToNotDeleteingStatus();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://HomeAsUp
                finish();
                break;
            default:
        }
        return true;
    }

}
