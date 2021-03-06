package com.ctrl.newweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ctrl.newweather.gson.Forecast;
import com.ctrl.newweather.gson.Weather;
import com.ctrl.newweather.util.HttpUtil;
import com.ctrl.newweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeTxt;

    private TextView weatherInfoTxt;

    private LinearLayout forecastLayout;

    private TextView aqiTxt;

    private TextView pm25Txt;

    private TextView comfortTxt;

    private TextView carWashTxt;

    private TextView sportTxt;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            /*View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);*/
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeTxt = (TextView) findViewById(R.id.degree_txt);
        weatherInfoTxt = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiTxt = (TextView) findViewById(R.id.aqi_txt);
        pm25Txt = (TextView) findViewById(R.id.pm25_txt);
        comfortTxt = (TextView) findViewById(R.id.comfort_txt);
        carWashTxt = (TextView) findViewById(R.id.car_wash_txt);
        sportTxt = (TextView) findViewById(R.id.sport_txt);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

    }
    public void requestWeather(final String weatherId){
        String address = "http://guolin.tech/api/weather?key=a0188c65ba3847e6b274240f022d479f&cityid="+weatherId;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.upadteTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeTxt.setText(degree);
        weatherInfoTxt.setText(weatherInfo);
        forecastLayout.removeAllViews();
        if(weather.forecastList!=null){
            for(Forecast forecast: weather.forecastList){
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout,false);
                TextView dateTxt = (TextView) view.findViewById(R.id.date_txt);
                TextView infoTxt = (TextView) view.findViewById(R.id.info_txt);
                TextView maxTxt = (TextView) view.findViewById(R.id.max_txt);
                TextView minTxt = (TextView) view.findViewById(R.id.min_txt);
                dateTxt.setText(forecast.date);
                infoTxt.setText(forecast.more.info);
                maxTxt.setText(forecast.temperature.max);
                minTxt.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
        }

        if(weather.aqi!= null){
            aqiTxt.setText(weather.aqi.city.aqi);
            pm25Txt.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        comfortTxt.setText(comfort);
        carWashTxt.setText(carWash);
        sportTxt.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        loadBingPic();
    }
    public void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        //Toast.makeText(this,requestBingPic,Toast.LENGTH_SHORT).show();
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this,"数据加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).placeholder(R.mipmap.default_bg).into(bingPicImg);
                    }
                });


            }
        });
    }
}
