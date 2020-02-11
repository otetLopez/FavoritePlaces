package com.f20.favoriteplaces;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlaceAdapter extends ArrayAdapter {
    Context mContext;
    int layoutRes;
    List<Place> placeList;
    DatabaseHelper mDataBase;


    public PlaceAdapter(Context context, int resource, List<Place> places, DatabaseHelper mDatabase) {
        super(context, resource, places);
        this.mContext = context;
        this.layoutRes = resource;
        this.placeList = places;
        this.mDataBase = mDataBase;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(R.layout.layout_places, null);

        TextView tv_addr = v.findViewById(R.id.tv_addr);
        TextView tv_date = v.findViewById(R.id.tv_date);
        LinearLayout cell = v.findViewById(R.id.cell_place);

        final Place place = placeList.get(position);
        tv_addr.setText(place.getAddr());
        tv_date.setText("Date created: " + place.getDate());

        if(place.isVisited())
            cell.setBackgroundColor(Color.LTGRAY);
        else
            cell.setBackgroundColor(Color.parseColor("#FAFAFA"));



        return v;
    }


}
