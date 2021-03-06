package com.ctrl.newweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ctrlc on 2017/10/31.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Aqi aqi;
    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecas")
    public List<Forecast> forecastList;
}
