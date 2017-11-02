package com.ctrl.newweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.ctrl.newweather.db.City;
import com.ctrl.newweather.db.County;
import com.ctrl.newweather.db.Province;
import com.ctrl.newweather.gson.Forecast;
import com.ctrl.newweather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ctrlc on 2017/10/30.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String respnse) {
        if (!TextUtils.isEmpty(respnse)) {
            try {
               // Log.d("aaa",respnse.toString());
                JSONArray allProvince = new JSONArray(respnse);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceOnject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceOnject.getInt("id"));
                    province.setProvinceName(provinceOnject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String respnse,int provinceId) {
        if (!TextUtils.isEmpty(respnse)) {
            try {
                JSONArray allCity = new JSONArray(respnse);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject CitOnject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(CitOnject.getInt("id"));
                    city.setCityName(CitOnject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String respnse,int cityId) {
        if (!TextUtils.isEmpty(respnse)) {
            try {
                JSONArray allCounty = new JSONArray(respnse);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject CountyOnject = allCounty.getJSONObject(i);
                    County county = new County();
                    //Log.d("aaa", CountyOnject.getString("name"));
                    county.setCountyName(CountyOnject.getString("name"));
                    county.setWeatherId(CountyOnject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            JSONArray array = jsonArray.getJSONObject(0).getJSONArray("daily_forecast");
            Weather weather =  new Gson().fromJson(weatherContent,Weather.class);
            weather.forecastList = handleForecastResponse(array);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Forecast> handleForecastResponse(JSONArray forecasts){
        List<Forecast> forecastList = new ArrayList<>();
        Log.d("forecast0", forecasts.length()+"");
        for (int i = 0;i<forecasts.length();i++){
            try {
                JSONObject object = forecasts.getJSONObject(i);
                forecastList.add(new Gson().fromJson(object.toString(),Forecast.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return forecastList;
    }
}
