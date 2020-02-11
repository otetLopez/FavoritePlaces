package com.f20.favoriteplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class GetDirectionData extends AsyncTask<Object, String, String> {

    String directionData;
    GoogleMap mMap;
    String url;

    String distance;
    String duration;
    LatLng latLng;

    String address;
    Polyline polyline;
    Context context;

    GetDirectionData(Context context, String address) {
        this.context = context;
        this.address = address;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        FetchUrl fetchUrl = new FetchUrl();
        try {
            directionData = fetchUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directionData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String > distanceData = null;
        DataParser distanceParser = new DataParser();
        distanceData = distanceParser.parseDistance(s);
        Log.i("GetDirectionData", "onPostExecute: " + "trying to get parsed data");

        distance = distanceData.get("distance");
        duration = distanceData.get("duration");

        Log.i("MapsActivity", "onPostExecute: " + "Done clearing map");
        //Create a new marker with new title and snippet
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(this.address)
                .snippet("Distance: " + distance + ", Duration: " + duration)
                .icon(bitmapDescriptorFromVector(this.context, R.drawable.destination));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
        mMap.addMarker(options);

        Log.i("MapsActivity", "onPostExecute: " + "Done adding marker");
        //if (MapsActivity.directionRequested) {
            String[] directionList;
            DataParser directionParser = new DataParser();
            directionList = directionParser.parseDirections(s);
            displayDirections(directionList);
       // }
        //MapsActivity.reSetHomeMarker();

    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i =0; i<count; ++i) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            this.polyline = mMap.addPolyline(options);

        }
        Log.i("MapsActivity", "displayDirections: " + "Done adding polyline");
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorDrawableResourceId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
