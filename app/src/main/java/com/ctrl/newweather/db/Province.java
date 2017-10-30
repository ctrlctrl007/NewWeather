package com.ctrl.newweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ctrlc on 2017/10/30.
 */

public class Province extends DataSupport {

    private int id;
    private String ProvinceName;
    private int provinceCode;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }



    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }



    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
