package com.ctrl.newweather.gson;

import android.test.MoreAsserts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ctrlc on 2017/10/31.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
