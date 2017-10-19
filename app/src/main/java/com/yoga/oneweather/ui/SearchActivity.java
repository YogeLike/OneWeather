package com.yoga.oneweather.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yoga.oneweather.R;
import com.yoga.oneweather.city.OnCityClickListener;
import com.yoga.oneweather.city.ResultAdapter;
import com.yoga.oneweather.city.SearchViewAdapter;
import com.yoga.oneweather.model.db.CityDao;
import com.yoga.oneweather.model.db.DBManager;
import com.yoga.oneweather.util.LogUtil;
import com.yoga.oneweather.widget.SideLetterBar;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public AMapLocationClient mLocationClient;

    private RelativeLayout relativeLayout;
    private RecyclerView cityView;
    private List<CityDao> cityList;
    SearchViewAdapter searchViewAdapter;
    private RecyclerView resultView;
    private List<CityDao> resultList ;
    ResultAdapter resultAdapter;

    private SideLetterBar sideBar;
    private TextView overlay;

    private EditText mSearchText;
    private ImageButton mActionEmptyBtn;
    private ImageButton mActionBack;

    private static final String TAG = "SearchActivity";

    public static void actionStart(Context context){
        Intent intent = new Intent(context,SearchActivity.class);
        context.startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        relativeLayout = (RelativeLayout) findViewById(R.id.search_layout);
        cityView = (RecyclerView) findViewById(R.id.recyclerView);
        resultView = (RecyclerView) findViewById(R.id.search_result_view);
        sideBar = (SideLetterBar) findViewById(R.id.letter_side);
        overlay = (TextView) findViewById(R.id.letter_overlay);
        mSearchText = (EditText) findViewById(R.id.searchTextView);
        mActionEmptyBtn = (ImageButton) findViewById(R.id.action_empty_btn);
        mActionBack = (ImageButton) findViewById(R.id.action_back);



        initView();

        if(Build.VERSION.SDK_INT >= 21){
            relativeLayout.setFitsSystemWindows(true);//5.0以上的显示状态栏
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
        initLocation();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            requestLocation();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocation();
                } else {
                    Toast.makeText(this,"必须同意所有权限才能使用定位功能",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initLocation() {
        //高德地图
        mLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                    String city = aMapLocation.getCity();
                    String district = aMapLocation.getDistrict();
                    LogUtil.d(TAG, "onLocationChanged: district:"+district+"CityDao:"+city);
                    //区分是否是县
                    String location = district.endsWith("县")||district.endsWith("区") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
                    //重置定位状态
                    CityDao cityDao = DBManager.getInstance().findCity(location);
                    if (cityDao == null) {
                        cityDao = DBManager.getInstance().findCity(city.substring(0, city.length() - 1));
                    }

                    if (cityDao != null) {
                        searchViewAdapter.updateLocateState(SearchViewAdapter.LOCATE_SUCCESS, cityDao);
                    } else {
                        searchViewAdapter.updateLocateState(SearchViewAdapter.LOCATE_FAILED, null);
                    }
                } else {
                    searchViewAdapter.updateLocateState(SearchViewAdapter.LOCATE_FAILED, null);
                }
                searchViewAdapter.notifyItemChanged(0);
            }
        });
    }

    private void initView() {

        final OnCityClickListener listener = new OnCityClickListener() {
            @Override
            public void onCityClick(String cityId) {
                WeatherActivity.actionStart(SearchActivity.this, cityId);
            }
        };

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cityView.setLayoutManager(linearLayoutManager);
        cityList = DBManager.getInstance().getAllCities();

        searchViewAdapter = new SearchViewAdapter(cityList);
        searchViewAdapter.setOnCityClickListener(listener);
        searchViewAdapter.setOnLocateClickListener(new SearchViewAdapter.OnLocateClickListener() {
            @Override
            public void onLocateClick() {
                if(searchViewAdapter.getLocateState() == SearchViewAdapter.LOCATE_FAILED){
                    searchViewAdapter.updateLocateState(SearchViewAdapter.LOCATE_ING,null);
                    searchViewAdapter.notifyItemChanged(0);
                    requestLocation();
                }else if(searchViewAdapter.getLocateState() == SearchViewAdapter.LOCATE_SUCCESS){
                    listener.onCityClick(searchViewAdapter.getLocateCity().getCityId());
                }

            }
        });
        cityView.setAdapter(searchViewAdapter);

        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        resultView.setLayoutManager(resultLayoutManager);
        resultList = DBManager.getInstance().getSearchCities("");//test
        resultAdapter = new ResultAdapter(resultList);
        resultAdapter.setOnCityClickListener(listener);
        resultView.setAdapter(resultAdapter);

        sideBar.setOverlay(overlay);
        sideBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                if("定位".equals(letter) || "热门".equals(letter)){
                    linearLayoutManager.scrollToPosition(0);
                }else{
                    linearLayoutManager.scrollToPositionWithOffset(getLetterPosition(letter,cityList)+1,0);//自动将指定位置放到屏幕顶部，然后再计算偏移
                }

            }
        });

        initSerachView();


    }


    private void initSerachView() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence text = mSearchText.getText();
                if(!TextUtils.isEmpty(text)){
                    mActionEmptyBtn.setVisibility(View.VISIBLE);
                }else{
                    mActionEmptyBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String keyword = s.toString();
                if(TextUtils.isEmpty(keyword)){
                    resultView.setVisibility(View.GONE);
                    cityView.setVisibility(View.VISIBLE);
                }else{
                    mActionEmptyBtn.setVisibility(View.VISIBLE);
                    resultView.setVisibility(View.VISIBLE);


                    resultList.clear();//直接赋值不会改变，那一块内存区域并不会改变，而是会使指针指向另一块内存
                    resultList.addAll(DBManager.getInstance().getSearchCities(keyword));//查询内容
                    resultAdapter.notifyDataSetChanged();

                    cityView.setVisibility(View.GONE);
                }

            }
        });

        //设置点击事件
        mActionEmptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        mActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//结束当前活动
            }
        });
    }

    private int getLetterPosition(String letter, List<CityDao> dataList) {
        int i = 0;
        for (; i < dataList.size(); i++) {
            if (dataList.get(i).getmInitial().equals(letter)) {
                break;
            }
        }
        return i;
    }

    private void requestLocation(){
        mLocationClient.startLocation();
    }

}
