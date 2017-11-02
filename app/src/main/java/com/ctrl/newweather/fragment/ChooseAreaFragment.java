package com.ctrl.newweather.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrl.newweather.R;
import com.ctrl.newweather.WeatherActivity;
import com.ctrl.newweather.db.City;
import com.ctrl.newweather.db.County;
import com.ctrl.newweather.db.Province;
import com.ctrl.newweather.util.HttpUtil;
import com.ctrl.newweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ctrlc on 2017/10/30.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int  LEVEL_PROVINCE = 0;
    public static final int  LEVEL_CITY = 1;
    public static final int  LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titletext;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    private Province selectedProvice;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.choose_area,container,false);
        titletext = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvice = provinces.get(i);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cities.get(i);
                    queryCounty();
                }else if(currentLevel == LEVEL_COUNTY){
                    selectedCounty = counties.get(i);
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",selectedCounty.getWeatherId());
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel ==LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel ==LEVEL_COUNTY){
                    queryCities();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince(){
        titletext.setText("中国");
        backButton.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);
        if(provinces.size()>0){
            dataList.clear();
            for(Province province: provinces){
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        //数据库无数据从服务器查询
        else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities(){
        titletext.setText(selectedProvice.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid  = ?", String.valueOf(selectedProvice.getId())).find(City.class);
        if(cities.size()>0){
            dataList.clear();
            for(City city : cities){
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int princeCode = selectedProvice.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+princeCode;
            queryFromServer(address,"city");
        }
    }

    private void queryCounty(){
        titletext.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(counties.size()>0){
            dataList.clear();
            for(County county : counties){
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int princeCode = selectedProvice.getProvinceCode();
            int cityCOde = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+princeCode+"/"+cityCOde;
            queryFromServer(address,"county");
        }
    }



    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"加载失败",  Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvice.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
