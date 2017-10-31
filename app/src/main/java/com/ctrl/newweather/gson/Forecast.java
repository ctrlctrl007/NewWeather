package com.ctrl.newweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ctrlc on 2017/10/31.
 */

public class Forecast {

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String max;
        public String min;
    }
    @SerializedName("cond")
    public More more;
    public class More{
        public String info;
    }

}
