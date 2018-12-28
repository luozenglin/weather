package com.luozenglin.myweather;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.luozenglin.myweather.common.Location;
import com.luozenglin.myweather.utils.LocationAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

public class CityManagerActivity extends AppCompatActivity {
    private Button backBtn;
    private Button addBtn;
    private ListView cityListView;
    private List<Location> locationList;
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        backBtn = (Button) findViewById(R.id.back_btn);
        addBtn = (Button) findViewById(R.id.add_city_btn);
        cityListView = (ListView) findViewById(R.id.city_manager_listView);
        refreshLayout();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CityManagerActivity","will to AddCityActivity");
                Intent intent = new Intent(CityManagerActivity.this,AddCityActivity.class);
                startActivity(intent);
            }
        });
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location location = locationList.get(position);
                Intent intent = new Intent(CityManagerActivity.this,WeatherActivity.class);
                intent.putExtra("weather_id",location.getWeatherId());
                startActivity(intent);
            }
        });
        cityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClick(position);
                return true;
            }
        });

    }


    protected void longClick(final int position) {
        final View popupView = getLayoutInflater().inflate(R.layout.delete_item, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.popupAnimation);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
        Button deleteButton = (Button) popupView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = locationList.get(position);
                DataSupport.deleteAll(Location.class,"weatherId = ?",location.getWeatherId());
                Log.d("CityManagerActivity","deleted city:"+location.getCounty());
                refreshLayout();
                popupWindow.dismiss();
            }
        });
    }

    protected void refreshLayout() {
        locationList = DataSupport.findAll(Location.class);
        for(Location location:locationList){
            Log.d("CityManagerActivity","location name:"+location.getCounty()+
                    " weatherId:"+location.getWeatherId());
        }
        LocationAdapter locationAdapter = new LocationAdapter(CityManagerActivity.this,
                R.layout.location_item_layout,locationList);
        cityListView.setAdapter(locationAdapter);
    }

}
