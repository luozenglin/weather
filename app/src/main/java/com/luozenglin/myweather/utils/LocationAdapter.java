package com.luozenglin.myweather.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luozenglin.myweather.R;
import com.luozenglin.myweather.common.Location;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<Location> {
    private int resourceId;

    public LocationAdapter(Context context, int textViewResoutceId, List<Location> objects){
        super(context,textViewResoutceId,objects);
        resourceId = textViewResoutceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Location location = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView locationTV = (TextView) view.findViewById(R.id.location_tv);
        locationTV.setText(location.getCounty());
        return view;
    }
}
