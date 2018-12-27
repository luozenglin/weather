package com.luozenglin.myweather.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luozenglin.myweather.common.City;
import com.luozenglin.myweather.common.County;
import com.luozenglin.myweather.common.Province;
import com.luozenglin.myweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Utility {


    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String weatherID(Activity activity, String provinceName, String cityName, String countyName) {
        Province province = queryProvinces(activity,provinceName);
        Log.d("Utility","searched province:"+province.getProvinceName());
        City city = queryCities(activity,province,cityName);
        Log.d("Utility","searched city:"+city.getCityName());
        String weatherId =  queryCounties(activity,province,city,countyName);
        Log.d("Utility","searched city:"+weatherId);
        return weatherId;
    }

    private static Province queryProvinces(Activity activity, String provinceName) {
        List<Province> provinceList;
        Province theProvince = new Province();
        do {
            provinceList = DataSupport.findAll(Province.class);
            if (provinceList.size() > 0) {
                for (Province province : provinceList) {
                    if (province.getProvinceName().equals(provinceName)) {
                        theProvince = province;
                        break;
                    }
                }
            } else {
                String address = "http://guolin.tech/api/china";
                queryFromServer(activity, address, "province", null, null);
            }
        } while (provinceList.isEmpty());
        return theProvince;
    }

    private static City queryCities(Activity activity, Province province, String cityName) {
        List<City> cityList;
        City theCity = new City();
        do {
            cityList = DataSupport.where("provinceid = ?", String.valueOf(province.getId())).find(City.class);
            if (cityList.size() > 0) {
                for (City city : cityList) {
                    if (city.getCityName().equals(cityName)) {
                        theCity = city;
                        break;
                    }
                }
            } else {
                int provinceCode = province.getProvinceCode();
                String address = "http://guolin.tech/api/china/" + provinceCode;
                queryFromServer(activity, address, "city", province, null);
            }
        } while (cityList.isEmpty());
        return theCity;
    }


    private static String queryCounties(Activity activity, Province province, City city, String countyName) {
        List<County> countyList;
        County theCounty = new County();
        do {
            countyList = DataSupport.where("cityId = ?", String.valueOf(city.getId())).find(County.class);
            Log.d("Utility","select from database countyList:"+countyList);
            if (countyList.size() > 0) {
                for (County county : countyList) {
                    if (county.getCountyName().equals(countyName)) {
                        theCounty = county;
                        break;
                    }
                }
            } else {
                int provinceCode = province.getProvinceCode();
                int cityCode = city.getCityCode();
                String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
                Log.d("Utility","start to query county from server.city:"+city.getCityName());
                queryFromServer(activity, address, "county", province, city);
            }
        } while (countyList.isEmpty());
        return theCounty.getWeatherId();
    }

    private static void queryFromServer(final Activity activity, String address, final String type, final Province province, final City city) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, province.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, city.getId());
                }
            }
        });
    }
}
