package com.luozenglin.myweather;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.luozenglin.myweather.utils.Utility;

import java.util.ArrayList;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient;
    String myLocationProvince;
    String myLocationCity;
    String myLocationCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }



    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            myLocationProvince = location.getProvince();
            myLocationProvince = myLocationProvince.substring(0,myLocationProvince.length()-1);
            myLocationCity = location.getCity();
            myLocationCity = myLocationCity.substring(0,myLocationCity.length()-1);
            myLocationCounty = location.getDistrict();
            myLocationCounty = myLocationCounty.substring(0,myLocationCounty.length()-1);
            Log.d("MainActivity","my location:"+myLocationProvince+" "+myLocationCity+" "+myLocationCounty);
            updateProgress();
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(myLocationProvince!=null && myLocationCity !=null && myLocationCounty !=null){
                String weatherId = Utility.weatherID(MainActivity.this,myLocationProvince,myLocationCity,myLocationCounty);
                Log.d("MainActivity","weatherId:"+weatherId);
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("weather_id",weatherId);
                Log.d("MainActivity","will to WeatherActivity");
                startActivity(intent);
            }
            updateProgress();
            return true;
        }
    });


    private void updateProgress() {
        Message msg = Message.obtain();
        handler.sendMessageDelayed(msg, 500);
    }
}