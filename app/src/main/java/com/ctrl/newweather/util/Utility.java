package com.ctrl.newweather.util;

import android.text.TextUtils;

import com.ctrl.newweather.db.City;
import com.ctrl.newweather.db.County;
import com.ctrl.newweather.db.Province;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                JSONArray allProvince = new JSONArray(respnse);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceOnject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setId(provinceOnject.getInt("id"));
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
                    city.setId(CitOnject.getInt("id"));
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
                    county.setId(CountyOnject.getInt("id"));
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

}
